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

public class InternalRequest<T extends Id, A> implements Guarded<InternalRequest<T, A>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(InternalRequest.class);

    private final Set<Flag> guardFlags;
    private final Set<T> ids;
    private final Set<A> attributes;
    private final Map<A, Object> attributeValues;
    private final RequestType type;

    public enum RequestType { // FIXME transform into different classes
        READ, UPDATE, DELETE, CREATE, SEARCH
    }

    private InternalRequest(RequestType type, Set<T> ids, Set<A> attributes, Map<A, Object> attributesValues, Set<Flag> guardFlags) {
        this.ids = Collections.unmodifiableSet(ids);
        this.attributes = Collections.unmodifiableSet(attributes);
        this.attributeValues = Collections.unmodifiableMap(attributesValues);
        this.type = type;
        this.guardFlags = Collections.unmodifiableSet(guardFlags);
    }

    static <T extends Id> InternalRequest<T, String> wrap(RequestType type, Request<T> request) {
        return new InternalRequest<>(
                type,
                new HashSet<>(request.getIds()),
                new HashSet<>(request.getAttributes()),
                request.getAttributesValues(),
                Collections.emptySet()
        );
    }

    public InternalRequest<T, A> withIds(Collection<T> idsToAdd) {
        Set<T> newIds = new HashSet<>(ids);
        newIds.addAll(idsToAdd);
        return new InternalRequest<>(type, newIds, attributes, attributeValues, guardFlags);
    }

    public InternalRequest<T, A> withoutIds(Collection<T> idsToRemove) {
        Set<T> newIds = new HashSet<>(ids);
        newIds.removeAll(idsToRemove);
        return new InternalRequest<>(type, newIds, attributes, attributeValues, guardFlags);
    }

    public InternalRequest<T, A> withAttributes(Collection<A> attributesToAdd) {
        if (!is(RequestType.READ)) {
            throw new IllegalStateException("Cannot add attributes to a non READ request");
        }

        Set<A> newAttributes = new HashSet<>(attributes);
        newAttributes.addAll(attributesToAdd);
        return new InternalRequest<>(type, ids, newAttributes, attributeValues, guardFlags);
    }

    public InternalRequest<T, A> withoutAttributes(Collection<A> attributesToRemove) {
        Set<A> newAttributes = new HashSet<>(attributes);
        newAttributes.removeAll(attributesToRemove);

        Map<A, Object> newAttributesValues = new HashMap<>(attributeValues);
        attributesToRemove.forEach(newAttributesValues::remove);

        return new InternalRequest<>(type, ids, newAttributes, newAttributesValues, guardFlags);
    }

    public <B> InternalRequest<T, B> mapAttributes(BiMap<B, A> conversionMap) {
        Set<B> newAttributes = attributes.stream()
                .map(conversionMap::getLeft)
                .collect(Collectors.toSet());

        Map<B, Object> newAttributeValues = attributeValues.keySet().stream()
                .collect(Collectors.toMap(
                        conversionMap::getLeft,
                        attributeValues::get
                ));

        return new InternalRequest<>(type, ids, newAttributes, newAttributeValues, guardFlags);
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
                new InternalRequest<>(type, ids, attributes, convertedAttributeValues, guardFlags),
                conversionErrors
        );
    }

    public boolean is(RequestType... types) {
        return Arrays.asList(types).contains(type);
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
        return new InternalRequest<>(type, ids, attributes, attributeValues, newGuardFlags);
    }
}
