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

import net.cpollet.seles.api.conversion.ConversionException;
import net.cpollet.seles.api.conversion.ValueConverter;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.api.execution.Request;
import net.cpollet.seles.impl.Guarded;
import net.cpollet.seles.impl.conversion.ConversionResult;
import net.cpollet.seles.impl.data.BiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents a request, as used and transformed from {@link net.cpollet.seles.impl.stages.Stage} to
 * {@link net.cpollet.seles.impl.stages.Stage} instances. Instances are immutable.
 *
 * @param <T> the type of the entity's ID, an implementation of {@link Id}
 * @param <A> the type of the attribute; usually {@link String} or {@link net.cpollet.seles.api.attribute.AttributeDef}
 */
public final class InternalRequest<T extends Id, A> implements Guarded<InternalRequest<T, A>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternalRequest.class);

    private final Set<Flag> guardFlags;
    private final Set<T> ids;
    private final Set<A> attributes;
    private final Map<A, Object> attributeValues;
    private final RequestType type;
    private final Principal principal;

    public enum RequestType { // FIXME transform into different classes
        READ, UPDATE, DELETE, CREATE, SEARCH;
    }

    private InternalRequest(RequestType type, Principal principal, Set<T> ids, Set<A> attributes, Map<A, Object> attributesValues, Set<Flag> guardFlags) {
        this.type = type;
        this.principal = principal;
        this.ids = Collections.unmodifiableSet(ids);
        this.attributes = Collections.unmodifiableSet(attributes);
        this.attributeValues = Collections.unmodifiableMap(attributesValues);
        this.guardFlags = Collections.unmodifiableSet(guardFlags);
    }

    static <T extends Id> InternalRequest<T, String> wrap(RequestType type, Request<T> request) {
        return new InternalRequest<>(
                type,
                request.principal(),
                new HashSet<>(request.getIds()),
                new HashSet<>(request.getAttributes()),
                request.getAttributesValues(),
                Collections.emptySet()
        );
    }

    public InternalRequest<T, A> withIds(Collection<T> idsToAdd) {
        Set<T> newIds = new HashSet<>(ids);
        newIds.addAll(idsToAdd);
        return new InternalRequest<>(type, principal, newIds, attributes, attributeValues, guardFlags);
    }

    public InternalRequest<T, A> withoutIds(Collection<T> idsToRemove) {
        Set<T> newIds = new HashSet<>(ids);
        newIds.removeAll(idsToRemove);
        return new InternalRequest<>(type, principal, newIds, attributes, attributeValues, guardFlags);
    }

    public InternalRequest<T, A> withAttributes(Collection<A> attributesToAdd) {
        if (!is(RequestType.READ)) {
            throw new IllegalStateException("Cannot add attributes to a non READ request");
        }

        Set<A> newAttributes = new HashSet<>(attributes);
        newAttributes.addAll(attributesToAdd);
        return new InternalRequest<>(type, principal, ids, newAttributes, attributeValues, guardFlags);
    }

    public InternalRequest<T, A> withoutAttributes(Collection<A> attributesToRemove) {
        Set<A> newAttributes = new HashSet<>(attributes);
        newAttributes.removeAll(attributesToRemove);

        Map<A, Object> newAttributesValues = new HashMap<>(attributeValues);
        attributesToRemove.forEach(newAttributesValues::remove);

        return new InternalRequest<>(type, principal, ids, newAttributes, newAttributesValues, guardFlags);
    }

    /**
     * Transforms a request with attributes of type A to a request with attributes of type B. Usually, this means
     * transforming a request with attributes names ({@link String}) to attribute descriptors
     * ({@link net.cpollet.seles.api.attribute.AttributeDef}).
     *
     * @param conversionMap the map used to converts from A to B and vice versa.
     * @param <B>           destination attribute type
     * @return the request, with attributes mapped from type A to type B
     * @see InternalResponse#mapAttributes(BiMap) for the reverse operation
     */
    public <B> InternalRequest<T, B> mapAttributes(BiMap<B, A> conversionMap) {
        Set<B> newAttributes = attributes.stream()
                .map(conversionMap::getLeft)
                .collect(Collectors.toSet());

        Map<B, Object> newAttributeValues = attributeValues.keySet().stream()
                .collect(Collectors.toMap(
                        conversionMap::getLeft,
                        attributeValues::get
                ));

        return new InternalRequest<>(type, principal, ids, newAttributes, newAttributeValues, guardFlags);
    }

    public ConversionResult<InternalRequest<T, A>> convertValues(Map<A, ValueConverter<A>> converters) {
        if (!is(RequestType.UPDATE, RequestType.CREATE)) {
            return new ConversionResult<>(this);
        }

        Map<A, Object> convertedAttributeValues = new HashMap<>(attributeValues.size());
        List<String> conversionErrors = new ArrayList<>();

        attributeValues.forEach((attribute, value) -> {
            try {
                convertedAttributeValues.put(attribute, converters.get(attribute).toInternalValue(attribute, value));
            } catch (ConversionException e) {
                LOGGER.error("Error while converting value of attribute {}", attribute, e);
                conversionErrors.add(String.format("Error while converting input value of attribute [%s]", attribute));
            }
        });

        return new ConversionResult<>(
                new InternalRequest<>(type, principal, ids, attributes, convertedAttributeValues, guardFlags),
                conversionErrors
        );
    }

    public boolean is(RequestType... types) {
        return Arrays.asList(types).contains(type);
    }

    public Principal principal() {
        return principal;
    }

    public Collection<T> ids() {
        return ids;
    }

    public Collection<A> attributes() {
        return attributes;
    }

    public <R> R attributes(Function<Collection<A>, R> function) {
        return function.apply(attributes);
    }

    public Map<A, Object> values(Collection<A> attributes) {
        if (!is(RequestType.UPDATE, RequestType.CREATE, RequestType.SEARCH)) {
            throw new IllegalStateException("Cannot get values from a non CREATE/UPDATE/SEARCH request");
        }

        return attributeValues.entrySet().stream()
                .filter(e -> attributes.contains(e.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }

    @Override
    public boolean hasGuardFlag(Flag flag) {
        return guardFlags.contains(flag);
    }

    @Override
    public InternalRequest<T, A> addGuardedFlagIf(boolean condition, Flag flag) {
        if (!condition) {
            return this;
        }

        Set<Flag> newGuardFlags = new HashSet<>(guardFlags);
        newGuardFlags.add(flag);
        return new InternalRequest<>(type, principal, ids, attributes, attributeValues, newGuardFlags);
    }
}
