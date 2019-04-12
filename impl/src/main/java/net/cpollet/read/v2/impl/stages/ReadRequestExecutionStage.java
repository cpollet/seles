package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.methods.FetchResult;
import net.cpollet.read.v2.api.methods.Method;
import net.cpollet.read.v2.impl.attribute.AttributesGrouper;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Executes a READ {@link InternalRequest}.
 */
public class ReadRequestExecutionStage<T extends Id> implements Stage<T, AttributeDef<T>> {
    @Override
    public InternalResponse<T, AttributeDef<T>> execute(final InternalRequest<T, AttributeDef<T>> request) {
        FetchResult<T> fetchResult = fetch(
                request.principal(),
                request.ids(),
                request.attributes(new AttributesGrouper<>())
        );

        return new InternalResponse<>(fetchResult.result())
                .withErrors(fetchResult.errors());
    }

    private FetchResult<T> fetch(Principal principal, Collection<T> ids, Map<Method<T>, List<AttributeDef<T>>> attributesGroupedByMethod) {
        return attributesGroupedByMethod.entrySet().stream()
                .map(e -> e.getKey().fetch(principal, e.getValue(), ids))
                .reduce(FetchResult.emptyResult(), FetchResult::merge);
    }
}
