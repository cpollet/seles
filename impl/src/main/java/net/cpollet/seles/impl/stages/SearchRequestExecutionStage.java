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
import net.cpollet.seles.api.methods.SearchResult;
import net.cpollet.seles.impl.attribute.AttributesGrouper;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Executes a SEARCH {@link InternalRequest}.
 */
public final class SearchRequestExecutionStage<T extends Id> implements Stage<T, AttributeDef<T>> {
    @Override
    public InternalResponse<T, AttributeDef<T>> execute(InternalRequest<T, AttributeDef<T>> request) {
        SearchResult<T> searchResult = request.attributes(new AttributesGrouper<>()).entrySet().stream()
                .map(e -> e.getKey().search(request.principal(), request.values(e.getValue())))
                .reduce(SearchResult.emptyResult(), SearchResult::merge);

        return new InternalResponse<T, AttributeDef<T>>(
                searchResult.ids().stream()
                        .collect(Collectors.toMap(
                                i -> i,
                                i -> Collections.emptyMap()
                        ))
        )
                .withErrors(
                        searchResult.errors()
                );
    }
}
