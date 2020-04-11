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

public final class SearchResult {
    private static final SearchResult EMPTY = new SearchResult();

    private final MergeAlgorithm mergeAlgorithm;
    private final Set<Id> ids;
    private final Collection<String> errors;

    public SearchResult(MergeAlgorithm mergeAlgorithm, Collection<Id> ids, Collection<String> errors) {
        this.mergeAlgorithm = mergeAlgorithm;
        this.ids = Collections.unmodifiableSet(new HashSet<>(ids));
        this.errors = Collections.unmodifiableCollection(errors);
    }

    public SearchResult(Collection<Id> ids, Collection<String> errors) {
        this(new MergeAlgorithm(), ids, errors);
    }

    public SearchResult(Collection<Id> ids) {
        this(ids, Collections.emptyList());
    }

    private SearchResult() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    public static SearchResult emptyResult() {
        return EMPTY;
    }

    public Collection<Id> ids() {
        return ids;
    }

    public Collection<String> errors() {
        return errors;
    }

    public SearchResult merge(SearchResult other) {
        return mergeAlgorithm.merge(this, other);
    }

    public static class MergeAlgorithm {
        SearchResult merge(SearchResult a, SearchResult b) {
            if (a == EMPTY) {
                return b;
            }
            if (b == EMPTY) {
                return a;
            }
            Collection<Id> ids = new HashSet<>(a.ids());
            ids.retainAll(b.ids());

            Collection<String> errors = new ArrayList<>(a.errors());
            errors.addAll(b.errors());

            return new SearchResult(this, ids, errors);
        }
    }
}
