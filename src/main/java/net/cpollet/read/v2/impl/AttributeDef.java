package net.cpollet.read.v2.impl;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.methods.Method;
import net.cpollet.read.v2.impl.methods.NestedMethod;
import net.cpollet.read.v2.impl.methods.NoopMethod;

import java.util.Objects;

public class AttributeDef<IdType extends Id> {
    private final String name;
    private final Method<IdType> method;
    private final boolean invalid;

    public AttributeDef(String name, Method<IdType> method) {
        this(name, method, false);
    }

    private AttributeDef(String name, Method<IdType> method, boolean invalid) {
        this.name = name;
        this.method = method;
        this.invalid = invalid;
    }

    @SuppressWarnings("unchecked")
    public static <IdType extends Id> AttributeDef<IdType> invalid(String name) {
        return new AttributeDef(name, NoopMethod.get(), true);
    }

    public String name() {
        return name;
    }

    public Method<IdType> method() {
        return method;
    }

    public boolean invalid() {
        return invalid;
    }

    public boolean deprecated() {
        return name.equals("currency");
    }

    public boolean nested() {
        return method instanceof NestedMethod;
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

    public boolean filtered() {
        return name.equals("email") || name.equals("hidden") || name.equals("filtered");
    }
}
