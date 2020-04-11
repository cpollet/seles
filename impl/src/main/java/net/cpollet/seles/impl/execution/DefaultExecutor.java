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

// FIXME deserves a refactoring
public final class DefaultExecutor implements Executor<Id> {
    private final AttributeStore attributeStore;
    private final IdValidator idValidator;
    private final AccessLevelPredicate filteringPredicate;
    private final DefaultExecutorGuard guard;

    private final Stage<String> readStack;
    private final Stage<String> updateStack;
    private final Stage<String> deleteStack;
    private final Stage<String> createStack;
    private final Stage<String> searchStack;

    public DefaultExecutor(AttributeStore attributeStore, IdValidator idValidator, AccessLevelPredicate filteringPredicate, DefaultExecutorGuard guard) {
        this.attributeStore = attributeStore;
        this.idValidator = idValidator;
        this.filteringPredicate = filteringPredicate;
        this.guard = guard;
        this.readStack =
                new TimerStage(
                        new ExpandStarStage(
                                attributeStore,
                                validateIdAndConvertValues(
                                        AttributeDef.Mode.READ,
                                        new ReadRequestExecutionStage()
                                )
                        )
                );
        this.updateStack =
                new TimerStage(
                        validateIdAndConvertValues(
                                AttributeDef.Mode.WRITE,
                                new UpdateRequestExecutionStage(
                                        new RequestHaltStage<>(
                                                req -> guard.haltDueToUpdateError(req.hasGuardFlag(Guarded.Flag.UPDATE_ERROR)),
                                                new ReadRequestExecutionStage()
                                        )
                                )
                        )
                );
        this.deleteStack =
                new TimerStage(
                        validateId(
                                AttributeDef.Mode.DELETE,
                                new DeleteRequestExecutionStage()
                        )
                );
        this.createStack =
                new TimerStage(
                        prepareRequest(
                                AttributeDef.Mode.WRITE,
                                new CreateRequestExecutionStage(
                                        attributeStore,
                                        new UpdateRequestExecutionStage(
                                                new EmptyResponseStage<>()
                                        ),
                                        new RequestHaltStage<>(
                                                req -> guard.haltDueToUpdateError(req.hasGuardFlag(Guarded.Flag.UPDATE_ERROR)),
                                                new ReadRequestExecutionStage()
                                        )
                                )
                        )
                );
        this.searchStack =
                new TimerStage(
                        validateIdAndConvertValues(
                                AttributeDef.Mode.SEARCH,
                                new SearchRequestExecutionStage()
                        )
                );
    }

    /**
     * Stages used for READ, WRITE and SEARCH requests.
     */
    private Stage<String> validateIdAndConvertValues(AttributeDef.Mode mode, Stage<AttributeDef> inner) {
        return validateId(
                mode,
                new ValueConversionStage(
                        AttributeDef::caster,
                        new ValueConversionStage(
                                AttributeDef::converter,
                                new RequestHaltStage<>(
                                        req -> guard.haltDueToInputValueConversionError(req.hasGuardFlag(Guarded.Flag.INPUT_VALUE_CONVERSION_ERROR)),
                                        inner
                                )
                        )
                )
        );
    }

    /**
     * Stages used for READ, WRITE, DELETE and SEARCH requests.
     */
    private Stage<String> validateId(AttributeDef.Mode mode, Stage<AttributeDef> inner) {
        return prepareRequest(
                mode,
                new IdsValidationStage(
                        idValidator,
                        new RequestHaltStage<>(
                                req -> guard.haltDueToIdValidationError(req.hasGuardFlag(Guarded.Flag.INVALID_IDS)),
                                inner
                        )
                )
        );
    }

    /**
     * Stages used for READ, WRITE, DELETE, CREATE and SEARCH requests.
     */
    private Stage<String> prepareRequest(AttributeDef.Mode mode, Stage<AttributeDef> inner) {
        return new AttributeConversionStage(
                attributeStore,
                new RequestHaltStage<>(
                        req -> guard.haltDueToAttributeConversionError(req.hasGuardFlag(Guarded.Flag.ATTRIBUTE_CONVERSION_ERROR)),
                        new ModeValidationStage(
                                mode,
                                new RequestHaltStage<>(
                                        req -> guard.haltDueToModeError(req.hasGuardFlag(Guarded.Flag.INVALID_MODE)),
                                        new LogDeprecatedStage(
                                                new FilteringStage(
                                                        filteringPredicate,
                                                        inner
                                                )
                                        )
                                )
                        )
                )
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
