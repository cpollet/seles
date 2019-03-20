package net.cpollet.read.v2.client;

import net.cpollet.read.v2.api.IdValidator;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.client.domain.PortfolioId;

import java.util.Collection;
import java.util.Collections;

public class DefaultIdValidator<IdType extends Id> implements IdValidator<IdType> {
    @Override
    public Collection<IdType> invalidIds(Collection<IdType> ids) {
        if (ids.contains(new PortfolioId("999999"))) {
            return Collections.singleton((IdType) new PortfolioId("999999"));
        }

        return Collections.emptySet();
    }
}
