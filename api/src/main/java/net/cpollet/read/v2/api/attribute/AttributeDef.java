package net.cpollet.read.v2.api.attribute;

import net.cpollet.read.v2.api.conversion.ValueConverter;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.methods.Method;

import java.util.Objects;

public class AttributeDef<IdType extends Id> {
    private final String name;
    private final Method<IdType> method;
    private final ValueConverter<AttributeDef<IdType>> converter;
    private final ValueConverter<AttributeDef<IdType>> caster;

    public AttributeDef(String name, Method<IdType> method, ValueConverter<AttributeDef<IdType>> converter, ValueConverter<AttributeDef<IdType>> caster) {
        this.name = name;
        this.method = method;
        this.converter = converter;
        this.caster = caster;
    }

    public String name() {
        return name;
    }

    public Method<IdType> method() {
        return method;
    }

    public boolean deprecated() {
        return name.equals("currency");
    }

    public boolean filtered() {
        return name.equals("email") || name.equals("hidden") || name.equals("filtered");
    }

    public ValueConverter<AttributeDef<IdType>> converter() {
        return converter;
    }

    public ValueConverter<AttributeDef<IdType>> caster() {
        return caster;
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
