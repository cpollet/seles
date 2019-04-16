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
package net.cpollet.seles.impl.domain;

import net.cpollet.seles.api.Cache;
import net.cpollet.seles.api.domain.IdValidator;
import net.cpollet.seles.api.domain.Id;

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
