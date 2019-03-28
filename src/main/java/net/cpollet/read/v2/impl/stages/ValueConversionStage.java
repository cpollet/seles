package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.AttributeDef;
import net.cpollet.read.v2.impl.InternalRequest;
import net.cpollet.read.v2.impl.InternalResponse;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValueConversionStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    private final Function<AttributeDef<IdType>, ValueConverter<AttributeDef<IdType>>> converterSupplier;
    private final Stage<IdType, AttributeDef<IdType>> next;

    public ValueConversionStage(Stage<IdType, AttributeDef<IdType>> next, Function<AttributeDef<IdType>, ValueConverter<AttributeDef<IdType>>> converterSupplier) {
        this.next = next;
        this.converterSupplier = converterSupplier;
    }

    @Override
    public InternalResponse<IdType, AttributeDef<IdType>> execute(InternalRequest<IdType, AttributeDef<IdType>> request) {
        Map<AttributeDef<IdType>, ValueConverter<AttributeDef<IdType>>> converters = request.attributes().stream()
                .collect(Collectors.toMap(
                        a -> a,
                        converterSupplier
                ));

        return next
                .execute(request)
                .convertValues(converters);
    }
}
