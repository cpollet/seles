package net.cpollet.read.v2.impl;

import net.cpollet.read.v2.api.AttributeStore;
import net.cpollet.read.v2.api.IdValidator;
import net.cpollet.read.v2.api.Read;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.domain.Request;
import net.cpollet.read.v2.api.domain.Response;
import net.cpollet.read.v2.impl.stages.AttributeConversionStage;
import net.cpollet.read.v2.impl.stages.ExpandStarStage;
import net.cpollet.read.v2.impl.stages.FilteringStage;
import net.cpollet.read.v2.impl.stages.IdsValidationStage;
import net.cpollet.read.v2.impl.stages.LogDeprecatedStage;
import net.cpollet.read.v2.impl.stages.RequestExecutionStage;
import net.cpollet.read.v2.impl.stages.TimerStage;
import net.cpollet.read.v2.impl.stages.ValueConversionStage;

public class ReadImpl<IdType extends Id> implements Read<IdType> {
    private final AttributeStore<IdType> attributeStore;
    private final IdValidator<IdType> idValidator;

    public ReadImpl(AttributeStore<IdType> attributeStore, IdValidator<IdType> idValidator) {
        this.attributeStore = attributeStore;
        this.idValidator = idValidator;
    }

    @Override
    public Response<IdType> execute(Request<IdType> request) {
        return InternalResponse.unwrap(
                new TimerStage<>(
                        new ExpandStarStage<>(
                                new AttributeConversionStage<>(
                                        new LogDeprecatedStage<>(
                                                new IdsValidationStage<>(
                                                        new ValueConversionStage<>(
                                                                new ValueConversionStage<>(
                                                                        new FilteringStage<>(
                                                                                new RequestExecutionStage<>()
                                                                        ),
                                                                        AttributeDef::converter
                                                                ),
                                                                AttributeDef::caster
                                                        ),
                                                        new CachedIdValidator<>(idValidator))
                                        ),
                                        attributeStore
                                ),
                                attributeStore
                        )
                ).execute(
                        InternalRequest.wrap(request)
                )
        );
    }
}
