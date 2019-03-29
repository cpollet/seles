package net.cpollet.read.v2.impl;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.methods.Method;
import net.cpollet.read.v2.impl.methods.NestedMethod;
import net.cpollet.read.v2.impl.stages.ConversionException;
import net.cpollet.read.v2.impl.stages.ValueConverter;

import java.util.Objects;

public class AttributeDef<IdType extends Id> {
    private final String name;
    private final Method<IdType> method;

    public AttributeDef(String name, Method<IdType> method) {
        this.name = name;
        this.method = method;
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

    public boolean nested() {
        return method instanceof NestedMethod;
    }

    public boolean filtered() {
        return name.equals("email") || name.equals("hidden") || name.equals("filtered");
    }

    public ValueConverter<AttributeDef<IdType>> converter() {
        return new ValueConverter<AttributeDef<IdType>>() {
            @Override
            public Object toExternalValue(AttributeDef<IdType> attribute, Object value) throws ConversionException {
                if (attribute.name().equals("currency") && value.equals("currency:100000")) {
                    throw new ConversionException("why not");
                }
                return attribute.nested() ? value : String.format("externalValue(%s)", value);
            }

            @Override
            public Object toInternalValue(AttributeDef<IdType> attribute, Object value) throws ConversionException {
                return value;
            }
        };
    }

    public ValueConverter<AttributeDef<IdType>> caster() {
        return new ValueConverter<AttributeDef<IdType>>() {
            @Override
            public Object toExternalValue(AttributeDef<IdType> attribute, Object value) throws ConversionException {
                return attribute.nested() ? value : String.format("externalCast(%s)", value);
            }

            @Override
            public Object toInternalValue(AttributeDef<IdType> attribute, Object value) throws ConversionException {
                return value;
            }
        };
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
