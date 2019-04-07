package net.cpollet.read.v2.api.methods;

import net.cpollet.read.v2.api.domain.Id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SearchResult<IdType extends Id> {
    private static final SearchResult EMPTY = new SearchResult();

    private final MergeAlgorithm mergeAlgorithm;
    private final Set<IdType> ids;
    private final Collection<String> errors;

    public SearchResult(MergeAlgorithm mergeAlgorithm, Collection<IdType> ids, Collection<String> errors) {
        this.mergeAlgorithm = mergeAlgorithm;
        this.ids = Collections.unmodifiableSet(new HashSet<>(ids));
        this.errors = Collections.unmodifiableCollection(errors);
    }

    public SearchResult(Collection<IdType> ids, Collection<String> errors) {
        this(new MergeAlgorithm(), ids, errors);
    }

    public SearchResult(Collection<IdType> ids) {
        this(ids, Collections.emptyList());
    }

    private SearchResult() {
        this(Collections.emptyList(), Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    public static <IdType extends Id> SearchResult<IdType> emptyResult() {
        return (SearchResult<IdType>) EMPTY;
    }

    public Collection<IdType> ids() {
        return ids;
    }

    public Collection<String> errors() {
        return errors;
    }

    public SearchResult<IdType> merge(SearchResult<IdType> other) {
        return mergeAlgorithm.merge(this, other);
    }

    public static class MergeAlgorithm {
        public <IdType extends Id> SearchResult<IdType> merge(SearchResult<IdType> a, SearchResult<IdType> b) {
            if (a == EMPTY) {
                return b;
            }
            if (b == EMPTY) {
                return a;
            }
            Collection<IdType> ids = new HashSet<>(a.ids());
            ids.retainAll(b.ids());

            Collection<String> errors = new ArrayList<>(a.errors());
            errors.addAll(b.errors());

            return new SearchResult<>(this, ids, errors);
        }
    }
}
