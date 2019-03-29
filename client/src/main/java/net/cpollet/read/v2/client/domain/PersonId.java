package net.cpollet.read.v2.client.domain;

import net.cpollet.read.v2.api.domain.Id;

import java.util.Objects;

public class PersonId implements Id<Integer> {
    private final Integer personId;

    public PersonId(int personId) {
        this.personId = personId;
    }

    @Override
    public Integer get() {
        return personId;
    }

    @Override
    public String toString() {
        return personId.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonId that = (PersonId) o;
        return Objects.equals(personId, that.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personId);
    }
}
