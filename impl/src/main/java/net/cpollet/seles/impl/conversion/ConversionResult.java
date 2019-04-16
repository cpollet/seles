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
package net.cpollet.seles.impl.conversion;

import java.util.Collection;
import java.util.Collections;

public final class ConversionResult<T> {
    private final T result;
    private final Collection<String> errors;

    public ConversionResult(T result, Collection<String> errors) {
        this.result = result;
        this.errors = errors;
    }

    public ConversionResult(T result) {
        this(result, Collections.emptyList());
    }

    public T result() {
        return result;
    }

    public Collection<String> errors() {
        return errors;
    }
}
