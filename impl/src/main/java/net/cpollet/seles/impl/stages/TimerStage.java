package net.cpollet.seles.impl.stages;

import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

/**
 * Compute execution time between the beginning of execution, until the lower {@link Stage} returns and puts the value
 * (in milliseconds) in the {@link InternalResponse}.
 */
public final class TimerStage<T extends Id> implements Stage<T, String> {
    private final Stage<T, String> next;

    public TimerStage(Stage<T, String> next) {
        this.next = next;
    }

    @Override
    public InternalResponse<T, String> execute(InternalRequest<T, String> request) {
        long start = System.currentTimeMillis();
        return next.execute(request)
                .withExecutionTime(
                        System.currentTimeMillis() - start
                );
    }
}
