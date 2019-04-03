package net.cpollet.read.v2.impl.execution;

import net.cpollet.read.v2.api.conversion.ConversionException;
import net.cpollet.read.v2.api.conversion.ValueConverter;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.execution.Response;
import net.cpollet.read.v2.impl.data.BiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InternalResponse<IdType extends Id, AttributeType> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternalRequest.class);

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
        Map<IdType, Map<AttributeType, Object>> convertedValues = new HashMap<>(values.size());
        List<String> conversionErrors = new ArrayList<>();

        values.forEach((id, attributesValues) -> {
            convertedValues.putIfAbsent(id, new HashMap<>(values.get(id).size()));
            attributesValues.forEach((attribute, value) -> {
                try {
                    convertedValues.get(id).put(attribute, converters.get(attribute).toExternalValue(attribute, value));
                } catch (ConversionException e) {
                    LOGGER.error("Error while converting value of attribute {}", attribute, e);
                    conversionErrors.add(String.format("Error while converting attribute [%s] value for key [%s]", attribute, id));
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
