package net.cpollet.read.v2.api.attribute;

import net.cpollet.read.v2.api.domain.Id;

import java.util.Collection;
import java.util.Optional;

public interface AttributeStore<T extends Id> {
    Optional<AttributeDef<T>> fetch(String attributeName);

    Optional<AttributeDef<T>> idAttribute();

    Collection<AttributeDef<T>> attributes();
}
