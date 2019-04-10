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
public class IdsValidationStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    private final Stage<IdType, AttributeDef<IdType>> next;
    private final IdValidator<IdType> idValidator;

    public IdsValidationStage(IdValidator<IdType> idValidator, Stage<IdType, AttributeDef<IdType>> next) {
        this.next = next;
        this.idValidator = idValidator;
    }

    public InternalResponse<IdType, AttributeDef<IdType>> execute(final InternalRequest<IdType, AttributeDef<IdType>> request) {
        Collection<IdType> invalidIds = idValidator.invalidIds(request.ids());

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
