package net.cpollet.read.v2.impl.execution;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.stages.Stage;

import java.util.Collections;

public class CreateRequestExecutionStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    @Override
    public InternalResponse<IdType, AttributeDef<IdType>> execute(InternalRequest<IdType, AttributeDef<IdType>> request) {
        return new InternalResponse<>(Collections.emptyMap());
    }
}
