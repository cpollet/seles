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
        Context baseContext = new Context(
                AttributeDef.Mode.READ,
                attributeStore,
                idValidator,
                filteringPredicate,
                guard,
                Arrays.asList(AttributeDef::converter, AttributeDef::caster)
        );
        this.readStack = StackBuilder.build(
                baseContext,
                Arrays.asList(
                        TimerStage.class,
                        ExpandStarStage.class,
                        AttributeConversionStage.class,
                        ModeValidationStage.class,
                        LogDeprecatedStage.class,
                        FilteringStage.class,
                        IdsValidationStage.class,
                        ValueConversionStage.class,
                        ReadRequestExecutionStage.class
                )
        );
        this.updateStack = StackBuilder.build(
                baseContext.withMode(AttributeDef.Mode.WRITE),
                Arrays.asList(
                        TimerStage.class,
                        AttributeConversionStage.class,
                        ModeValidationStage.class,
                        LogDeprecatedStage.class,
                        FilteringStage.class,
                        IdsValidationStage.class,
                        ValueConversionStage.class,
                        UpdateRequestExecutionStage.class,
                        ReadRequestExecutionStage.class
                )
        );
        this.deleteStack = StackBuilder.build(
                baseContext.withMode(AttributeDef.Mode.DELETE),
                Arrays.asList(
                        TimerStage.class,
                        AttributeConversionStage.class,
                        ModeValidationStage.class,
                        LogDeprecatedStage.class,
                        FilteringStage.class,
                        IdsValidationStage.class,
                        DeleteRequestExecutionStage.class
                )
        );
        this.createStack = StackBuilder.build(
                baseContext.withMode(AttributeDef.Mode.WRITE),
                Arrays.asList(
                        TimerStage.class,
                        AttributeConversionStage.class,
                        ModeValidationStage.class,
                        LogDeprecatedStage.class,
                        FilteringStage.class,
                        ValueConversionStage.class,
                        CreateRequestExecutionStage.class,
                        ReadRequestExecutionStage.class
                )
        );
        this.searchStack = StackBuilder.build(
                baseContext.withMode(AttributeDef.Mode.SEARCH),
                Arrays.asList(
                        TimerStage.class,
                        AttributeConversionStage.class,
                        ModeValidationStage.class,
                        LogDeprecatedStage.class,
                        FilteringStage.class,
                        IdsValidationStage.class,
                        ValueConversionStage.class,
                        SearchRequestExecutionStage.class
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
