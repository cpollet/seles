package net.cpollet.read.v2.impl.execution;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.domain.IdValidator;
import net.cpollet.read.v2.api.execution.Executor;
import net.cpollet.read.v2.api.execution.Request;
import net.cpollet.read.v2.api.execution.Response;
import net.cpollet.read.v2.impl.Guarded;
import net.cpollet.read.v2.impl.stages.AttributeConversionStage;
import net.cpollet.read.v2.impl.stages.ExpandStarStage;
import net.cpollet.read.v2.impl.stages.FilteringStage;
import net.cpollet.read.v2.impl.stages.IdsValidationStage;
import net.cpollet.read.v2.impl.stages.LogDeprecatedStage;
import net.cpollet.read.v2.impl.stages.ReadRequestExecutionStage;
import net.cpollet.read.v2.impl.stages.RequestHaltStage;
import net.cpollet.read.v2.impl.stages.Stage;
import net.cpollet.read.v2.impl.stages.TimerStage;
import net.cpollet.read.v2.impl.stages.UpdateRequestExecutionStage;
import net.cpollet.read.v2.impl.stages.ValueConversionStage;

import java.util.function.Function;

public class DefaultExecutor<IdType extends Id> implements Executor<IdType> {
    private final Stage<IdType, String> readStack;
    private final Stage<IdType, String> updateStack;

    public DefaultExecutor(AttributeStore<IdType> attributeStore, IdValidator<IdType> idValidator, Configuration configuration) {
        this.readStack =
                new TimerStage<>(
                        new ExpandStarStage<>(attributeStore,
                                new AttributeConversionStage<>(attributeStore,
                                        new RequestHaltStage<>(haltOnAttributeConversionError(configuration),
                                                new LogDeprecatedStage<>(
                                                        new IdsValidationStage<>(idValidator,
                                                                new RequestHaltStage<>(haltOnIdValidationError(configuration),
                                                                        new FilteringStage<>(
                                                                                new ValueConversionStage<>(AttributeDef::caster,
                                                                                        new ValueConversionStage<>(AttributeDef::converter,
                                                                                                new ReadRequestExecutionStage<>()
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                );

        this.updateStack =
                new TimerStage<>(
                        new AttributeConversionStage<>(attributeStore,
                                new RequestHaltStage<>(haltOnAttributeConversionError(configuration),
                                        new LogDeprecatedStage<>(
                                                new IdsValidationStage<>(idValidator,
                                                        new RequestHaltStage<>(haltOnIdValidationError(configuration),
                                                                new FilteringStage<>(
                                                                        new ValueConversionStage<>(AttributeDef::caster,
                                                                                new ValueConversionStage<>(AttributeDef::converter,
                                                                                        new RequestHaltStage<>(haltOnInputValueConversionError(configuration),
                                                                                                new UpdateRequestExecutionStage<>(
                                                                                                        new RequestHaltStage<>(haltOnUpdateError(configuration),
                                                                                                                new ReadRequestExecutionStage<>()
                                                                                                        )
                                                                                                )
                                                                                        )
                                                                                )
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                );
    }

    private Function<Guarded, Boolean> haltOnAttributeConversionError(Configuration configuration) {
        return req -> configuration.haltOnAttributeConversionError && req.hasGuardFlag(Guarded.Flag.ATTRIBUTE_CONVERSION_ERROR);
    }

    private Function<Guarded, Boolean> haltOnIdValidationError(Configuration configuration) {
        return req -> configuration.haltOnIdValidationError && req.hasGuardFlag(Guarded.Flag.INVALID_IDS);
    }

    private Function<Guarded, Boolean> haltOnInputValueConversionError(Configuration configuration) {
        return req -> configuration.haltOnInputValueConversionError && req.hasGuardFlag(Guarded.Flag.INPUT_VALUE_CONVERSION_ERROR);
    }

    private Function<Guarded, Boolean> haltOnUpdateError(Configuration configuration) {
        return req -> configuration.haltOnUpdateError && req.hasGuardFlag(Guarded.Flag.UPDATE_ERROR);
    }

    @Override
    public Response<IdType> read(Request<IdType> request) {
        return InternalResponse.unwrap(
                readStack.execute(
                        InternalRequest.wrap(request)
                )
        );
    }

    @Override
    public Response<IdType> update(Request<IdType> request) {
        return InternalResponse.unwrap(
                updateStack.execute(
                        InternalRequest.wrap(request)
                )
        );
    }

    public static class Configuration {
        private final boolean haltOnAttributeConversionError;
        private final boolean haltOnIdValidationError;
        private final boolean haltOnInputValueConversionError;
        private final boolean haltOnUpdateError;

        public Configuration(boolean haltOnAttributeConversionError, boolean haltOnIdValidationError, boolean haltOnInputValueConversionError, boolean haltOnUpdateError) {
            this.haltOnAttributeConversionError = haltOnAttributeConversionError;
            this.haltOnIdValidationError = haltOnIdValidationError;
            this.haltOnInputValueConversionError = haltOnInputValueConversionError;
            this.haltOnUpdateError = haltOnUpdateError;
        }
    }
}
