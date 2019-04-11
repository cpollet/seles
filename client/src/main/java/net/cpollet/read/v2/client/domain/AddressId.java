package net.cpollet.read.v2.client.domain;

import net.cpollet.read.v2.api.domain.Id;

import java.util.Objects;

public class AddressId implements Id<Integer> {
    private final Integer id;

     AddressId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer get() {
        return id;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressId that = (AddressId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
