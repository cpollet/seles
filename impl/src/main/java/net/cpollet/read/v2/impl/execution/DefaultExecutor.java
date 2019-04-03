package net.cpollet.read.v2.impl.execution;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.execution.Executor;
import net.cpollet.read.v2.api.domain.IdValidator;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.execution.Request;
import net.cpollet.read.v2.api.execution.Response;
import net.cpollet.read.v2.impl.stages.AttributeConversionStage;
import net.cpollet.read.v2.impl.stages.ExpandStarStage;
import net.cpollet.read.v2.impl.stages.FilteringStage;
import net.cpollet.read.v2.impl.stages.IdsValidationStage;
import net.cpollet.read.v2.impl.stages.LogDeprecatedStage;
import net.cpollet.read.v2.impl.stages.ReadRequestExecutionStage;
import net.cpollet.read.v2.impl.stages.TimerStage;
import net.cpollet.read.v2.impl.stages.UpdateRequestExecutionStage;
import net.cpollet.read.v2.impl.stages.ValueConversionStage;

public class DefaultExecutor<IdType extends Id> implements Executor<IdType> {
    private final AttributeStore<IdType> attributeStore;
    private final IdValidator<IdType> idValidator;

    public DefaultExecutor(AttributeStore<IdType> attributeStore, IdValidator<IdType> idValidator) {
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
