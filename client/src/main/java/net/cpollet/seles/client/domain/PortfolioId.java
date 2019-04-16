package net.cpollet.seles.client.domain;

import net.cpollet.seles.api.domain.Id;

import java.util.Objects;

public class PortfolioId implements Id<String> {
    private final String id;

    public PortfolioId(String id) {
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
        PortfolioId that = (PortfolioId) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
