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

public final class FetchResult {
    private static final FetchResult EMPTY = new FetchResult();

    private final MergeAlgorithm mergeAlgorithm;
    private final Map<Id, Map<AttributeDef, Object>> result;
    private final Collection<String> errors;

    public FetchResult(MergeAlgorithm mergeAlgorithm, Map<Id, Map<AttributeDef, Object>> result, Collection<String> errors) {
        this.mergeAlgorithm = mergeAlgorithm;
        this.result = Collections.unmodifiableMap(result);
        this.errors = Collections.unmodifiableCollection(errors);
    }

    public FetchResult(Map<Id, Map<AttributeDef, Object>> result, Collection<String> errors) {
        this(new MergeAlgorithm(), result, errors);
    }

    public FetchResult(Map<Id, Map<AttributeDef, Object>> result) {
        this(result, Collections.emptyList());
    }

    private FetchResult() {
        this(Collections.emptyMap(), Collections.emptyList());
    }

    public static FetchResult emptyResult() {
        return EMPTY;
    }

    public Map<Id, Map<AttributeDef, Object>> result() {
        return result;
    }

    public Collection<String> errors() {
        return errors;
    }

    public FetchResult merge(FetchResult other) {
        return mergeAlgorithm.merge(this, other);
    }

    public static class MergeAlgorithm {
        FetchResult merge(FetchResult a, FetchResult b) {
            HashMap<Id, Map<AttributeDef, Object>> newResult = new HashMap<>(a.result());
            b.result().forEach((key, value) -> {
                newResult.putIfAbsent(key, new HashMap<>());
                newResult.get(key).putAll(value);
            });

            ArrayList<String> newErrors = new ArrayList<>(a.errors());
            newErrors.addAll(b.errors());

            return new FetchResult(this, newResult, newErrors);
        }
    }
}
