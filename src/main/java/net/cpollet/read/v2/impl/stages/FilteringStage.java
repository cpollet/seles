package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.AttributeDef;
import net.cpollet.read.v2.impl.InternalRequest;
import net.cpollet.read.v2.impl.InternalResponse;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FilteringStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    private final Stage<IdType, AttributeDef<IdType>> next;

    public FilteringStage(Stage<IdType, AttributeDef<IdType>> next) {
        this.next = next;
    }

    @Override
    public InternalResponse<IdType, AttributeDef<IdType>> execute(InternalRequest<IdType, AttributeDef<IdType>> request) {
        Set<AttributeDef<IdType>> filteredAttributes = request.attributes().stream()
                .filter(a -> a.name().equals("email"))
                .collect(Collectors.toSet());

        Map<IdType, Map<AttributeDef<IdType>, String>> filteredValues = request.ids().stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> filteredAttributes.stream()
                                .collect(Collectors.toMap(
                                        a -> a,
                                        a -> "*****"
                                )))
                );

        return next.execute(
                request.withoutAttributes(filteredAttributes)
        ).append(filteredValues);

    }
}
