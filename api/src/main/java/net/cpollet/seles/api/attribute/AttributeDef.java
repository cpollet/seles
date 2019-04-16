package net.cpollet.seles.api.attribute;

import net.cpollet.seles.api.attribute.printer.AttributePrinter;
import net.cpollet.seles.api.conversion.ValueConverter;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.api.methods.Method;

import java.util.Objects;
import java.util.Set;

public final class AttributeDef<T extends Id> {
    private final String name;
    private final AccessLevel accessLevel;
    private final boolean deprecated;
    private final Method<T> method;
    private final Set<Mode> modes;
    private final ValueConverter<AttributeDef<T>> converter;
    private final ValueConverter<AttributeDef<T>> caster;

    public enum Mode {
        READ,
        WRITE,
        DELETE,
        CREATE,
        SEARCH
    }

    public AttributeDef(
            String name,
            AccessLevel accessLevel,
            boolean deprecated,
            Method<T> method,
            Set<Mode> modes,
            ValueConverter<AttributeDef<T>> converter,
            ValueConverter<AttributeDef<T>> caster) {
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

    public Method<T> method() {
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

    public ValueConverter<AttributeDef<T>> converter() {
        return converter;
    }

    public ValueConverter<AttributeDef<T>> caster() {
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
}
