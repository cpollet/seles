package net.cpollet.seles.client.domain;

import java.util.function.Function;

public class OwnerIdToPersonId implements Function<Object, PersonId> {
    @Override
    public PersonId apply(Object o) {
        return new PersonId((Integer) o);
    }
}
