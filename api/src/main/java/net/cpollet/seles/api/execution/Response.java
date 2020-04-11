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
package net.cpollet.seles.api.execution;

import net.cpollet.seles.api.domain.Id;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class Response<T extends Id> {
    private final Map<T, Map<String, Object>> values;
    private final Collection<String> errors;
    private final Collection<String> messages;
    private final long executionTime;

    public Response(Map<T, Map<String, Object>> values, Collection<String> errors, Collection<String> messages, long executionTime) {
        this.values = Collections.unmodifiableMap(values);
        this.errors = Collections.unmodifiableCollection(errors);
        this.messages = Collections.unmodifiableCollection(messages);
        this.executionTime = executionTime;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public Map<T, Map<String, Object>> values() {
        return values;
    }

    public Collection<String> errors() {
        return errors;
    }

    public Collection<String> messages() {
        return messages;
    }

    public long executionTime() {
        return executionTime;
    }
}
