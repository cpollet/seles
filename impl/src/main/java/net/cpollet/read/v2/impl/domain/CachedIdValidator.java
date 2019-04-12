package net.cpollet.read.v2.impl.domain;

import net.cpollet.read.v2.api.Cache;
import net.cpollet.read.v2.api.domain.IdValidator;
import net.cpollet.read.v2.api.domain.Id;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class CachedIdValidator<T extends Id> implements IdValidator<T>, Cache {
    private final IdValidator<T> nested;
    private final Set<T> validIds;

    public CachedIdValidator(IdValidator<T> nested) {
        this.nested = nested;
        this.validIds = Collections.synchronizedSet(new HashSet<>());
    }

    @Override
    public Collection<T> invalidIds(Collection<T> ids) {
        HashSet<T> idsToValidate = cachedIds(ids, validIds);

        Collection<T> invalidIds = nested.invalidIds(idsToValidate);

        updateCache(idsToValidate, invalidIds);

        return invalidIds;
    }

    private HashSet<T> cachedIds(Collection<T> ids, Collection<T> validIds) {
        HashSet<T> idsToValidate = new HashSet<>(ids);
        idsToValidate.removeAll(validIds);
        return idsToValidate;
    }

    private void updateCache(Collection<T> ids, Collection<T> invalidIds) {
        HashSet<T> newValidIds = new HashSet<>(ids);
        newValidIds.removeAll(invalidIds);
        validIds.addAll(newValidIds);
    }

    @Override
    public void invalidate() {
        // remove all elements of the final validIds Set
        validIds.retainAll(Collections.<T>emptySet());
    }
}
