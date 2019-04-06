package net.cpollet.read.v2.client.domain;

import net.cpollet.read.v2.api.domain.Id;

import java.util.Objects;

public class AddressId implements Id<Integer> {
    private final Integer addressId;

     AddressId(Integer addressId) {
        this.addressId = addressId;
    }

    @Override
    public Integer get() {
        return addressId;
    }

    @Override
    public String toString() {
        return addressId.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddressId that = (AddressId) o;
        return Objects.equals(addressId, that.addressId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressId);
    }
}
