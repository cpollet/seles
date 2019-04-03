package net.cpollet.read.v2.impl.methods;

import net.cpollet.read.v2.api.execution.Executor;
import net.cpollet.read.v2.api.methods.FetchResult;
import net.cpollet.read.v2.api.methods.Method;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.execution.Request;
import net.cpollet.read.v2.api.execution.Response;
import net.cpollet.read.v2.api.attribute.AttributeDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NestedMethod<IdType extends Id, NestedIdType extends Id> implements Method<IdType> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NestedMethod.class);

    private final String prefix;
    private final AttributeDef<IdType> attribute;
    private final Executor<NestedIdType> executor;
    private final Function<Object, NestedIdType> idProvider;

    public NestedMethod(String prefix, AttributeDef<IdType> attribute, Executor<NestedIdType> executor, Function<Object, NestedIdType> idProvider) {
        this.prefix = prefix;
        this.attribute = attribute;
        this.executor = executor;
        this.idProvider = idProvider;
    }

    @Override
    public FetchResult<IdType> fetch(List<AttributeDef<IdType>> attributes, Collection<IdType> ids) {
        Map<NestedIdType, IdType> nestedIdsToIds = attribute.method().fetch(Collections.singletonList(attribute), ids)
                .result().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> idProvider.apply(e.getValue().get(attribute)),
                        Map.Entry::getKey
                ));

        Map<String, AttributeDef<IdType>> attributeNamesToAttributeDefs = attributes.stream()
                .collect(Collectors.toMap(
                        a -> a.name().substring(prefix.length() + 1),
                        a -> a
                ));

        Response<NestedIdType> response = nestedFetch(attributeNamesToAttributeDefs.keySet(), nestedIdsToIds.keySet());

        Map<IdType, Map<AttributeDef<IdType>, Object>> result = response.values().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> nestedIdsToIds.get(e.getKey()),
                        e -> e.getValue().entrySet().stream()
                                .collect(Collectors.toMap(
                                        v -> attributeNamesToAttributeDefs.get(v.getKey()),
                                        Map.Entry::getValue
                                ))
                ));

        return new FetchResult<>(
                result,
                response.errors().stream()
                        .map(e -> String.format("[%s].%s", prefix, e))
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public Collection<String> update(Map<AttributeDef<IdType>, Object> attributeValues, Collection<IdType> ids) {
        ids.forEach(
                id -> attributeValues.forEach((a, v) -> LOGGER.info("{}:{} -> {}", id, a, v))
        );

        return Collections.emptyList();
    }

    private Response<NestedIdType> nestedFetch(Collection<String> attributes, Collection<NestedIdType> nestedIds) {
        return executor.read(new Request<>(
                nestedIds,
                attributes
        ));
    }

    public boolean supports(String attributeName) {
        return attributeName.startsWith(prefix + ".");
    }
}