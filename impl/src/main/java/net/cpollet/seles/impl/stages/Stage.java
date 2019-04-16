package net.cpollet.seles.impl.stages;

import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

/**
 * Implements a {@link InternalRequest} processing stage.
 *
 * Classes implementing this interface must be stateless.
 */
public interface Stage<T extends Id, A> {
    /**
     * Ultimately transforms the {@link InternalRequest} to an {@link InternalResponse}. When delegating the request
     * execution to a lower stage, it is expected to to create a new modified instance of the request.
     */
    InternalResponse<T, A> execute(InternalRequest<T, A> request);
}
