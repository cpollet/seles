package net.cpollet.read.v2.impl.domain;

import net.cpollet.read.v2.api.Cache;
import net.cpollet.read.v2.api.domain.IdValidator;
import net.cpollet.read.v2.api.domain.Id;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CachedIdValidator<IdType extends Id> implements IdValidator<IdType>, Cache {
    private final IdValidator<IdType> nested;
    private final Set<IdType> validIds;

    public CachedIdValidator(IdValidator<IdType> nested) {
        this.nested = nested;
        this.validIds = Collections.synchronizedSet(new HashSet<>());
    }

    @Override
    public Collection<IdType> invalidIds(Collection<IdType> ids) {
        HashSet<IdType> idsToValidate = cachedIds(ids, validIds);

        Collection<IdType> invalidIds = nested.invalidIds(idsToValidate);

        updateCache(idsToValidate, invalidIds);

        return invalidIds;
    }

    private HashSet<IdType> cachedIds(Collection<IdType> ids, Collection<IdType> validIds) {
        HashSet<IdType> idsToValidate = new HashSet<>(ids);
        idsToValidate.removeAll(validIds);
        return idsToValidate;
    }

    private void updateCache(Collection<IdType> ids, Collection<IdType> invalidIds) {
        HashSet<IdType> newValidIds = new HashSet<>(ids);
        newValidIds.removeAll(invalidIds);
        validIds.addAll(newValidIds);
    }

    @Override
    public void invalidate() {
        // remove all elements of the final validIds Set
        validIds.retainAll(Collections.<IdType>emptySet());
    }
}
