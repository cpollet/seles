package net.cpollet.read.v2.impl.methods;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.domain.Request;
import net.cpollet.read.v2.api.domain.Response;
import net.cpollet.read.v2.impl.AttributeDef;
import net.cpollet.read.v2.impl.ReadImpl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NestedMethod<IdType extends Id, NestedIdType extends Id> implements Method<IdType> {
    private final String prefix;
    private final AttributeDef<IdType> attribute;
    private final ReadImpl<NestedIdType> read;
    private final Function<Object, NestedIdType> idProvider;

    public NestedMethod(String prefix, AttributeDef<IdType> attribute, ReadImpl<NestedIdType> read, Function<Object, NestedIdType> idProvider) {
        this.prefix = prefix;
        this.attribute = attribute;
        this.read = read;
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

    private Response<NestedIdType> nestedFetch(Collection<String> attributes, Collection<NestedIdType> nestedIds) {
        return read.execute(new Request<>(
                nestedIds,
                attributes
        ));
    }
}
