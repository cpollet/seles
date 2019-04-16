package net.cpollet.seles.impl.stages;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.api.methods.FetchResult;
import net.cpollet.seles.api.methods.Method;
import net.cpollet.seles.impl.attribute.AttributesGrouper;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Executes a READ {@link InternalRequest}.
 */
public final class ReadRequestExecutionStage<T extends Id> implements Stage<T, AttributeDef<T>> {
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
