/*
 * Copyright 2019 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cpollet.seles.api.methods;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.domain.Id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class FetchResult<T extends Id> {
    private static final FetchResult<Id> EMPTY = new FetchResult<>();

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
        <T extends Id> FetchResult<T> merge(FetchResult<T> a, FetchResult<T> b) {
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
