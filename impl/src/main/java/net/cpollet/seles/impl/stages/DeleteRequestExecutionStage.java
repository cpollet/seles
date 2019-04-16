package net.cpollet.seles.impl.stages;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.impl.attribute.AttributesGrouper;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Executes a DELETE {@link InternalRequest}.
 */
public final class DeleteRequestExecutionStage<T extends Id> implements Stage<T, AttributeDef<T>> {
    @Override
    public InternalResponse<T, AttributeDef<T>> execute(InternalRequest<T, AttributeDef<T>> request) {
        List<String> errors = new ArrayList<>();

        request.attributes(new AttributesGrouper<>()).forEach(
                (method, attributes) -> errors.addAll(
                        method.delete(
                                request.principal(),
                                attributes,
                                request.ids()
                        )
                )
        );

        return new InternalResponse<T, AttributeDef<T>>()
                .withErrors(errors);
    }
}
