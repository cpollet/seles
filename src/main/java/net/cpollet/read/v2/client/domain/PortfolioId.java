package net.cpollet.read.v2.client.domain;

import net.cpollet.read.v2.api.domain.Id;

import java.util.Objects;

public class PortfolioId implements Id<String> {
    private final String portfolioId;

    public PortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    @Override
    public String get() {
        return portfolioId;
    }

    @Override
    public String toString() {
        return portfolioId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioId that = (PortfolioId) o;
        return Objects.equals(portfolioId, that.portfolioId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(portfolioId);
    }
}
