package net.cpollet.read.v2.impl.attribute;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.domain.Id;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DirectAttributeStore<IdType extends Id> implements AttributeStore<IdType> {
    private final Map<String, AttributeDef<IdType>> attributes;
    private final AttributeDef<IdType> idAttribute;

    public DirectAttributeStore(String idAttribute, Collection<AttributeDef<IdType>> attributes) {
        this.attributes = Collections.unmodifiableMap(
                attributes.stream()
                        .collect(Collectors.toMap(
                                AttributeDef::name,
                                a -> a
                        ))
        );
        this.idAttribute = attributes.isEmpty() ? null : this.attributes.get(idAttribute);
    }

    public DirectAttributeStore(Collection<AttributeDef<IdType>> attributes) {
        this("", attributes);
    }

    @Override
    public Optional<AttributeDef<IdType>> fetch(String attributeName) {
        return Optional.ofNullable(attributes.get(attributeName));
    }

    @Override
    public Optional<AttributeDef<IdType>> idAttribute() {
        return Optional.ofNullable(idAttribute);
    }

    @Override
    public Collection<AttributeDef<IdType>> attributes() {
        return attributes.values();
    }
}
