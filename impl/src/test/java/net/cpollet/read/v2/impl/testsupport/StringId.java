package net.cpollet.read.v2.impl.testsupport;

import net.cpollet.read.v2.api.domain.Id;

import java.util.Objects;

public class StringId implements Id<String> {
    private final String id;

    public StringId(String id) {
        this.id = id;
    }

    @Override
    public String get() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringId stringId = (StringId) o;
        return Objects.equals(id, stringId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
