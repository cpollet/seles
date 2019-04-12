package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.Guarded;
import net.cpollet.read.v2.impl.attribute.AttributesGrouper;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Executes a UPDATE {@link InternalRequest}.
 */
public class UpdateRequestExecutionStage<T extends Id> implements Stage<T, AttributeDef<T>> {
    private final Stage<T, AttributeDef<T>> next;

    public UpdateRequestExecutionStage(Stage<T, AttributeDef<T>> next) {
        this.next = next;
    }

    @Override
    public InternalResponse<T, AttributeDef<T>> execute(InternalRequest<T, AttributeDef<T>> request) {
        List<String> errors = new ArrayList<>();

        request.attributes(new AttributesGrouper<>()).forEach(
                (method, attributes) -> errors.addAll(
                        method.update(
                                request.principal(),
                                request.values(attributes),
                                request.ids()
                        )
                )
        );

        return next
                .execute(
                        request.addGuardedFlagIf(!errors.isEmpty(), Guarded.Flag.UPDATE_ERROR)
                )
                .withErrors(errors);
    }
}
