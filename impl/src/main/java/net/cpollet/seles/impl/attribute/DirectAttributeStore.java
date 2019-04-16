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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class DirectAttributeStore<T extends Id> implements AttributeStore<T> {
    private final Map<String, AttributeDef<T>> attributes;
    private final AttributeDef<T> idAttribute;

    public DirectAttributeStore(String idAttribute, Collection<AttributeDef<T>> attributes) {
        this.attributes = Collections.unmodifiableMap(
                attributes.stream()
                        .collect(Collectors.toMap(
                                AttributeDef::name,
                                a -> a
                        ))
        );
        this.idAttribute = attributes.isEmpty() ? null : this.attributes.get(idAttribute);
    }

    public DirectAttributeStore(Collection<AttributeDef<T>> attributes) {
        this("", attributes);
    }

    @Override
    public Optional<AttributeDef<T>> fetch(String attributeName) {
        return Optional.ofNullable(attributes.get(attributeName));
    }

    @Override
    public Optional<AttributeDef<T>> idAttribute() {
        return Optional.ofNullable(idAttribute);
    }

    @Override
    public Collection<AttributeDef<T>> attributes() {
        return attributes.values();
    }
}
