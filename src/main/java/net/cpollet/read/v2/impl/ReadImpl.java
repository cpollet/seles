package net.cpollet.read.v2.impl;

import net.cpollet.read.v2.api.Read;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.domain.Request;
import net.cpollet.read.v2.api.domain.Response;
import net.cpollet.read.v2.impl.stages.AttributeConversionStage;
import net.cpollet.read.v2.impl.stages.ConversionException;
import net.cpollet.read.v2.impl.stages.IdsValidationStage;
import net.cpollet.read.v2.impl.stages.LogDeprecatedStage;
import net.cpollet.read.v2.impl.stages.RequestExecutionStage;
import net.cpollet.read.v2.impl.stages.TimerStage;
import net.cpollet.read.v2.impl.stages.ValueConversionStage;
import net.cpollet.read.v2.impl.stages.ValueConverter;

public class ReadImpl<IdType extends Id> implements Read<IdType> {
    private final AttributeStore<IdType> attributeStore;

    private final ValueConverter<IdType, AttributeDef<IdType>> converter = (attribute, value) -> {
        if (attribute.name().equals("currency") && value.equals("currency:100000")) {
            throw new ConversionException("why not");
        }
        return String.format("convert(%s)", value);
    };

    private final ValueConverter<IdType, AttributeDef<IdType>> caster = (attribute, value) -> String.format("cast(%s)", value);

    public ReadImpl(AttributeStore<IdType> attributeStore) {
        this.attributeStore = attributeStore;
    }

    @Override
    public Response<IdType> execute(Request<IdType> request) {
        return InternalResponse.unwrap(
                new TimerStage<>(
                        new AttributeConversionStage<>(
                                new LogDeprecatedStage<>(
                                        new IdsValidationStage<>(
                                                new ValueConversionStage<>(
                                                        new ValueConversionStage<>(
                                                                new RequestExecutionStage<>(),
                                                                converter
                                                        ),
                                                        caster
                                                )
                                        )
                                ),
                                attributeStore
                        )
                ).execute(
                        InternalRequest.wrap(request)
                )
        );
    }
}
