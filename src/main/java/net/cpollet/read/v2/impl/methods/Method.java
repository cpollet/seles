package net.cpollet.read.v2.impl.methods;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.AttributeDef;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Method<IdType extends Id> {
    FetchResult<IdType> fetch(List<AttributeDef<IdType>> attributes, Collection<IdType> ids);

    Collection<String> update(Map<AttributeDef<IdType>, Object> attributeValues, Collection<IdType> ids);
}
