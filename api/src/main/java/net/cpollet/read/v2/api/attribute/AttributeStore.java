package net.cpollet.read.v2.api.attribute;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.execution.Executor;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public interface AttributeStore<IdType extends Id> {
    void add(String name, AttributeDef<IdType> def);

    <NestedIdType extends Id> void nest(String prefix, AttributeDef<IdType> attribute, Executor<NestedIdType> executor, Function<Object, NestedIdType> idProvider);

    Optional<AttributeDef<IdType>> fetch(String attributeName);

    Collection<AttributeDef<IdType>> directAttributes();
}
