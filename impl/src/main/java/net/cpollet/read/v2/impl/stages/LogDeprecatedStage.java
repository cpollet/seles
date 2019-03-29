package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.AttributeDef;
import net.cpollet.read.v2.impl.InternalRequest;
import net.cpollet.read.v2.impl.InternalResponse;

import java.util.stream.Collectors;

public class LogDeprecatedStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    private final Stage<IdType, AttributeDef<IdType>> next;

    public LogDeprecatedStage(Stage<IdType, AttributeDef<IdType>> next) {
        this.next = next;
    }

    @Override
    public InternalResponse<IdType, AttributeDef<IdType>> execute(InternalRequest<IdType, AttributeDef<IdType>> request) {
        return next.execute(request)
                .withMessages(
                        request.attributes().stream()
                                .filter(AttributeDef::deprecated)
                                .map(a -> String.format("[%s] is deprecated", a))
                                .collect(Collectors.toSet())
                );
    }
}
