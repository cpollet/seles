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
import net.cpollet.seles.api.methods.CreateResult;
import net.cpollet.seles.api.methods.FetchResult;
import net.cpollet.seles.api.methods.Method;
import net.cpollet.seles.api.methods.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class StandardMethod implements Method<Id> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StandardMethod.class);

    private final Function<Object, Id> idTypeProvider;

    public StandardMethod(Function<Object, Id> idTypeProvider) {
        this.idTypeProvider = idTypeProvider;
    }

    @Override
    public FetchResult fetch(Principal principal, List<AttributeDef> attributes, Collection<Id> ids) {
        return new FetchResult(
                ids.stream()
                        .collect(Collectors.toMap(
                                id -> id,
                                id -> attributes.stream()
                                        .collect(Collectors.toMap(
                                                a -> a,
                                                a -> a.name() + ":" + id.get()
                                        ))
                                )

                        )
        );
    }

    @Override
    public Collection<String> update(Principal principal, Map<AttributeDef, Object> attributeValues, Collection<Id> ids) {
        ids.forEach(
                id -> attributeValues.forEach((a, v) -> LOGGER.info("UPDATE {}:{} -> {}", id, a, v))
        );

        return Collections.singletonList("update error");
    }

    @Override
    public Collection<String> delete(Principal principal, List<AttributeDef> attributes, Collection<Id> ids) {
        ids.forEach(
                id -> attributes.forEach(a -> LOGGER.info("DELETE {}:{}", id, a))
        );

        return Collections.singletonList("delete error");
    }

    @Override
    public CreateResult create(Principal principal, Map<AttributeDef, Object> values) {
        values.forEach(
                (a, v) -> LOGGER.info("CREATE {} -> {}", a, v)
        );

        return new CreateResult(idTypeProvider.apply("100000"));
    }

    @Override
    public SearchResult search(Principal principal, Map<AttributeDef, Object> values) {
        values.forEach(
                (a, v) -> LOGGER.info("SEARCH {} -> {}", a, v)
        );

        return new SearchResult(
                Arrays.asList(idTypeProvider.apply("100000"), idTypeProvider.apply("222222"))
        );
    }
}
