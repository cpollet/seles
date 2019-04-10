package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

/**
 * Compute execution time between the beginning of execution, until the lower {@link Stage} returns and puts the value
 * (in milliseconds) in the {@link InternalResponse}.
 */
public class TimerStage<IdType extends Id> implements Stage<IdType, String> {
    private final Stage<IdType, String> next;

    public TimerStage(Stage<IdType, String> next) {
        this.next = next;
    }

    @Override
    public InternalResponse<IdType, String> execute(InternalRequest<IdType, String> request) {
        long start = System.currentTimeMillis();
        return next.execute(request)
                .withExecutionTime(
                        System.currentTimeMillis() - start
                );
    }
}
