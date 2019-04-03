package net.cpollet.read.v2.api.attribute;

import net.cpollet.read.v2.api.domain.Id;

import java.util.Collection;
import java.util.Optional;

public interface AttributeStore<IdType extends Id> {
    Optional<AttributeDef<IdType>> fetch(String attributeName);

    Collection<AttributeDef<IdType>> attributes();
}
