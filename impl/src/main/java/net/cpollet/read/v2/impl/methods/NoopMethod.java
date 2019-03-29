package net.cpollet.read.v2.impl.methods;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.AttributeDef;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class NoopMethod<IdType extends Id> implements Method<IdType> {
    private static final Method<Id> instance = new NoopMethod<>();

    private NoopMethod() {
        // nothing
    }

    @Override
    public FetchResult<IdType> fetch(List<AttributeDef<IdType>> attributes, Collection<IdType> ids) {
        throw new IllegalStateException("One should never call fetch() on the NOOP method!");
    }

    @Override
    public Collection<String> update(Map<AttributeDef<IdType>, Object> attributeValues, Collection<IdType> ids) {
        throw new IllegalStateException("One should never call update() on the NOOP method!");
    }


    @SuppressWarnings("unchecked")
    public static <IdType extends Id> Method<IdType> get() {
        return (Method<IdType>) instance;
    }
}
