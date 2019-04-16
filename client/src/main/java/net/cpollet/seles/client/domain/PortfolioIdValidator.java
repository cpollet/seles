package net.cpollet.seles.client.domain;

import net.cpollet.seles.api.domain.IdValidator;

import java.util.Collection;
import java.util.Collections;

/**
 * This is an example of a IdValidator for the Portfolio context. If statically reports portfolioId 999999 as an invalid
 * portfolioId.
 */
public class PortfolioIdValidator implements IdValidator<PortfolioId> {
    @Override
    public Collection<PortfolioId> invalidIds(Collection<PortfolioId> ids) {
        if (ids.contains(new PortfolioId("999999"))) {
            return Collections.singleton(new PortfolioId("999999"));
        }

        return Collections.emptyList();
    }
}
