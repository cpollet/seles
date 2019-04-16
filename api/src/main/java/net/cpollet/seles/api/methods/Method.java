package net.cpollet.seles.api.methods;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.domain.Id;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Method<T extends Id> {
    FetchResult<T> fetch(Principal principal, List<AttributeDef<T>> attributes, Collection<T> ids);

    Collection<String> update(Principal principal, Map<AttributeDef<T>, Object> attributeValues, Collection<T> ids);

    Collection<String> delete(Principal principal, List<AttributeDef<T>> attributes, Collection<T> ids);

    CreateResult<T> create(Principal principal, Map<AttributeDef<T>, Object> values);

    SearchResult<T> search(Principal principal, Map<AttributeDef<T>, Object> values);
}
