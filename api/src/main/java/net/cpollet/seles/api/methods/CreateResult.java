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

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public final class CreateResult {
    private final Id id;
    private final Collection<String> errors;

    private CreateResult(Id id, Collection<String> errors) {
        this.id = id;
        this.errors = Collections.unmodifiableCollection(errors);
    }

    public CreateResult(Id id) {
        this(id, Collections.emptySet());
    }

    public CreateResult(Collection<String> errors) {
        this(null, errors);
    }

    public Optional<Id> id() {
        return Optional.ofNullable(id);
    }

    public Collection<String> errors() {
        return errors;
    }
}
