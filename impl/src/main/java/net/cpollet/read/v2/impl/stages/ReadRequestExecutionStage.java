package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.impl.attribute.AttributesGrouper;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;
import net.cpollet.read.v2.api.methods.FetchResult;
import net.cpollet.read.v2.api.methods.Method;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ReadRequestExecutionStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    @Override
    public InternalResponse<IdType, AttributeDef<IdType>> execute(final InternalRequest<IdType, AttributeDef<IdType>> request) {
        FetchResult<IdType> fetchResult = fetch(
                request.ids(),
                request.attributes(new AttributesGrouper<>())
        );

        return new InternalResponse<>(fetchResult.result())
                .withErrors(fetchResult.errors());
    }

    private FetchResult<IdType> fetch(Collection<IdType> ids, Map<Method<IdType>, List<AttributeDef<IdType>>> attributesGroupedByMethod) {
        return attributesGroupedByMethod.entrySet().stream()
                .map(e -> e.getKey().fetch(e.getValue(), ids))
                .reduce(new FetchResult<>(), FetchResult::merge);
    }
}
