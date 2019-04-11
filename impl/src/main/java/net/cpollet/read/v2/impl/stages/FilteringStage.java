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
public class FilteringStage<T extends Id> implements Stage<T, AttributeDef<T>> {
    private final Stage<T, AttributeDef<T>> next;
    private final AccessLevelPredicate<T> predicate;

    public FilteringStage(AccessLevelPredicate<T> predicate, Stage<T, AttributeDef<T>> next) {
        this.predicate = predicate;
        this.next = next;
    }

    @Override
    public InternalResponse<T, AttributeDef<T>> execute(InternalRequest<T, AttributeDef<T>> request) {
        Set<AttributeDef<T>> filteredAttributes = request.attributes().stream()
                .filter(predicate::test)
                .collect(Collectors.toSet());

        Map<T, Map<AttributeDef<T>, String>> filteredValues = request.ids().stream()
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
