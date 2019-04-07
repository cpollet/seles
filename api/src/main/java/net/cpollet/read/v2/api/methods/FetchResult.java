package net.cpollet.read.v2.api.methods;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FetchResult<IdType extends Id> {
    private static final FetchResult EMPTY = new FetchResult();

    private final MergeAlgorithm mergeAlgorithm;
    private final Map<IdType, Map<AttributeDef<IdType>, Object>> result;
    private final Collection<String> errors;

    public FetchResult(MergeAlgorithm mergeAlgorithm, Map<IdType, Map<AttributeDef<IdType>, Object>> result, Collection<String> errors) {
        this.mergeAlgorithm = mergeAlgorithm;
        this.result = Collections.unmodifiableMap(result);
        this.errors = Collections.unmodifiableCollection(errors);
    }

    public FetchResult(Map<IdType, Map<AttributeDef<IdType>, Object>> result, Collection<String> errors) {
        this(new MergeAlgorithm(), result, errors);
    }

    public FetchResult(Map<IdType, Map<AttributeDef<IdType>, Object>> result) {
        this(result, Collections.emptyList());
    }

    private FetchResult() {
        this(Collections.emptyMap(), Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    public static <IdType extends Id> FetchResult<IdType> emptyResult() {
        return (FetchResult<IdType>) EMPTY;
    }

    public Map<IdType, Map<AttributeDef<IdType>, Object>> result() {
        return result;
    }

    public Collection<String> errors() {
        return errors;
    }

    public FetchResult<IdType> merge(FetchResult<IdType> other) {
        return mergeAlgorithm.merge(this, other);
    }

    public static class MergeAlgorithm {
        public <IdType extends Id> FetchResult<IdType> merge(FetchResult<IdType> a, FetchResult<IdType> b) {
            HashMap<IdType, Map<AttributeDef<IdType>, Object>> newResult = new HashMap<>(a.result());
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
