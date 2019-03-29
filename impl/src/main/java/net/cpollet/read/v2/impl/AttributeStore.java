package net.cpollet.read.v2.impl;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.methods.NestedMethod;

import java.util.Collection;

public interface AttributeStore<IdType extends Id> {
    void add(String name, AttributeDef<IdType> def);

    <NestedIdType extends Id> void add(NestedMethod<IdType, NestedIdType> nestedMethod);

    AttributeDef<IdType> fetch(String attributeName);

    Collection<AttributeDef<IdType>> directAttributes();
}
