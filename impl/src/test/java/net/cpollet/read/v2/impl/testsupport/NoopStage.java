package net.cpollet.read.v2.impl.testsupport;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;
import net.cpollet.read.v2.impl.stages.Stage;

public class NoopStage implements Stage<StringId, AttributeDef<StringId>> {
    @Override
    public InternalResponse<StringId, AttributeDef<StringId>> execute(InternalRequest<StringId, AttributeDef<StringId>> request) {
        return new InternalResponse<>();
    }
}
