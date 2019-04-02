package net.cpollet.read.v2.impl.execution;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.execution.Request;
import net.cpollet.read.v2.impl.conversion.ConversionResult;
import net.cpollet.read.v2.impl.data.BiMap;
import net.cpollet.read.v2.api.conversion.ConversionException;
import net.cpollet.read.v2.api.conversion.ValueConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class InternalRequest<IdType, AttributeType> {
    private final Collection<IdType> ids;
    private final Collection<AttributeType> attributes;
    private final Map<AttributeType, Object> attributeValues;
    private final RequestType type;

    public enum RequestType {
        READ, UPDATE;

        public static <AttributeType> RequestType from(Collection<AttributeType> attributes, Map<AttributeType, Object> attributesValues) {
            if (attributesValues.isEmpty()) {
                return READ;
            }

            return UPDATE;
        }
    }

    private InternalRequest(Collection<IdType> ids, Collection<AttributeType> attributes, Map<AttributeType, Object> attributesValues) {
        this.ids = Collections.unmodifiableCollection(ids);
        this.attributes = Collections.unmodifiableCollection(attributes);
        this.attributeValues = Collections.unmodifiableMap(attributesValues);
        this.type = RequestType.from(attributes, attributesValues);
    }

    static <IdType extends Id> InternalRequest<IdType, String> wrap(Request<IdType> request) {
        return new InternalRequest<>(request.getIds(), request.getAttributes(), request.getAttributesValues());
    }

    public InternalRequest<IdType, AttributeType> withoutIds(Collection<IdType> idsToRemove) {
        ArrayList<IdType> newIds = new ArrayList<>(ids);
        newIds.removeAll(idsToRemove);
        return new InternalRequest<>(newIds, attributes, attributeValues);
    }

    public InternalRequest<IdType, AttributeType> withAttributes(Collection<AttributeType> attributesToAdd) {
        if (is(RequestType.UPDATE)) {
            throw new IllegalStateException("Cannot add attributes to an UPDATE request");
        }

        ArrayList<AttributeType> newAttributes = new ArrayList<>(attributes);
        newAttributes.addAll(attributesToAdd);
        return new InternalRequest<>(ids, newAttributes, attributeValues);
    }

    public InternalRequest<IdType, AttributeType> withoutAttributes(Collection<AttributeType> attributesToRemove) {
        ArrayList<AttributeType> newAttributes = new ArrayList<>(attributes);
        newAttributes.removeAll(attributesToRemove);

        Map<AttributeType, Object> newAttributesValues = new HashMap<>(attributeValues);
        attributesToRemove.forEach(newAttributesValues::remove);

        return new InternalRequest<>(ids, newAttributes, newAttributesValues);
    }

    public <AttributeTypeTo> InternalRequest<IdType, AttributeTypeTo> mapAttributes(BiMap<AttributeTypeTo, AttributeType> conversionMap) {
        Set<AttributeTypeTo> newAttributes = attributes.stream()
                .map(conversionMap::getLeft)
                .collect(Collectors.toSet());

        Map<AttributeTypeTo, Object> newAttributeValues = attributeValues.keySet().stream()
                .collect(Collectors.toMap(
                        conversionMap::getLeft,
                        attributeValues::get
                ));

        return new InternalRequest<>(ids, newAttributes, newAttributeValues);
    }

    public ConversionResult<InternalRequest<IdType, AttributeType>> convertValues(Map<AttributeType, ValueConverter<AttributeType>> converters) {
        if (type != RequestType.UPDATE) {
            return new ConversionResult<>(this);
        }

        Map<AttributeType, Object> convertedAttributeValues = new HashMap<>(attributeValues.size());
        List<String> conversionErrors = new ArrayList<>();

        attributeValues.forEach((attribute, value) -> {
            try {
                convertedAttributeValues.put(attribute, converters.get(attribute).toInternalValue(attribute, value));
            } catch (ConversionException e) {
                conversionErrors.add(String.format("Error while converting value of attribute [%s] ", attribute));
                // LOG ERROR
            }
        });

        return new ConversionResult<>(
                new InternalRequest<>(ids, attributes, convertedAttributeValues),
                conversionErrors
        );
    }

    public boolean is(RequestType... types) {
        return Arrays.asList(types).contains(type);
    }

    public Collection<IdType> ids() {
        return ids;
    }

    @Deprecated
    public Collection<AttributeType> attributes() {
        return attributes;
    }

    public <R> R attributes(Function<Collection<AttributeType>, R> function) {
        return function.apply(attributes);
    }

    public Map<AttributeType, Object> values(Collection<AttributeType> attributes) {
        if (!is(RequestType.UPDATE)) {
            throw new IllegalStateException("Cannot get values from a non-UPDATE request");
        }

        return attributeValues.entrySet().stream()
                .filter(e -> attributes.contains(e.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
}
