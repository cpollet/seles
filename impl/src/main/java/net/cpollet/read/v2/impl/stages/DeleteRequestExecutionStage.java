package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.Guarded;
import net.cpollet.read.v2.impl.attribute.AttributesGrouper;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeleteRequestExecutionStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    @Override
    public InternalResponse<IdType, AttributeDef<IdType>> execute(InternalRequest<IdType, AttributeDef<IdType>> request) {
        List<String> errors = new ArrayList<>();

        request.attributes(new AttributesGrouper<>()).forEach(
                (method, attributes) -> errors.addAll(
                        method.delete(
                                attributes,
                                request.ids()
                        )
                )
        );

        return new InternalResponse<IdType, AttributeDef<IdType>>(Collections.emptyMap())
                .withErrors(errors);
    }
}
