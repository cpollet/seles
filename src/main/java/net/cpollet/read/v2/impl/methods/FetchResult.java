package net.cpollet.read.v2.impl.methods;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.AttributeDef;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class FetchResult<IdType extends Id> {
    private final Map<IdType, Map<AttributeDef<IdType>, Object>> result;
    private final Collection<String> errors;

    private FetchResult(Map<IdType, Map<AttributeDef<IdType>, Object>> result, Collection<String> errors) {
        this.result = Collections.unmodifiableMap(result);
        this.errors = Collections.unmodifiableCollection(errors);
    }

    FetchResult(Map<IdType, Map<AttributeDef<IdType>, Object>> result) {
        this(result, Collections.emptyList());
    }

    public Map<IdType, Map<AttributeDef<IdType>, Object>> result() {
        return result;
    }
}
