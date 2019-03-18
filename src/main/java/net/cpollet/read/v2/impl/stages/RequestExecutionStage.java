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

public class RequestExecutionStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    @Override
    public InternalResponse<IdType, AttributeDef<IdType>> execute(final InternalRequest<IdType, AttributeDef<IdType>> request) {
        FetchResult<IdType> fetchResult = fetch(
                request.ids(),
                groupByMethod(request.attributes())
        );

        return new InternalResponse<>(fetchResult.result())
                .withErrors(fetchResult.errors());
    }

    private FetchResult<IdType> fetch(Collection<IdType> ids, Map<Method<IdType>, List<AttributeDef<IdType>>> attributesGroupedByMethod) {
        return attributesGroupedByMethod.entrySet().stream()
                .map(e -> e.getKey().fetch(e.getValue(), ids))
                .reduce(new FetchResult<>(), FetchResult::append);
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
