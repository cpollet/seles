package net.cpollet.read.v2.impl;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.methods.NestedMethod;

import java.util.Collection;
import java.util.Optional;

public interface AttributeStore<IdType extends Id> {
    void add(String name, AttributeDef<IdType> def);

    <NestedIdType extends Id> void add(NestedMethod<IdType, NestedIdType> nestedMethod);

    Optional<AttributeDef<IdType>> fetch(String attributeName);

    Collection<AttributeDef<IdType>> directAttributes();
}
