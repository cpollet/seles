package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

/**
 * Implements a {@link InternalRequest} processing stage.
 *
 * Classes implementing this interface must not store any request and/or response relative data.
 */
public interface Stage<T extends Id, A> {
    /**
     * Ultimately transforms the {@link InternalRequest} to an {@link InternalResponse}. When delegating the request
     * execution to a lower stage, it is expected to to create a new modified instance of the request.
     */
    InternalResponse<T, A> execute(InternalRequest<T, A> request);
}
