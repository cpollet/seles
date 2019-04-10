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
public class SearchRequestExecutionStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    @Override
    public InternalResponse<IdType, AttributeDef<IdType>> execute(InternalRequest<IdType, AttributeDef<IdType>> request) {
        SearchResult<IdType> searchResult = request.attributes(new AttributesGrouper<>()).entrySet().stream()
                .map(e -> e.getKey().search(request.values(e.getValue())))
                .reduce(SearchResult.emptyResult(), SearchResult::merge);

        return new InternalResponse<IdType, AttributeDef<IdType>>(
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
