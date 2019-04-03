package net.cpollet.read.v2.client.domain;

import java.util.function.Function;

public class AddressIdToAddressId implements Function<Object, AddressId> {
    @Override
    public AddressId apply(Object o) {
        return new AddressId((Integer) o);
    }
}
