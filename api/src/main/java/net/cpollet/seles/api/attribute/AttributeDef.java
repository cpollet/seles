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
package net.cpollet.seles.api.attribute;

import net.cpollet.seles.api.attribute.printer.AttributePrinter;
import net.cpollet.seles.api.conversion.ValueConverter;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.api.methods.Method;

import java.util.Objects;
import java.util.Set;

public final class AttributeDef {
    private final String name;
    private final AccessLevel accessLevel;
    private final boolean deprecated;
    private final Method<Id> method;
    private final Set<Mode> modes;
    private final ValueConverter<AttributeDef> converter;
    private final ValueConverter<AttributeDef> caster;

    public AttributeDef(
            String name,
            AccessLevel accessLevel,
            boolean deprecated,
            Method<Id> method,
            Set<Mode> modes,
            ValueConverter<AttributeDef> converter,
            ValueConverter<AttributeDef> caster) {
        this.name = name;
        this.accessLevel = accessLevel;
        this.deprecated = deprecated;
        this.method = method;
        this.modes = modes;
        this.converter = converter;
        this.caster = caster;
    }

    public String name() {
        return name;
    }

    public Method<Id> method() {
        return method;
    }

    public boolean supports(Mode mode) {
        return modes.contains(mode);
    }

    public Set<Mode> modes() {
        return modes;
    }

    public boolean deprecated() {
        return deprecated;
    }

    public AccessLevel accessLevel() {
        return accessLevel;
    }

    public ValueConverter<AttributeDef> converter() {
        return converter;
    }

    public ValueConverter<AttributeDef> caster() {
        return caster;
    }

    public <P> P print(AttributePrinter<P> printer) {
        return printer
                .name(name)
                .accessLevel(accessLevel)
                .deprecated(deprecated)
                .modes(modes)
                .print();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttributeDef that = (AttributeDef) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public enum Mode {
        READ,
        WRITE,
        DELETE,
        CREATE,
        SEARCH
    }
}
