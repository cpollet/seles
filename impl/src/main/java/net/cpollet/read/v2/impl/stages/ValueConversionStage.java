package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.conversion.ValueConverter;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.Guarded;
import net.cpollet.read.v2.impl.conversion.ConversionResult;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Converts {@link InternalRequest} attributes values from external representation to internal representation and
 * converts {@link InternalResponse} attribute values from internal representation to external representation.
 *
 * Multiple {@link ValueConversionStage} may be used, to decouple type casting from value mapping for instance
 */
public class ValueConversionStage<T extends Id> implements Stage<T, AttributeDef<T>> {
    private final Stage<T, AttributeDef<T>> next;
    private final Function<AttributeDef<T>, ValueConverter<AttributeDef<T>>> converterSupplier;

    public ValueConversionStage(Function<AttributeDef<T>, ValueConverter<AttributeDef<T>>> converterSupplier, Stage<T, AttributeDef<T>> next) {
        this.next = next;
        this.converterSupplier = converterSupplier;
    }

    @Override
    public InternalResponse<T, AttributeDef<T>> execute(InternalRequest<T, AttributeDef<T>> request) {
        Map<AttributeDef<T>, ValueConverter<AttributeDef<T>>> converters = request.attributes().stream()
                .collect(Collectors.toMap(
                        a -> a,
                        converterSupplier
                ));

        ConversionResult<InternalRequest<T, AttributeDef<T>>> conversionResult = request.convertValues(converters);

        return next
                .execute(conversionResult
                        .result()
                        .addGuardedFlagIf(!conversionResult.errors().isEmpty(), Guarded.Flag.INPUT_VALUE_CONVERSION_ERROR)
                )
                .convertValues(converters)
                .withErrors(conversionResult.errors());
    }
}
