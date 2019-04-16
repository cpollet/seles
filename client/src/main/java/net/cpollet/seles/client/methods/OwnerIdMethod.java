package net.cpollet.seles.client.methods;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.methods.CreateResult;
import net.cpollet.seles.api.methods.FetchResult;
import net.cpollet.seles.api.methods.Method;
import net.cpollet.seles.api.methods.SearchResult;
import net.cpollet.seles.client.domain.PortfolioId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OwnerIdMethod implements Method<PortfolioId> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OwnerIdMethod.class);

    @Override
    public FetchResult<PortfolioId> fetch(Principal principal, List<AttributeDef<PortfolioId>> attributes, Collection<PortfolioId> ids) {
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
    public Collection<String> update(Principal principal, Map<AttributeDef<PortfolioId>, Object> attributeValues, Collection<PortfolioId> ids) {
        ids.forEach(
                id -> attributeValues.forEach((a, v) -> LOGGER.info("UPDATE {}:{} -> {}", id, a, v))
        );

        return Collections.emptyList();
    }

    @Override
    public Collection<String> delete(Principal principal, List<AttributeDef<PortfolioId>> attributes, Collection<PortfolioId> ids) {
        ids.forEach(
                id -> attributes.forEach(a -> LOGGER.info("DELETE {}:{}", id, a))
        );

        return Collections.emptyList();
    }

    @Override
    public CreateResult<PortfolioId> create(Principal principal, Map<AttributeDef<PortfolioId>, Object> values) {
        values.forEach(
                (a, v) -> LOGGER.info("CREATE {} -> {}", a, v)
        );

        return new CreateResult<>(new PortfolioId("111111"));
    }

    @Override
    public SearchResult<PortfolioId> search(Principal principal, Map<AttributeDef<PortfolioId>, Object> values) {
        values.forEach(
                (a, v) -> LOGGER.info("SEARCH {} -> {}", a, v)
        );

        return new SearchResult<>(
                Collections.singletonList(new PortfolioId("222222"))
        );
    }
}
