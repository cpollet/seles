package net.cpollet.read.v2.impl;

import net.cpollet.read.v2.api.Executor;
import net.cpollet.read.v2.api.IdValidator;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.domain.Request;
import net.cpollet.read.v2.api.domain.Response;
import net.cpollet.read.v2.impl.stages.AttributeConversionStage;
import net.cpollet.read.v2.impl.stages.ExpandStarStage;
import net.cpollet.read.v2.impl.stages.FilteringStage;
import net.cpollet.read.v2.impl.stages.IdsValidationStage;
import net.cpollet.read.v2.impl.stages.LogDeprecatedStage;
import net.cpollet.read.v2.impl.stages.ReadRequestExecutionStage;
import net.cpollet.read.v2.impl.stages.TimerStage;
import net.cpollet.read.v2.impl.stages.UpdateRequestExecutionStage;
import net.cpollet.read.v2.impl.stages.ValueConversionStage;

public class ExecutorImpl<IdType extends Id> implements Executor<IdType> {
    private final AttributeStore<IdType> attributeStore;
    private final IdValidator<IdType> idValidator;

    public ExecutorImpl(AttributeStore<IdType> attributeStore, IdValidator<IdType> idValidator) {
        this.attributeStore = attributeStore;
        this.idValidator = idValidator;
    }

    @Override
    public Response<IdType> read(Request<IdType> request) {
        return InternalResponse.unwrap(
                new TimerStage<>(
                        new ExpandStarStage<>(
                                new AttributeConversionStage<>(
                                        new LogDeprecatedStage<>(
                                                new IdsValidationStage<>(
                                                        new FilteringStage<>(
                                                                new ValueConversionStage<>(
                                                                        new ValueConversionStage<>(
                                                                                new ReadRequestExecutionStage<>(),
                                                                                AttributeDef::converter
                                                                        ),
                                                                        AttributeDef::caster
                                                                )
                                                        ),
                                                        idValidator
                                                )
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

    @Override
    public Response<IdType> update(Request<IdType> request) {
        return InternalResponse.unwrap(
                new TimerStage<>(
                        new AttributeConversionStage<>(
                                new LogDeprecatedStage<>(
                                        new IdsValidationStage<>(
                                                new FilteringStage<>(
                                                        new ValueConversionStage<>(
                                                                new ValueConversionStage<>(
                                                                        new UpdateRequestExecutionStage<>(
                                                                                new ReadRequestExecutionStage<>()
                                                                        ),
                                                                        AttributeDef::converter
                                                                ),
                                                                AttributeDef::caster
                                                        )
                                                ),
                                                idValidator
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
