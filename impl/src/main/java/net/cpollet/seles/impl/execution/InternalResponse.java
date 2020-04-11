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
import net.cpollet.seles.api.execution.Response;
import net.cpollet.seles.impl.data.BiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a response, as used and transformed from {@link net.cpollet.seles.impl.stages.Stage} to
 * {@link net.cpollet.seles.impl.stages.Stage} instances. Instances are immutable.
 *
 * @param <A> the type of the attribute; usually {@link String} or {@link net.cpollet.seles.api.attribute.AttributeDef}
 */
public final class InternalResponse<A> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternalRequest.class);

    private final Map<Id, Map<A, Object>> values;
    private final Collection<String> errors;
    private final Collection<String> messages;
    private final long executionTime;

    private InternalResponse(Map<Id, Map<A, Object>> values, Collection<String> errors, Collection<String> messages, long executionTime) {
        this.values = Collections.unmodifiableMap(values);
        this.errors = Collections.unmodifiableCollection(errors);
        this.messages = Collections.unmodifiableCollection(messages);
        this.executionTime = executionTime;
    }

    public InternalResponse(Map<Id, Map<A, Object>> values) {
        this(values, Collections.emptyList(), Collections.emptyList(), 0L);
    }

    public InternalResponse() {
        this(Collections.emptyMap());
    }

    static Response unwrap(InternalResponse<String> response) {
        return new Response(response.values, response.errors, response.messages, response.executionTime);
    }

    /**
     * Transforms a response with attributes of type A to a request with attributes of type B. Usually, this means
     * transforming a response with attribute descriptors ({@link net.cpollet.seles.api.attribute.AttributeDef}) to
     * attribute names ({@link String}).
     *
     * @param conversionMap the map used to converts from A to B and vice versa.
     * @param <B>           destination attribute type
     * @return the response, with attributes mapped from type A to type B
     * @see InternalRequest#mapAttributes(BiMap) for the reverse operation
     */
    public <B> InternalResponse<B> mapAttributes(BiMap<A, B> conversionMap) {
        Map<Id, Map<B, Object>> convertedMap = values.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> convertAttributes(e.getValue(), conversionMap)
                ));

        return new InternalResponse<>(convertedMap, errors, messages, executionTime);
    }

    private <B> Map<B, Object> convertAttributes(Map<A, Object> map, BiMap<A, B> conversionMap) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> conversionMap.getRight(e.getKey()),
                        Map.Entry::getValue
                ));
    }

    public InternalResponse<A> convertValues(Map<A, ValueConverter<A>> converters) {
        Map<Id, Map<A, Object>> convertedValues = new HashMap<>(values.size());
        List<String> conversionErrors = new ArrayList<>();

        values.forEach((id, attributesValues) -> {
            convertedValues.putIfAbsent(id, new HashMap<>(values.get(id).size()));
            attributesValues.forEach((attribute, value) -> {
                try {
                    convertedValues.get(id).put(attribute, converters.get(attribute).toExternalValue(attribute, value));
                }
                catch (ConversionException e) {
                    LOGGER.error("Error while converting output value of attribute {}", attribute, e);
                    conversionErrors.add(String.format("Error while converting attribute [%s] value for key [%s]", attribute, id));
                }
            });
        });

        return new InternalResponse<>(convertedValues, errors, messages, executionTime)
                .withErrors(conversionErrors);
    }

    public InternalResponse<A> withErrors(Collection<String> errors) {
        if (errors.isEmpty()) {
            return this;
        }
        ArrayList<String> mergedErrors = new ArrayList<>(this.errors.size() + errors.size());
        mergedErrors.addAll(this.errors);
        mergedErrors.addAll(errors);
        return new InternalResponse<>(values, mergedErrors, messages, executionTime);
    }

    public InternalResponse<A> mergeErrors(InternalResponse<A> other) {
        return withErrors(other.errors);
    }

    public InternalResponse<A> withMessages(Collection<String> messages) {
        if (messages.isEmpty()) {
            return this;
        }
        ArrayList<String> mergedMessages = new ArrayList<>(this.messages.size() + messages.size());
        mergedMessages.addAll(this.messages);
        mergedMessages.addAll(messages);
        return new InternalResponse<>(values, errors, mergedMessages, executionTime);
    }

    public InternalResponse<A> mergeMessages(InternalResponse<A> other) {
        return withMessages(other.messages);
    }

    public InternalResponse<A> withExecutionTime(long executionTime) {
        return new InternalResponse<>(values, errors, messages, executionTime);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public InternalResponse<A> append(Map<Id, Map<A, String>> values) {
        HashMap<Id, Map<A, Object>> newResult = new HashMap<>(this.values);
        values.forEach((key, value) -> {
            newResult.putIfAbsent(key, new HashMap<>());
            newResult.get(key).putAll(value);
        });

        return new InternalResponse<>(newResult, errors, messages, executionTime);
    }
}
