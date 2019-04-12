package net.cpollet.read.v2.api.methods;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class FetchResult<T extends Id> {
    private static final FetchResult EMPTY = new FetchResult();

    private final MergeAlgorithm mergeAlgorithm;
    private final Map<T, Map<AttributeDef<T>, Object>> result;
    private final Collection<String> errors;

    public FetchResult(MergeAlgorithm mergeAlgorithm, Map<T, Map<AttributeDef<T>, Object>> result, Collection<String> errors) {
        this.mergeAlgorithm = mergeAlgorithm;
        this.result = Collections.unmodifiableMap(result);
        this.errors = Collections.unmodifiableCollection(errors);
    }

    public FetchResult(Map<T, Map<AttributeDef<T>, Object>> result, Collection<String> errors) {
        this(new MergeAlgorithm(), result, errors);
    }

    public FetchResult(Map<T, Map<AttributeDef<T>, Object>> result) {
        this(result, Collections.emptyList());
    }

    private FetchResult() {
        this(Collections.emptyMap(), Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Id> FetchResult<T> emptyResult() {
        return (FetchResult<T>) EMPTY;
    }

    public Map<T, Map<AttributeDef<T>, Object>> result() {
        return result;
    }

    public Collection<String> errors() {
        return errors;
    }

    public FetchResult<T> merge(FetchResult<T> other) {
        return mergeAlgorithm.merge(this, other);
    }

    public static class MergeAlgorithm {
        public <T extends Id> FetchResult<T> merge(FetchResult<T> a, FetchResult<T> b) {
            HashMap<T, Map<AttributeDef<T>, Object>> newResult = new HashMap<>(a.result());
            b.result().forEach((key, value) -> {
                newResult.putIfAbsent(key, new HashMap<>());
                newResult.get(key).putAll(value);
            });

            ArrayList<String> newErrors = new ArrayList<>(a.errors());
            newErrors.addAll(b.errors());

            return new FetchResult<>(this, newResult, newErrors);
        }
    }
}
