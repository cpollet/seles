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

import net.cpollet.seles.api.domain.Id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class SearchResult<T extends Id> {
    private static final SearchResult EMPTY = new SearchResult();

    private final MergeAlgorithm mergeAlgorithm;
    private final Set<T> ids;
    private final Collection<String> errors;

    public SearchResult(MergeAlgorithm mergeAlgorithm, Collection<T> ids, Collection<String> errors) {
        this.mergeAlgorithm = mergeAlgorithm;
        this.ids = Collections.unmodifiableSet(new HashSet<>(ids));
        this.errors = Collections.unmodifiableCollection(errors);
    }

    public SearchResult(Collection<T> ids, Collection<String> errors) {
        this(new MergeAlgorithm(), ids, errors);
    }

    public SearchResult(Collection<T> ids) {
        this(ids, Collections.emptyList());
    }

    private SearchResult() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Id> SearchResult<T> emptyResult() {
        return (SearchResult<T>) EMPTY;
    }

    public Collection<T> ids() {
        return ids;
    }

    public Collection<String> errors() {
        return errors;
    }

    public SearchResult<T> merge(SearchResult<T> other) {
        return mergeAlgorithm.merge(this, other);
    }

    public static class MergeAlgorithm {
        <T extends Id> SearchResult<T> merge(SearchResult<T> a, SearchResult<T> b) {
            if (a == EMPTY) {
                return b;
            }
            if (b == EMPTY) {
                return a;
            }
            Collection<T> ids = new HashSet<>(a.ids());
            ids.retainAll(b.ids());

            Collection<String> errors = new ArrayList<>(a.errors());
            errors.addAll(b.errors());

            return new SearchResult<>(this, ids, errors);
        }
    }
}
