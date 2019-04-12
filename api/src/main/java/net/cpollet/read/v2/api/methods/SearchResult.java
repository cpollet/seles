package net.cpollet.read.v2.api.methods;

import net.cpollet.read.v2.api.domain.Id;

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
        public <T extends Id> SearchResult<T> merge(SearchResult<T> a, SearchResult<T> b) {
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
