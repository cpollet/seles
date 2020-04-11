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
package net.cpollet.seles.impl.attribute;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.attribute.AttributeStore;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.api.execution.Executor;
import net.cpollet.seles.impl.conversion.NoopValueConverter;
import net.cpollet.seles.impl.methods.NestedMethod;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class NestedAttributeStore implements AttributeStore {
    private final Map<String, AttributeDef> store;
    private final AttributeStore parentStore;

    public NestedAttributeStore(AttributeStore parentStore, Collection<NestedAttributes> attributes) {
        HashMap<String, AttributeDef> tmpStore = new HashMap<>(
                parentStore.attributes().stream()
                        .collect(
                                Collectors.toMap(
                                        AttributeDef::name,
                                        a -> a
                                )
                        )
        );

        attributes.forEach(
                a -> {
                    NestedMethod method = new NestedMethod(
                            a.prefix,
                            parentStore.fetch(a.attribute).orElseThrow(IllegalArgumentException::new),
                            a.executor,
                            a.idProvider
                    );
                    a.executor.attributeStore().attributes().forEach(
                            na -> {
                                String attributeName = String.format("%s.%s", a.prefix, na.name());

                                if (tmpStore.containsKey(attributeName)) {
                                    throw new IllegalStateException(String.format("Attribute [%s] already exists", attributeName));
                                }

                                tmpStore.put(
                                        attributeName,
                                        new AttributeDef(
                                                attributeName,
                                                na.accessLevel(),
                                                na.deprecated(),
                                                method,
                                                na.modes(),
                                                NoopValueConverter.instance(),
                                                NoopValueConverter.instance()
                                        )
                                );
                            }
                    );

                }
        );

        this.store = Collections.unmodifiableMap(tmpStore);
        this.parentStore = parentStore;
    }

    @Override
    public Optional<AttributeDef> fetch(String attributeName) {
        return Optional.ofNullable(store.get(attributeName));
    }

    @Override
    public Optional<AttributeDef> idAttribute() {
        return parentStore.idAttribute();
    }

    @Override
    public Collection<AttributeDef> attributes() {
        return store.values();
    }

    public static class NestedAttributes {
        private final String prefix;
        private final String attribute;
        private final Executor<Id> executor;
        private final Function<Object, Id> idProvider;

        public NestedAttributes(String prefix, String attribute, Executor<Id> executor, Function<Object, Id> idProvider) {
            this.prefix = prefix;
            this.attribute = attribute;
            this.executor = executor;
            this.idProvider = idProvider;
        }
    }
}
