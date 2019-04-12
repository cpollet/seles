package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.attribute.AttributesGrouper;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

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
