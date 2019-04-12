package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

/**
 * Returns an empty {@link InternalResponse} no matter what the {@link InternalRequest} contains.
 *
 * @param <T> the entity's ID type
 * @param <A> the attributes types (usually either {@link String} or
 *            {@link net.cpollet.read.v2.api.attribute.AttributeDef}
 */
public final class EmptyResponseStage<T extends Id, A> implements Stage<T, A> {
    @Override
    public InternalResponse<T, A> execute(InternalRequest<T, A> request) {
        return new InternalResponse<>();
    }
}
