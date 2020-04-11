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
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.api.domain.IdValidator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class CachedIdValidator implements IdValidator, Cache {
    private final IdValidator nested;
    private final Set<Id> validIds;

    public CachedIdValidator(IdValidator nested) {
        this.nested = nested;
        this.validIds = Collections.synchronizedSet(new HashSet<>());
    }

    @Override
    public Collection<Id> invalidIds(Collection<Id> ids) {
        HashSet<Id> idsToValidate = cachedIds(ids, validIds);

        Collection<Id> invalidIds = nested.invalidIds(idsToValidate);

        updateCache(idsToValidate, invalidIds);

        return invalidIds;
    }

    private HashSet<Id> cachedIds(Collection<Id> ids, Collection<Id> validIds) {
        HashSet<Id> idsToValidate = new HashSet<>(ids);
        idsToValidate.removeAll(validIds);
        return idsToValidate;
    }

    private void updateCache(Collection<Id> ids, Collection<Id> invalidIds) {
        HashSet<Id> newValidIds = new HashSet<>(ids);
        newValidIds.removeAll(invalidIds);
        validIds.addAll(newValidIds);
    }

    @Override
    public void invalidate() {
        // remove all elements of the final validIds Set
        validIds.retainAll(Collections.<Id>emptySet());
    }
}
