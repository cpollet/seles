package net.cpollet.read.v2.impl.methods;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.methods.CreateResult;
import net.cpollet.read.v2.api.methods.FetchResult;
import net.cpollet.read.v2.api.methods.Method;
import net.cpollet.read.v2.api.methods.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StandardMethod<IdType extends Id> implements Method<IdType> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StandardMethod.class);

    private final Function<Object, IdType> idTypeProvider;

    public StandardMethod(Function<Object, IdType> idTypeProvider) {
        this.idTypeProvider = idTypeProvider;
    }

    @Override
    public FetchResult<IdType> fetch(List<AttributeDef<IdType>> attributes, Collection<IdType> ids) {
        return new FetchResult<>(
                ids.stream()
                        .collect(Collectors.toMap(
                                id -> id,
                                id -> attributes.stream()
                                        .collect(Collectors.toMap(
                                                a -> a,
                                                a -> a.name() + ":" + id.get()
                                        ))
                                )

                        )
        );
    }

    @Override
    public Collection<String> update(Map<AttributeDef<IdType>, Object> attributeValues, Collection<IdType> ids) {
        ids.forEach(
                id -> attributeValues.forEach((a, v) -> LOGGER.info("UPDATE {}:{} -> {}", id, a, v))
        );

        return Collections.singletonList("update error");
    }

    @Override
    public Collection<String> delete(List<AttributeDef<IdType>> attributes, Collection<IdType> ids) {
        ids.forEach(
                id -> attributes.forEach(a -> LOGGER.info("DELETE {}:{}", id, a))
        );

        return Collections.singletonList("delete error");
    }

    @Override
    public CreateResult<IdType> create(Map<AttributeDef<IdType>, Object> values) {
        values.forEach(
                (a, v) -> LOGGER.info("CREATE {} -> {}", a, v)
        );

        return new CreateResult<>(idTypeProvider.apply("100000"));
    }

    @Override
    public SearchResult<IdType> search(Map<AttributeDef<IdType>, Object> values) {
        values.forEach(
                (a, v) -> LOGGER.info("SEARCH {} -> {}", a, v)
        );

        return new SearchResult<>(
                Arrays.asList(idTypeProvider.apply("100000"), idTypeProvider.apply("222222"))
        );
    }
}
