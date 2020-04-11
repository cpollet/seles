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
package net.cpollet.seles.api.execution;

import net.cpollet.seles.api.domain.Id;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Represents a request. The instance is immutable and once created, the ids and attributes collections are wrapped
 * in an unmodifiable collection.
 */
public final class Request<T extends Id> {  
    private final Collection<Id> ids;
    private final Collection<String> attributes;
    private final Map<String, Object> attributesValues;
    private final Principal principal; // FIXME make immutable

    private Request(Principal principal, Collection<T> ids, Collection<String> attributes, Map<String, Object> attributesValues) {
        this.principal = principal;
        this.ids = Collections.unmodifiableCollection(ids);
        this.attributes = Collections.unmodifiableCollection(attributes);
        this.attributesValues = Collections.unmodifiableMap(attributesValues);
    }

    public static <T extends Id> Request<T> read(Principal principal, Collection<T> ids, Collection<String> attributes) {
        return new Request<>(principal, ids, attributes, Collections.emptyMap());
    }

    public static <T extends Id> Request<T> delete(Principal principal, Collection<T> ids, Collection<String> attributes) {
        return new Request<>(principal, ids, attributes, Collections.emptyMap());
    }

    public static <T extends Id> Request<T> write(Principal principal, Collection<T> ids, Map<String, Object> attributesValues) {
        return new Request<>(principal, ids, attributesValues.keySet(), attributesValues);
    }

    public static <T extends Id> Request<T> create(Principal principal, Map<String, Object> attributesValues) {
        return new Request<>(principal, Collections.emptyList(), attributesValues.keySet(), attributesValues);
    }

    public static <T extends Id> Request<T> search(Principal principal, Map<String, Object> attributesValues) {
        return new Request<>(principal, Collections.emptyList(), attributesValues.keySet(), attributesValues);
    }

    public Collection<Id> getIds() {
        return ids;
    }

    public Collection<String> getAttributes() {
        return attributes;
    }

    public Map<String, Object> getAttributesValues() {
        return attributesValues;
    }

    public Principal principal() {
        return principal;
    }
}
