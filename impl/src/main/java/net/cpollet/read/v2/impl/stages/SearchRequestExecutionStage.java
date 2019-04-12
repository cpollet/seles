package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.methods.SearchResult;
import net.cpollet.read.v2.impl.attribute.AttributesGrouper;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Executes a SEARCH {@link InternalRequest}.
 */
public class SearchRequestExecutionStage<T extends Id> implements Stage<T, AttributeDef<T>> {
    @Override
    public InternalResponse<T, AttributeDef<T>> execute(InternalRequest<T, AttributeDef<T>> request) {
        SearchResult<T> searchResult = request.attributes(new AttributesGrouper<>()).entrySet().stream()
                .map(e -> e.getKey().search(request.principal(), request.values(e.getValue())))
                .reduce(SearchResult.emptyResult(), SearchResult::merge);

        return new InternalResponse<T, AttributeDef<T>>(
                searchResult.ids().stream()
                        .collect(Collectors.toMap(
                                i -> i,
                                i -> Collections.emptyMap()
                        ))
        )
                .withErrors(
                        searchResult.errors()
                );
    }
}
