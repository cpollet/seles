package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.Guarded;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

import java.util.function.Function;

/**
 * Halts the processing of the {@link InternalRequest} and returns an empty {@link InternalResponse} when the guard
 * function returns true.
 *
 * @see Guarded
 */
public final class RequestHaltStage<T extends Id, A> implements Stage<T, A> {
    private final Stage<T, A> next;
    private final Function<Guarded, Boolean> guard;

    public RequestHaltStage(Function<Guarded, Boolean> guard, Stage<T, A> next) {
        this.next = next;
        this.guard = guard;
    }

    @Override
    public InternalResponse<T, A> execute(InternalRequest<T, A> request) {
        if (guard.apply(request)) {
            return new InternalResponse<>();
        }

        return next.execute(request);
    }
}
