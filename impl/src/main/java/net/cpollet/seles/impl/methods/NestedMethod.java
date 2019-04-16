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
package net.cpollet.seles.impl.methods;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.api.execution.Executor;
import net.cpollet.seles.api.execution.Request;
import net.cpollet.seles.api.execution.Response;
import net.cpollet.seles.api.methods.CreateResult;
import net.cpollet.seles.api.methods.FetchResult;
import net.cpollet.seles.api.methods.Method;
import net.cpollet.seles.api.methods.SearchResult;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class NestedMethod<T extends Id, U extends Id> implements Method<T> {
    private final String prefix;
    private final AttributeDef<T> attribute;
    private final Executor<U> executor;
    private final Function<Object, U> idProvider;

    public NestedMethod(String prefix, AttributeDef<T> attribute, Executor<U> executor, Function<Object, U> idProvider) {
        this.prefix = prefix;
        this.attribute = attribute;
        this.executor = executor;
        this.idProvider = idProvider;
    }

    @Override
    public FetchResult<T> fetch(Principal principal, List<AttributeDef<T>> attributes, Collection<T> ids) {
        Map<U, T> nestedIdsToIds = attribute.method().fetch(principal, Collections.singletonList(attribute), ids)
                .result().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> idProvider.apply(e.getValue().get(attribute)),
                        Map.Entry::getKey
                ));

        Map<String, AttributeDef<T>> attributeNamesToAttributeDefs = attributes.stream()
                .collect(Collectors.toMap(
                        a -> removePrefix(a.name()),
                        a -> a
                ));

        Response<U> response = nestedFetch(principal, attributeNamesToAttributeDefs.keySet(), nestedIdsToIds.keySet());

        Map<T, Map<AttributeDef<T>, Object>> result = response.values().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> nestedIdsToIds.get(e.getKey()),
                        e -> e.getValue().entrySet().stream()
                                .collect(Collectors.toMap(
                                        v -> attributeNamesToAttributeDefs.get(v.getKey()),
                                        Map.Entry::getValue
                                ))
                ));

        return new FetchResult<>(
                result,
                response.errors().stream()
                        .map(e -> String.format("[%s].%s", prefix, e))
                        .collect(Collectors.toSet())
        );
    }

    private String removePrefix(String string) {
        return string.substring(prefix.length() + 1);
    }

    private Response<U> nestedFetch(Principal principal, Collection<String> attributes, Collection<U> nestedIds) {
        return executor.read(Request.read(
                principal,
                nestedIds,
                attributes
        ));
    }

    @Override
    public Collection<String> update(Principal principal, Map<AttributeDef<T>, Object> attributeValues, Collection<T> ids) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Collection<String> delete(Principal principal, List<AttributeDef<T>> attributes, Collection<T> ids) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public CreateResult<T> create(Principal principal, Map<AttributeDef<T>, Object> values) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public SearchResult<T> search(Principal principal, Map<AttributeDef<T>, Object> values) {
        throw new RuntimeException("not implemented");
    }
}
