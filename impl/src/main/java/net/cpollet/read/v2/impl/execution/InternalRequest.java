package net.cpollet.read.v2.impl.execution;

import net.cpollet.read.v2.api.conversion.ConversionException;
import net.cpollet.read.v2.api.conversion.ValueConverter;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.execution.Request;
import net.cpollet.read.v2.impl.Guarded;
import net.cpollet.read.v2.impl.conversion.ConversionResult;
import net.cpollet.read.v2.impl.data.BiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class InternalRequest<IdType, AttributeType> implements Guarded<InternalRequest<IdType, AttributeType>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternalRequest.class);

    private final Set<Flag> guardFlags;
    private final Set<IdType> ids;
    private final Set<AttributeType> attributes;
    private final Map<AttributeType, Object> attributeValues;
    private final RequestType type;

    public enum RequestType { // FIXME transform into different classes
        READ, UPDATE, DELETE, CREATE
    }

    private InternalRequest(RequestType type, Set<IdType> ids, Set<AttributeType> attributes, Map<AttributeType, Object> attributesValues, Set<Flag> guardFlags) {
        this.ids = Collections.unmodifiableSet(ids);
        this.attributes = Collections.unmodifiableSet(attributes);
        this.attributeValues = Collections.unmodifiableMap(attributesValues);
        this.type = type;
        this.guardFlags = Collections.unmodifiableSet(guardFlags);
    }

    static <IdType extends Id> InternalRequest<IdType, String> wrap(RequestType type, Request<IdType> request) {
        return new InternalRequest<>(
                type,
                new HashSet<>(request.getIds()),
                new HashSet<>(request.getAttributes()),
                request.getAttributesValues(),
                Collections.emptySet()
        );
    }

    public InternalRequest<IdType, AttributeType> withIds(Collection<IdType> idsToAdd) {
        Set<IdType> newIds = new HashSet<>(ids);
        newIds.addAll(idsToAdd);
        return new InternalRequest<>(type, newIds, attributes, attributeValues, guardFlags);
    }

    public InternalRequest<IdType, AttributeType> withoutIds(Collection<IdType> idsToRemove) {
        Set<IdType> newIds = new HashSet<>(ids);
        newIds.removeAll(idsToRemove);
        return new InternalRequest<>(type, newIds, attributes, attributeValues, guardFlags);
    }

    public InternalRequest<IdType, AttributeType> withAttributes(Collection<AttributeType> attributesToAdd) {
        if (!is(RequestType.READ)) {
            throw new IllegalStateException("Cannot add attributes to a non READ request");
        }

        Set<AttributeType> newAttributes = new HashSet<>(attributes);
        newAttributes.addAll(attributesToAdd);
        return new InternalRequest<>(type, ids, newAttributes, attributeValues, guardFlags);
    }

    public InternalRequest<IdType, AttributeType> withoutAttributes(Collection<AttributeType> attributesToRemove) {
        Set<AttributeType> newAttributes = new HashSet<>(attributes);
        newAttributes.removeAll(attributesToRemove);

        Map<AttributeType, Object> newAttributesValues = new HashMap<>(attributeValues);
        attributesToRemove.forEach(newAttributesValues::remove);

        return new InternalRequest<>(type, ids, newAttributes, newAttributesValues, guardFlags);
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

        return new InternalRequest<>(type, ids, newAttributes, newAttributeValues, guardFlags);
    }

    public ConversionResult<InternalRequest<IdType, AttributeType>> convertValues(Map<AttributeType, ValueConverter<AttributeType>> converters) {
        if (!is(RequestType.UPDATE, RequestType.CREATE)) {
            return new ConversionResult<>(this);
        }

        Map<AttributeType, Object> convertedAttributeValues = new HashMap<>(attributeValues.size());
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
                new InternalRequest<>(type, ids, attributes, convertedAttributeValues, guardFlags),
                conversionErrors
        );
    }

    public boolean is(RequestType... types) {
        return Arrays.asList(types).contains(type);
    }

    public Collection<IdType> ids() {
        return ids;
    }

    public Collection<AttributeType> attributes() {
        return attributes;
    }

    public <R> R attributes(Function<Collection<AttributeType>, R> function) {
        return function.apply(attributes);
    }

    public Map<AttributeType, Object> values(Collection<AttributeType> attributes) {
        if (!is(RequestType.UPDATE, RequestType.CREATE)) {
            throw new IllegalStateException("Cannot get values from a non-UPDATE / non-CREATE request");
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
    public InternalRequest<IdType, AttributeType> addGuardedFlagIf(boolean condition, Flag flag) {
        if (!condition) {
            return this;
        }

        Set<Flag> newGuardFlags = new HashSet<>(guardFlags);
        newGuardFlags.add(flag);
        return new InternalRequest<>(type, ids, attributes, attributeValues, newGuardFlags);
    }
}
