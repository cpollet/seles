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

public class NestedMethod<IdType extends Id> implements Method<IdType> {
    private final String prefix;
    private final AttributeDef<IdType> attribute;
    private final ReadImpl read;
    private final Function<Object, ? extends Id> idProvider;

    public NestedMethod(String prefix, AttributeDef<IdType> attribute, ReadImpl read, Function<Object, ? extends Id> idProvider) {
        this.prefix = prefix;
        this.attribute = attribute;
        this.read = read;
        this.idProvider = idProvider;
    }

    @Override
    public FetchResult<IdType> fetch(List<AttributeDef<IdType>> attributes, Collection<IdType> ids) {
        Map<Id, IdType> nestedIdsToIds = attribute.method().fetch(Collections.singletonList(attribute), ids)
                .result().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> idProvider.apply(e.getValue().get(attribute)),
                        Map.Entry::getKey
                ));

        Map<String, AttributeDef<IdType>> attributeNamesToAttributeDefs = attributes.stream()
                .collect(Collectors.toMap(
                        AttributeDef::name,
                        a -> a
                ));

        Response<Id> response = nestedFetch(attributes, nestedIdsToIds.keySet());

        Map<IdType, Map<AttributeDef<IdType>, Object>> result = response.values().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> nestedIdsToIds.get(e.getKey()),
                        e -> e.getValue().entrySet().stream()
                                .collect(Collectors.toMap(
                                        e1 -> attributeNamesToAttributeDefs.get(prefix + "." + e1.getKey()),
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

    @SuppressWarnings("unchecked")
    private Response<Id> nestedFetch(List<AttributeDef<IdType>> attributes, Collection<Id> nestedIds) {
        return read.execute(new Request(
                nestedIds,
                attributes.stream()
                        .map(a -> a.name().substring(prefix.length() + 1))
                        .collect(Collectors.toSet())
        ));
    }
}
