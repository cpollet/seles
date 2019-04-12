package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.Guarded;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Validates that the {@link AttributeDef} are valid for the current {@link InternalRequest}, i.e. for instance that a
 * read {@link InternalRequest} only contains {@link AttributeDef} supporting the read {@link AttributeDef.Mode}. It
 * removes the attributes that are not valid and add an errors for each of them in the {@link InternalResponse}.
 */
public final class ModeValidationStage<T extends Id> implements Stage<T, AttributeDef<T>> {
    private final Stage<T, AttributeDef<T>> next;
    private final AttributeDef.Mode mode;

    public ModeValidationStage(AttributeDef.Mode mode, Stage<T, AttributeDef<T>> next) {
        this.next = next;
        this.mode = mode;
    }

    @Override
    public InternalResponse<T, AttributeDef<T>> execute(InternalRequest<T, AttributeDef<T>> request) {
        Collection<AttributeDef<T>> invalidModes = request.attributes(
                as -> as.stream()
                        .filter(a -> !a.supports(mode))
                        .collect(Collectors.toList())
        );

        return next.execute(
                request
                        .withoutAttributes(invalidModes)
                        .addGuardedFlagIf(!invalidModes.isEmpty(), Guarded.Flag.INVALID_MODE)
        ).withErrors(
                invalidModes.stream()
                        .map(a -> String.format("[%s] does not support [%s]", a, mode))
                        .collect(Collectors.toSet())
        );
    }
}
