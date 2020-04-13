/*
 * Copyright 2019 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cpollet.seles.impl.execution;

import net.cpollet.seles.api.attribute.AccessLevelPredicate;
import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.attribute.AttributeStore;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.api.domain.IdValidator;
import net.cpollet.seles.api.execution.Executor;
import net.cpollet.seles.api.execution.Request;
import net.cpollet.seles.api.execution.Response;
import net.cpollet.seles.impl.Guarded;
import net.cpollet.seles.impl.stages.AttributeConversionStage;
import net.cpollet.seles.impl.stages.CreateRequestExecutionStage;
import net.cpollet.seles.impl.stages.DeleteRequestExecutionStage;
import net.cpollet.seles.impl.stages.EmptyResponseStage;
import net.cpollet.seles.impl.stages.ExpandStarStage;
import net.cpollet.seles.impl.stages.FilteringStage;
import net.cpollet.seles.impl.stages.IdsValidationStage;
import net.cpollet.seles.impl.stages.LogDeprecatedStage;
import net.cpollet.seles.impl.stages.ModeValidationStage;
import net.cpollet.seles.impl.stages.ReadRequestExecutionStage;
import net.cpollet.seles.impl.stages.RequestHaltStage;
import net.cpollet.seles.impl.stages.SearchRequestExecutionStage;
import net.cpollet.seles.impl.stages.Stage;
import net.cpollet.seles.impl.stages.TimerStage;
import net.cpollet.seles.impl.stages.UpdateRequestExecutionStage;
import net.cpollet.seles.impl.stages.ValueConversionStage;

import java.util.Arrays;

// FIXME deserves a refactoring
public final class DefaultExecutor implements Executor<Id> {
    private final AttributeStore attributeStore;

    private final Stage<String> readStack;
    private final Stage<String> updateStack;
    private final Stage<String> deleteStack;
    private final Stage<String> createStack;
    private final Stage<String> searchStack;

    public DefaultExecutor(AttributeStore attributeStore, IdValidator idValidator, AccessLevelPredicate filteringPredicate, DefaultExecutorGuard guard) {
        this.attributeStore = attributeStore;

        Context readContext = new Context(
                AttributeDef.Mode.READ,
                attributeStore,
                idValidator,
                filteringPredicate,
                guard,
                Arrays.asList(AttributeDef::converter, AttributeDef::caster)
        );
        this.readStack =
                new TimerStage(
                        new ExpandStarStage(
                                validateIdAndConvertValues(
                                        new ReadRequestExecutionStage(), readContext
                                ), readContext
                        )
                );

        Context writeContext = new Context(
                AttributeDef.Mode.WRITE,
                attributeStore,
                idValidator,
                filteringPredicate,
                guard,
                Arrays.asList(AttributeDef::converter, AttributeDef::caster)
        );
        this.updateStack =
                new TimerStage(
                        validateIdAndConvertValues(
                                new UpdateRequestExecutionStage(
                                        new RequestHaltStage<>(
                                                new ReadRequestExecutionStage(),
                                                req -> writeContext.guard.haltDueToUpdateError(req.hasGuardFlag(Guarded.Flag.UPDATE_ERROR))
                                        )
                                ), writeContext
                        )
                );

        Context deleteContext = new Context(
                AttributeDef.Mode.DELETE,
                attributeStore,
                idValidator,
                filteringPredicate,
                guard,
                Arrays.asList(AttributeDef::converter, AttributeDef::caster)
        );
        this.deleteStack =
                new TimerStage(
                        validateId(
                                new DeleteRequestExecutionStage(), deleteContext
                        )
                );

        this.createStack =
                new TimerStage(
                        prepareRequest(
                                new CreateRequestExecutionStage(
                                        new UpdateRequestExecutionStage(
                                                new EmptyResponseStage<>()
                                        ),
                                        new RequestHaltStage<>(
                                                new ReadRequestExecutionStage(),
                                                req -> guard.haltDueToUpdateError(req.hasGuardFlag(Guarded.Flag.UPDATE_ERROR))
                                        ),
                                        writeContext
                                ), writeContext
                        )
                );

        Context searchContext = new Context(
                AttributeDef.Mode.SEARCH,
                attributeStore,
                idValidator,
                filteringPredicate,
                guard,
                Arrays.asList(AttributeDef::converter, AttributeDef::caster)
        );
        this.searchStack =
                new TimerStage(
                        validateIdAndConvertValues(
                                new SearchRequestExecutionStage(), searchContext
                        )
                );
    }

    /**
     * Stages used for READ, WRITE and SEARCH requests.
     */
    private Stage<String> validateIdAndConvertValues(Stage<AttributeDef> inner, Context context) {
        return validateId(
                new ValueConversionStage(
                        new RequestHaltStage<>(
                                inner,
                                req -> context.guard.haltDueToInputValueConversionError(req.hasGuardFlag(Guarded.Flag.INPUT_VALUE_CONVERSION_ERROR))
                        ), context
                ), context
        );
    }

    /**
     * Stages used for READ, WRITE, DELETE and SEARCH requests.
     */
    private Stage<String> validateId(Stage<AttributeDef> inner, Context context) {
        return prepareRequest(
                new IdsValidationStage(
                        new RequestHaltStage<>(
                                inner,
                                req -> context.guard.haltDueToIdValidationError(req.hasGuardFlag(Guarded.Flag.INVALID_IDS))
                        ),
                        context
                ), context
        );
    }

    /**
     * Stages used for READ, WRITE, DELETE, CREATE and SEARCH requests.
     */
    private Stage<String> prepareRequest(Stage<AttributeDef> inner, Context context) {
        return new AttributeConversionStage(
                new RequestHaltStage<>(
                        new ModeValidationStage(
                                new RequestHaltStage<>(
                                        new LogDeprecatedStage(
                                                new FilteringStage(
                                                        inner, context
                                                )
                                        ),
                                        req -> context.guard.haltDueToModeError(req.hasGuardFlag(Guarded.Flag.INVALID_MODE))
                                ), context
                        ),
                        req -> context.guard.haltDueToAttributeConversionError(req.hasGuardFlag(Guarded.Flag.ATTRIBUTE_CONVERSION_ERROR))
                ), context
        );
    }

    @Override
    public AttributeStore attributeStore() {
        return attributeStore;
    }

    @Override
    public Response<Id> read(Request<Id> request) {
        return InternalResponse.unwrap(
                readStack.execute(
                        InternalRequest.wrap(InternalRequest.RequestType.READ, request)
                )
        );
    }

    @Override
    public Response<Id> update(Request<Id> request) {
        return InternalResponse.unwrap(
                updateStack.execute(
                        InternalRequest.wrap(InternalRequest.RequestType.UPDATE, request)
                )
        );
    }

    @Override
    public Response<Id> create(Request<Id> request) {
        return InternalResponse.unwrap(
                createStack.execute(
                        InternalRequest.wrap(InternalRequest.RequestType.CREATE, request)
                )
        );
    }

    @Override
    public Response<Id> delete(Request<Id> request) {
        return InternalResponse.unwrap(
                deleteStack.execute(
                        InternalRequest.wrap(InternalRequest.RequestType.DELETE, request)
                )
        );
    }

    @Override
    public Response<Id> search(Request<Id> request) {
        return InternalResponse.unwrap(
                searchStack.execute(
                        InternalRequest.wrap(InternalRequest.RequestType.SEARCH, request)
                )
        );
    }
}
