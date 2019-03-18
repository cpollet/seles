package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.client.domain.PortfolioId;
import net.cpollet.read.v2.impl.AttributeDef;
import net.cpollet.read.v2.impl.InternalRequest;
import net.cpollet.read.v2.impl.InternalResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class IdsValidationStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    private final Stage<IdType, AttributeDef<IdType>> next;

    public IdsValidationStage(Stage<IdType, AttributeDef<IdType>> next) {
        this.next = next;
    }

    public InternalResponse<IdType, AttributeDef<IdType>> execute(final InternalRequest<IdType, AttributeDef<IdType>> request) {
        Collection<IdType> invalidIds = invalidIds(request.ids());

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

    @SuppressWarnings("unchecked")
    private Collection<IdType> invalidIds(Collection<IdType> ids) {
        if (ids.contains(new PortfolioId("999999"))) {
            return Collections.singleton((IdType) new PortfolioId("999999"));
        }
        return Collections.emptySet();
    }
}
