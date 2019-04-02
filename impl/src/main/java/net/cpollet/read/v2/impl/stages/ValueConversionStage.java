package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.conversion.ValueConverter;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.impl.conversion.ConversionResult;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

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

        ConversionResult<InternalRequest<IdType, AttributeDef<IdType>>> conversionResult = request.convertValues(converters);

        return next
                .execute(conversionResult.result())
                .convertValues(converters)
                .withErrors(conversionResult.errors());
    }
}
