package net.cpollet.read.v2.client.methods;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.methods.FetchResult;
import net.cpollet.read.v2.api.methods.Method;
import net.cpollet.read.v2.client.domain.PortfolioId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OwnerIdMethod implements Method<PortfolioId> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerIdMethod.class);

    @Override
    public FetchResult<PortfolioId> fetch(List<AttributeDef<PortfolioId>> attributes, Collection<PortfolioId> ids) {
        return new FetchResult<>(
                ids.stream()
                        .collect(Collectors.toMap(
                                id -> id,
                                id -> attributes.stream()
                                        .collect(Collectors.toMap(
                                                a -> a,
                                                a -> Integer.valueOf(id.get())
                                        ))
                                )

                        )
        );
    }

    @Override
    public Collection<String> update(Map<AttributeDef<PortfolioId>, Object> attributeValues, Collection<PortfolioId> ids) {
        ids.forEach(
                id -> attributeValues.forEach((a, v) -> LOGGER.info("UPDATE {}:{} -> {}", id, a, v))
        );

        return Collections.emptyList();
    }

    @Override
    public Collection<String> delete(List<AttributeDef<PortfolioId>> attributes, Collection<PortfolioId> ids) {
        ids.forEach(
                id -> attributes.forEach(a -> LOGGER.info("DELETE {}:{}", id, a))
        );

        return Collections.emptyList();
    }
}
