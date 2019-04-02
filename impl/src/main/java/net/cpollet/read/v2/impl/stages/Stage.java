package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

public interface Stage<IdType extends Id, AttributeType> {
    InternalResponse<IdType, AttributeType> execute(InternalRequest<IdType, AttributeType> request);
}
