package net.cpollet.read.v2.impl;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.methods.NestedMethod;

import java.util.Collection;

public interface AttributeStore<IdType extends Id> {
    AttributeDef<IdType> fetch(String attributeName);

    <NestedIdType extends Id> void nest(Collection<String> nestedAttributes, NestedMethod<IdType, NestedIdType> nestedMethod);
}
