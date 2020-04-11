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

import net.cpollet.seles.api.conversion.ConversionException;
import net.cpollet.seles.api.conversion.ValueConverter;

public final class NoopValueConverter implements ValueConverter<Object> {
    private static final NoopValueConverter instance = new NoopValueConverter();

    private NoopValueConverter() {
        // nothing
    }

    public static ValueConverter instance() {
        return instance;
    }

    @Override
    public Object toExternalValue(Object attribute, Object value) throws ConversionException {
        return value;
    }

    @Override
    public Object toInternalValue(Object attribute, Object value) throws ConversionException {
        return value;
    }
}
