package net.cpollet.read.v2.api.methods;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Method<T extends Id> {
    FetchResult<T> fetch(List<AttributeDef<T>> attributes, Collection<T> ids);

    Collection<String> update(Map<AttributeDef<T>, Object> attributeValues, Collection<T> ids);

    Collection<String> delete(List<AttributeDef<T>> attributes, Collection<T> ids);

    CreateResult<T> create(Map<AttributeDef<T>, Object> values);

    SearchResult<T> search(Map<AttributeDef<T>, Object> values);
}
