package net.cpollet.read.v2.impl;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.domain.Request;
import net.cpollet.read.v2.impl.data.BiMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class InternalRequest<IdType, AttributeType> {
    private final Collection<IdType> ids;
    private final Collection<AttributeType> attributes;

    private InternalRequest(Collection<IdType> ids, Collection<AttributeType> attributes) {
        this.ids = Collections.unmodifiableCollection(ids);
        this.attributes = Collections.unmodifiableCollection(attributes);
    }

    static <IdType extends Id> InternalRequest<IdType, String> wrap(Request<IdType> request) {
        return new InternalRequest<>(request.getIds(), request.getAttributes());
    }

    public InternalRequest<IdType, AttributeType> withoutIds(Collection<IdType> idsToRemove) {
        ArrayList<IdType> newIds = new ArrayList<>(ids);
        newIds.removeAll(idsToRemove);
        return new InternalRequest<>(newIds, attributes);
    }

    public InternalRequest<IdType, AttributeType> withAttributes(Collection<AttributeType> attributesToAdd) {
        ArrayList<AttributeType> newAttributes = new ArrayList<>(attributes);
        newAttributes.addAll(attributesToAdd);
        return new InternalRequest<>(ids, newAttributes);
    }

    public InternalRequest<IdType, AttributeType> withoutAttributes(Collection<AttributeType> attributesToRemove) {
        ArrayList<AttributeType> newAttributes = new ArrayList<>(attributes);
        newAttributes.removeAll(attributesToRemove);
        return new InternalRequest<>(ids, newAttributes);
    }

    public <AttributeTypeTo> InternalRequest<IdType, AttributeTypeTo> mapAttributes(BiMap<AttributeTypeTo, AttributeType> conversionMap) {
        Set<AttributeTypeTo> newAttributes = attributes.stream()
                .map(conversionMap::getLeft)
                .collect(Collectors.toSet());

        return new InternalRequest<>(ids, newAttributes);
    }

    public Collection<IdType> ids() {
        return ids;
    }

    public Collection<AttributeType> attributes() {
        return attributes;
    }
}
