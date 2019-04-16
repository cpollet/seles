package net.cpollet.seles.impl.testsupport;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;
import net.cpollet.seles.impl.stages.Stage;

public class NoopStage implements Stage<StringId, AttributeDef<StringId>> {
    @Override
    public InternalResponse<StringId, AttributeDef<StringId>> execute(InternalRequest<StringId, AttributeDef<StringId>> request) {
        return new InternalResponse<>();
    }
}
