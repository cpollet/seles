package net.cpollet.read.v2.impl.methods;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.AttributeDef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FetchResult<IdType extends Id> {
    private final Map<IdType, Map<AttributeDef<IdType>, Object>> result;
    private final Collection<String> errors;

    FetchResult(Map<IdType, Map<AttributeDef<IdType>, Object>> result, Collection<String> errors) {
        this.result = Collections.unmodifiableMap(result);
        this.errors = Collections.unmodifiableCollection(errors);
    }

    public FetchResult(Map<IdType, Map<AttributeDef<IdType>, Object>> result) {
        this(result, Collections.emptyList());
    }

    public FetchResult() {
        this(Collections.emptyMap(), Collections.emptyList());
    }

    public Map<IdType, Map<AttributeDef<IdType>, Object>> result() {
        return result;
    }

    public Collection<String> errors() {
        return errors;
    }

    public FetchResult<IdType> append(FetchResult<IdType> other) {
        HashMap<IdType, Map<AttributeDef<IdType>, Object>> newResult = new HashMap<>(result);
        other.result().forEach((key, value) -> {
            newResult.putIfAbsent(key, new HashMap<>());
            newResult.get(key).putAll(value);
        });

        ArrayList<String> newErrors = new ArrayList<>(errors);
        newErrors.addAll(other.errors());

        return new FetchResult<>(newResult, newErrors);
    }
}
