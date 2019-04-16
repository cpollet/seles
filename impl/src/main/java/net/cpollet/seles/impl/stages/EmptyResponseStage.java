package net.cpollet.seles.impl.stages;

import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

/**
 * Returns an empty {@link InternalResponse} no matter what the {@link InternalRequest} contains.
 *
 * @param <T> the entity's ID type
 * @param <A> the attributes types (usually either {@link String} or
 *            {@link net.cpollet.seles.api.attribute.AttributeDef}
 */
public final class EmptyResponseStage<T extends Id, A> implements Stage<T, A> {
    @Override
    public InternalResponse<T, A> execute(InternalRequest<T, A> request) {
        return new InternalResponse<>();
    }
}
