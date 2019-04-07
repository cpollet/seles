package net.cpollet.read.v2.client.methods;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.methods.CreateResult;
import net.cpollet.read.v2.api.methods.FetchResult;
import net.cpollet.read.v2.api.methods.Method;
import net.cpollet.read.v2.api.methods.SearchResult;
import net.cpollet.read.v2.client.domain.PersonId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OwnedPortfolioIdMethod implements Method<PersonId> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OwnedPortfolioIdMethod.class);

    @Override
    public FetchResult<PersonId> fetch(List<AttributeDef<PersonId>> attributes, Collection<PersonId> ids) {
        return new FetchResult<>(
                ids.stream()
                        .collect(Collectors.toMap(
                                id -> id,
                                id -> attributes.stream()
                                        .collect(Collectors.toMap(
                                                a -> a,
                                                a -> String.valueOf(id.get())
                                        ))
                                )
                        )
        );
    }

    @Override
    public Collection<String> update(Map<AttributeDef<PersonId>, Object> attributeValues, Collection<PersonId> ids) {
        ids.forEach(
                id -> attributeValues.forEach((a, v) -> LOGGER.info("UPDATE {}:{} -> {}", id, a, v))
        );

        return Collections.emptyList();
    }

    @Override
    public Collection<String> delete(List<AttributeDef<PersonId>> attributes, Collection<PersonId> ids) {
        ids.forEach(
                id -> attributes.forEach(a -> LOGGER.info("DELETE {}:{}", id, a))
        );

        return Collections.emptyList();
    }

    @Override
    public CreateResult<PersonId> create(Map<AttributeDef<PersonId>, Object> values) {
        values.forEach(
                (a, v) -> LOGGER.info("CREATE {} -> {}", a, v)
        );

        return new CreateResult<>(new PersonId(111111));
    }

    @Override
    public SearchResult<PersonId> search(Map<AttributeDef<PersonId>, Object> values) {
        values.forEach(
                (a, v) -> LOGGER.info("SEARCH {} -> {}", a, v)
        );

        return new SearchResult<>(
                Collections.singletonList(new PersonId(111111))
        );
    }
}
