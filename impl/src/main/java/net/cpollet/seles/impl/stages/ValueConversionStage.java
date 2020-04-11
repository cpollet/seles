/*
 * Copyright 2019 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cpollet.seles.impl.stages;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.conversion.ValueConverter;
import net.cpollet.seles.impl.Guarded;
import net.cpollet.seles.impl.conversion.ConversionResult;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Converts {@link InternalRequest} attributes values from external representation to internal representation and
 * converts {@link InternalResponse} attribute values from internal representation to external representation.
 * <p>
 * Multiple {@link ValueConversionStage} may be used, to decouple type casting from value mapping for instance
 */
public final class ValueConversionStage implements Stage<AttributeDef> {
    private final Stage<AttributeDef> next;
    private final Function<AttributeDef, ValueConverter<AttributeDef>> converterSupplier;

    public ValueConversionStage(Function<AttributeDef, ValueConverter<AttributeDef>> converterSupplier, Stage<AttributeDef> next) {
        this.next = next;
        this.converterSupplier = converterSupplier;
    }

    @Override
    public InternalResponse<AttributeDef> execute(InternalRequest<AttributeDef> request) {
        Map<AttributeDef, ValueConverter<AttributeDef>> converters = request.attributes().stream()
                .collect(Collectors.toMap(
                        a -> a,
                        converterSupplier
                ));

        ConversionResult<InternalRequest<AttributeDef>> conversionResult = request.convertValues(converters);

        return next
                .execute(conversionResult
                        .result()
                        .addGuardedFlagIf(!conversionResult.errors().isEmpty(), Guarded.Flag.INPUT_VALUE_CONVERSION_ERROR)
                )
                .convertValues(converters)
                .withErrors(conversionResult.errors());
    }
}
