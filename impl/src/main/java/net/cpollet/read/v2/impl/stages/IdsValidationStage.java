package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.domain.IdValidator;
import net.cpollet.read.v2.impl.Guarded;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Validates the IDs of the {@link InternalRequest} with the help of a {@link IdValidator} instance, removes the IDs
 * from the {@link InternalRequest} for the next {@link Stage} and puts errors in the {@link InternalResponse} for each
 * invalid id.
 */
public final class IdsValidationStage<T extends Id> implements Stage<T, AttributeDef<T>> {
    private final Stage<T, AttributeDef<T>> next;
    private final IdValidator<T> idValidator;

    public IdsValidationStage(IdValidator<T> idValidator, Stage<T, AttributeDef<T>> next) {
        this.next = next;
        this.idValidator = idValidator;
    }

    public InternalResponse<T, AttributeDef<T>> execute(final InternalRequest<T, AttributeDef<T>> request) {
        Collection<T> invalidIds = idValidator.invalidIds(request.ids());

        return next
                .execute(
                        request
                                .withoutIds(invalidIds)
                                .addGuardedFlagIf(!invalidIds.isEmpty(), Guarded.Flag.INVALID_IDS)
                )
                .withErrors(
                        invalidIds.stream()
                                .map(e -> String.format("[%s] is not a valid id", e))
                                .collect(Collectors.toList())
                );
    }
}
