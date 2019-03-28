package net.cpollet.read.v2.impl;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.domain.Response;
import net.cpollet.read.v2.impl.data.BiMap;
import net.cpollet.read.v2.impl.stages.ConversionException;
import net.cpollet.read.v2.impl.stages.ValueConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InternalResponse<IdType extends Id, AttributeType> {
    private final Map<IdType, Map<AttributeType, Object>> values;
    private final Collection<String> errors;
    private final Collection<String> messages;
    private final long executionTime;

    private InternalResponse(Map<IdType, Map<AttributeType, Object>> values, Collection<String> errors, Collection<String> messages, long executionTime) {
        this.values = Collections.unmodifiableMap(values);
        this.errors = Collections.unmodifiableCollection(errors);
        this.messages = Collections.unmodifiableCollection(messages);
        this.executionTime = executionTime;
    }

    public InternalResponse(Map<IdType, Map<AttributeType, Object>> values) {
        this(values, Collections.emptyList(), Collections.emptyList(), 0L);
    }

    static <IdType extends Id> Response<IdType> unwrap(InternalResponse<IdType, String> response) {
        return new Response<>(response.values, response.errors, response.messages, response.executionTime);
    }

    public <AttributeTypeTo> InternalResponse<IdType, AttributeTypeTo> mapAttributes(BiMap<AttributeType, AttributeTypeTo> conversionMap) {
        Map<IdType, Map<AttributeTypeTo, Object>> convertedMap = values.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> convertAttributes(e.getValue(), conversionMap)
                ));

        return new InternalResponse<>(convertedMap, errors, messages, executionTime);
    }

    private <AttributeTypeTo> Map<AttributeTypeTo, Object> convertAttributes(Map<AttributeType, Object> map, BiMap<AttributeType, AttributeTypeTo> conversionMap) {
        return map.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> conversionMap.getRight(e.getKey()),
                        Map.Entry::getValue
                ));
    }

    public InternalResponse<IdType, AttributeType> convertValues(Map<AttributeType, ValueConverter<AttributeType>> converters) {
        Map<IdType, Map<AttributeType, Object>> convertedValues = new HashMap<>();
        List<String> conversionErrors = new ArrayList<>();

        values.forEach((id, attributesValues) -> {
            convertedValues.putIfAbsent(id, new HashMap<>());
            attributesValues.forEach((attribute, value) -> {
                try {
                    convertedValues.get(id).put(attribute, converters.get(attribute).toExternalValue(attribute, value));
                } catch (ConversionException e) {
                    conversionErrors.add(String.format("Error while converting attribute [%s] value for key [%s]", attribute, id));
                    // LOG ERROR
                }
            });
        });

        return new InternalResponse<>(convertedValues, errors, messages, executionTime)
                .withErrors(conversionErrors);
    }

    public InternalResponse<IdType, AttributeType> withErrors(Collection<String> errors) {
        if (errors.isEmpty()) {
            return this;
        }
        ArrayList<String> mergedErrors = new ArrayList<>(this.errors.size() + errors.size());
        mergedErrors.addAll(this.errors);
        mergedErrors.addAll(errors);
        return new InternalResponse<>(values, mergedErrors, messages, executionTime);
    }

    public InternalResponse<IdType, AttributeType> withMessages(Collection<String> messages) {
        if (messages.isEmpty()) {
            return this;
        }
        ArrayList<String> mergedMessages = new ArrayList<>(this.messages.size() + messages.size());
        mergedMessages.addAll(this.messages);
        mergedMessages.addAll(messages);
        return new InternalResponse<>(values, errors, mergedMessages, executionTime);
    }

    public InternalResponse<IdType, AttributeType> withExecutionTime(long executionTime) {
        return new InternalResponse<>(values, errors, messages, executionTime);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public InternalResponse<IdType, AttributeType> append(Map<IdType, Map<AttributeType, String>> values) {
        HashMap<IdType, Map<AttributeType, Object>> newResult = new HashMap<>(this.values);
        values.forEach((key, value) -> {
            newResult.putIfAbsent(key, new HashMap<>());
            newResult.get(key).putAll(value);
        });

        return new InternalResponse<>(newResult, errors, messages, executionTime);
    }
}
