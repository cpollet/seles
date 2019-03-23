package net.cpollet.read.v2.api;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.AttributeDef;
import net.cpollet.read.v2.impl.methods.NestedMethod;

public interface AttributeStore<IdType extends Id> {
    void add(String name, AttributeDef<IdType> def);

    <NestedIdType extends Id> void nest(NestedMethod<IdType, NestedIdType> nestedMethod);

    AttributeDef<IdType> fetch(String attributeName);
}
