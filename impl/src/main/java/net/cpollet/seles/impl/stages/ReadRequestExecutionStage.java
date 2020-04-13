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
package net.cpollet.seles.impl.stages;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.api.methods.FetchResult;
import net.cpollet.seles.api.methods.Method;
import net.cpollet.seles.impl.attribute.AttributesGrouper;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Executes a READ {@link InternalRequest}.
 */
public final class ReadRequestExecutionStage implements Stage<AttributeDef> {
    @Override
    public InternalResponse<AttributeDef> execute(InternalRequest<AttributeDef> request) {
        FetchResult fetchResult = fetch(
                request.principal(),
                request.ids(),
                request.attributes(new AttributesGrouper())
        );

        return new InternalResponse<>(fetchResult.result())
                .withErrors(fetchResult.errors());
    }

    private FetchResult fetch(Principal principal, Collection<Id> ids, Map<Method<Id>, List<AttributeDef>> attributesGroupedByMethod) {
        return attributesGroupedByMethod.entrySet().stream()
                .map(e -> e.getKey().fetch(principal, e.getValue(), ids))
                .reduce(FetchResult.emptyResult(), FetchResult::merge);
    }
}
