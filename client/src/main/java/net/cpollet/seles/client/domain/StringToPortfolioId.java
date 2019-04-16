package net.cpollet.seles.client.domain;

import java.util.function.Function;

public class StringToPortfolioId implements Function<Object, PortfolioId> {
    @Override
    public PortfolioId apply(Object o) {
        return new PortfolioId((String) o);
    }
}
