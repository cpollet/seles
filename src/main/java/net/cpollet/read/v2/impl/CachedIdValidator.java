package net.cpollet.read.v2.impl;

import net.cpollet.read.v2.api.IdValidator;
import net.cpollet.read.v2.api.domain.Id;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CachedIdValidator<IdType extends Id> implements IdValidator<IdType> {
    private final IdValidator<IdType> nested;
    private final Set<IdType> validIds;

    public CachedIdValidator(IdValidator<IdType> nested) {
        this.nested = nested;
        this.validIds = new HashSet<>();
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
}
