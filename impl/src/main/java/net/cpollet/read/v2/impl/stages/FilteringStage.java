package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.attribute.AccessLevelPredicate;
import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Removes {@link AttributeDef} from the {@link InternalRequest} according to the ({@link AttributeDef#accessLevel()}
 * and passes the newly created request object to the lower {@link Stage}.
 * Puts '*****' as a value in the {@link InternalResponse} for each removed attribute.
 */
public class FilteringStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    private final Stage<IdType, AttributeDef<IdType>> next;
    private final AccessLevelPredicate<IdType> predicate;

    public FilteringStage(AccessLevelPredicate<IdType> predicate, Stage<IdType, AttributeDef<IdType>> next) {
        this.predicate = predicate;
        this.next = next;
    }

    @Override
    public InternalResponse<IdType, AttributeDef<IdType>> execute(InternalRequest<IdType, AttributeDef<IdType>> request) {
        Set<AttributeDef<IdType>> filteredAttributes = request.attributes().stream()
                .filter(predicate::test)
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
        )
                .append(filteredValues)
                .withMessages(
                        filteredAttributes.stream()
                                .map(a -> String.format("[%s] is filtered", a.name()))
                                .collect(Collectors.toSet())
                );
    }
}
