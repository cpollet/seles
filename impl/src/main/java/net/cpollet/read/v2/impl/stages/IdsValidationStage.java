package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.IdValidator;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

import java.util.Collection;
import java.util.stream.Collectors;

public class IdsValidationStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    private final Stage<IdType, AttributeDef<IdType>> next;
    private final IdValidator<IdType> idValidator;

    public IdsValidationStage(Stage<IdType, AttributeDef<IdType>> next, IdValidator<IdType> idValidator) {
        this.next = next;
        this.idValidator = idValidator;
    }

    public InternalResponse<IdType, AttributeDef<IdType>> execute(final InternalRequest<IdType, AttributeDef<IdType>> request) {
        Collection<IdType> invalidIds = idValidator.invalidIds(request.ids());

        return next
                .execute(
                        request.withoutIds(invalidIds)
                )
                .withErrors(
                        invalidIds.stream()
                                .map(e -> String.format("[%s] is not a valid id", e))
                                .collect(Collectors.toList())
                );
    }
}
