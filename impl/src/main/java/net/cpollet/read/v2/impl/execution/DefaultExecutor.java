package net.cpollet.read.v2.impl.execution;

import net.cpollet.read.v2.api.attribute.AccessLevelPredicate;
import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.domain.IdValidator;
import net.cpollet.read.v2.api.execution.Executor;
import net.cpollet.read.v2.api.execution.Request;
import net.cpollet.read.v2.api.execution.Response;
import net.cpollet.read.v2.impl.Guarded;
import net.cpollet.read.v2.impl.stages.AttributeConversionStage;
import net.cpollet.read.v2.impl.stages.CreateRequestExecutionStage;
import net.cpollet.read.v2.impl.stages.DeleteRequestExecutionStage;
import net.cpollet.read.v2.impl.stages.EmptyResponseStage;
import net.cpollet.read.v2.impl.stages.ExpandStarStage;
import net.cpollet.read.v2.impl.stages.FilteringStage;
import net.cpollet.read.v2.impl.stages.IdsValidationStage;
import net.cpollet.read.v2.impl.stages.LogDeprecatedStage;
import net.cpollet.read.v2.impl.stages.ModeValidationStage;
import net.cpollet.read.v2.impl.stages.ReadRequestExecutionStage;
import net.cpollet.read.v2.impl.stages.RequestHaltStage;
import net.cpollet.read.v2.impl.stages.SearchRequestExecutionStage;
import net.cpollet.read.v2.impl.stages.Stage;
import net.cpollet.read.v2.impl.stages.TimerStage;
import net.cpollet.read.v2.impl.stages.UpdateRequestExecutionStage;
import net.cpollet.read.v2.impl.stages.ValueConversionStage;

// FIXME deserves a refactoring
public final class DefaultExecutor<T extends Id> implements Executor<T> {
    private final AttributeStore<T> attributeStore;
    private final IdValidator<T> idValidator;
    private final AccessLevelPredicate<T> filteringPredicate;
    private final DefaultExecutorGuard guard;

    private final Stage<T, String> readStack;
    private final Stage<T, String> updateStack;
    private final Stage<T, String> deleteStack;
    private final Stage<T, String> createStack;
    private final Stage<T, String> searchStack;

    public DefaultExecutor(AttributeStore<T> attributeStore, IdValidator<T> idValidator, AccessLevelPredicate<T> filteringPredicate, DefaultExecutorGuard guard) {
        this.attributeStore = attributeStore;
        this.idValidator = idValidator;
        this.filteringPredicate = filteringPredicate;
        this.guard = guard;
        this.readStack =
                new TimerStage<>(
                        new ExpandStarStage<>(attributeStore,
                                validateIdAndConvertValues(AttributeDef.Mode.READ,
                                        new ReadRequestExecutionStage<>()
                                )
                        )
                );
        this.updateStack =
                new TimerStage<>(
                        validateIdAndConvertValues(AttributeDef.Mode.WRITE,
                                new UpdateRequestExecutionStage<>(
                                        new RequestHaltStage<>(
                                                req -> guard.haltDueToUpdateError(req.hasGuardFlag(Guarded.Flag.UPDATE_ERROR)),
                                                new ReadRequestExecutionStage<>()
                                        )
                                )
                        )
                );
        this.deleteStack =
                new TimerStage<>(
                        validateId(AttributeDef.Mode.DELETE,
                                new DeleteRequestExecutionStage<>()
                        )
                );
        this.createStack =
                new TimerStage<>(
                        prepareRequest(AttributeDef.Mode.WRITE,
                                new CreateRequestExecutionStage<>(attributeStore,
                                        new UpdateRequestExecutionStage<>(
                                                new EmptyResponseStage<>()
                                        ),
                                        new RequestHaltStage<>(
                                                req -> guard.haltDueToUpdateError(req.hasGuardFlag(Guarded.Flag.UPDATE_ERROR)),
                                                new ReadRequestExecutionStage<>()
                                        )
                                )
                        )
                );
        this.searchStack =
                new TimerStage<>(
                        validateIdAndConvertValues(AttributeDef.Mode.SEARCH,
                                new SearchRequestExecutionStage<>()
                        )
                );
    }

    /**
     * Stages used for READ, WRITE and SEARCH requests.
     */
    private Stage<T, String> validateIdAndConvertValues(AttributeDef.Mode mode, Stage<T, AttributeDef<T>> inner) {
        return validateId(mode,
                new ValueConversionStage<>(AttributeDef::caster,
                        new ValueConversionStage<>(AttributeDef::converter,
                                new RequestHaltStage<>(
                                        req -> guard.haltDueToInputValueConversionError(req.hasGuardFlag(Guarded.Flag.INPUT_VALUE_CONVERSION_ERROR)),
                                        inner
                                )
                        )
                )
        );
    }

    /**
     * Stages used for READ, WRITE, DELETE and SEARCH requests.
     */
    private Stage<T, String> validateId(AttributeDef.Mode mode, Stage<T, AttributeDef<T>> inner) {
        return prepareRequest(mode,
                new IdsValidationStage<>(idValidator,
                        new RequestHaltStage<>(
                                req -> guard.haltDueToIdValidationError(req.hasGuardFlag(Guarded.Flag.INVALID_IDS)),
                                inner
                        )
                )
        );
    }

    /**
     * Stages used for READ, WRITE, DELETE, CREATE and SEARCH requests.
     */
    private Stage<T, String> prepareRequest(AttributeDef.Mode mode, Stage<T, AttributeDef<T>> inner) {
        return new AttributeConversionStage<>(attributeStore,
                new RequestHaltStage<>(
                        req -> guard.haltDueToAttributeConversionError(req.hasGuardFlag(Guarded.Flag.ATTRIBUTE_CONVERSION_ERROR)),
                        new ModeValidationStage<>(mode,
                                new RequestHaltStage<>(
                                        req -> guard.haltDueToModeError(req.hasGuardFlag(Guarded.Flag.INVALID_MODE)),
                                        new LogDeprecatedStage<>(
                                                new FilteringStage<>(
                                                        filteringPredicate,
                                                        inner
                                                )
                                        )
                                )
                        )
                )
        );
    }

    @Override
    public AttributeStore<T> attributeStore() {
        return attributeStore;
    }

    @Override
    public Response<T> read(Request<T> request) {
        return InternalResponse.unwrap(
                readStack.execute(
                        InternalRequest.wrap(InternalRequest.RequestType.READ, request)
                )
        );
    }

    @Override
    public Response<T> update(Request<T> request) {
        return InternalResponse.unwrap(
                updateStack.execute(
                        InternalRequest.wrap(InternalRequest.RequestType.UPDATE, request)
                )
        );
    }

    @Override
    public Response<T> create(Request<T> request) {
        return InternalResponse.unwrap(
                createStack.execute(
                        InternalRequest.wrap(InternalRequest.RequestType.CREATE, request)
                )
        );
    }

    @Override
    public Response<T> delete(Request<T> request) {
        return InternalResponse.unwrap(
                deleteStack.execute(
                        InternalRequest.wrap(InternalRequest.RequestType.DELETE, request)
                )
        );
    }

    @Override
    public Response<T> search(Request<T> request) {
        return InternalResponse.unwrap(
                searchStack.execute(
                        InternalRequest.wrap(InternalRequest.RequestType.SEARCH, request)
                )
        );
    }
}
