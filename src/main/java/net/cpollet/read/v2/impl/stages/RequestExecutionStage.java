package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.AttributeDef;
import net.cpollet.read.v2.impl.InternalRequest;
import net.cpollet.read.v2.impl.InternalResponse;
import net.cpollet.read.v2.impl.methods.FetchResult;
import net.cpollet.read.v2.impl.methods.Method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestExecutionStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    @Override
    public InternalResponse<IdType, AttributeDef<IdType>> execute(final InternalRequest<IdType, AttributeDef<IdType>> request) {
        return new InternalResponse<>(
                flatten(
                        fetch(
                                request.ids(),
                                groupByMethod(request.attributes())
                        )
                )
        );
    }

    private Map<IdType, Map<AttributeDef<IdType>, Object>> flatten(List<Map<IdType, Map<AttributeDef<IdType>, Object>>> listOfMaps) {
        Map<IdType, Map<AttributeDef<IdType>, Object>> result = new HashMap<>();

        for (Map<IdType, Map<AttributeDef<IdType>, Object>> map : listOfMaps) {
            map.forEach((id, value) -> {
                result.putIfAbsent(id, new HashMap<>());
                result.get(id).putAll(value);
            });
        }

        return result;
    }

    private List<Map<IdType, Map<AttributeDef<IdType>, Object>>> fetch(Collection<IdType> ids, Map<Method<IdType>, List<AttributeDef<IdType>>> attributesGroupedByMethod) {
        return attributesGroupedByMethod.entrySet().parallelStream()
                .map(e -> e.getKey().fetch(e.getValue(), ids))
                .map(FetchResult::result) // FIXME do something about errors ;)
                .collect(Collectors.toList());
    }

    private Map<Method<IdType>, List<AttributeDef<IdType>>> groupByMethod(Collection<AttributeDef<IdType>> attributes) {
        Map<Method<IdType>, List<AttributeDef<IdType>>> attributesGroupedByMethod = new HashMap<>();

        for (AttributeDef<IdType> attribute : attributes) {
            attributesGroupedByMethod.putIfAbsent(attribute.method(), new ArrayList<>());
            attributesGroupedByMethod.get(attribute.method()).add(attribute);
        }

        return attributesGroupedByMethod;
    }
}
