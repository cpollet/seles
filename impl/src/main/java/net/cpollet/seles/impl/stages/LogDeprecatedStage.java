package net.cpollet.seles.impl.stages;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

import java.util.stream.Collectors;

/**
 * Puts warning in the {@link InternalResponse} for each deprecated {@link AttributeDef} used in the
 * {@link InternalRequest}.
 */
public final class LogDeprecatedStage<T extends Id> implements Stage<T, AttributeDef<T>> {
    private final Stage<T, AttributeDef<T>> next;

    public LogDeprecatedStage(Stage<T, AttributeDef<T>> next) {
        this.next = next;
    }

    @Override
    public InternalResponse<T, AttributeDef<T>> execute(InternalRequest<T, AttributeDef<T>> request) {
        return next.execute(request)
                .withMessages(
                        request.attributes().stream()
                                .filter(AttributeDef::deprecated)
                                .map(a -> String.format("[%s] is deprecated", a))
                                .collect(Collectors.toSet())
                );
    }
}
