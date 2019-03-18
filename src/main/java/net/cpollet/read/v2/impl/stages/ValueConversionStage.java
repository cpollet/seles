package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.AttributeDef;
import net.cpollet.read.v2.impl.InternalRequest;
import net.cpollet.read.v2.impl.InternalResponse;

public class ValueConversionStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    private final Stage<IdType, AttributeDef<IdType>> next;
    private final ValueConverter<IdType, AttributeDef<IdType>> converter;

    public ValueConversionStage(Stage<IdType, AttributeDef<IdType>> next, ValueConverter<IdType, AttributeDef<IdType>> converter) {
        this.next = next;
        this.converter = converter;
    }

    @Override
    public InternalResponse<IdType, AttributeDef<IdType>> execute(InternalRequest<IdType, AttributeDef<IdType>> request) {
        return next
                .execute(request)
                .convertValues(converter);
    }
}
