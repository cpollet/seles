package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.methods.CreateResult;
import net.cpollet.read.v2.api.methods.Method;
import net.cpollet.read.v2.impl.Guarded;
import net.cpollet.read.v2.impl.attribute.AttributesGrouper;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Executes a CREATE {@link InternalRequest} and returns the created entity's ID.
 */
public final class CreateRequestExecutionStage<T extends Id> implements Stage<T, AttributeDef<T>> {
    private final Stage<T, AttributeDef<T>> next;
    private final Stage<T, AttributeDef<T>> update;
    private final Method<T> idAttributeMethod;
    private final Set<AttributeDef<T>> requiredAttributes;

    public CreateRequestExecutionStage(AttributeStore<T> store, Stage<T, AttributeDef<T>> update, Stage<T, AttributeDef<T>> next) {
        this.next = next;
        this.update = update;
        this.idAttributeMethod = store.idAttribute()
                .map(AttributeDef::method)
                .orElse(null);
        this.requiredAttributes = Collections.unmodifiableSet(
                store.attributes().stream()
                        .filter(a -> a.modes().contains(AttributeDef.Mode.CREATE))
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public InternalResponse<T, AttributeDef<T>> execute(InternalRequest<T, AttributeDef<T>> request) {
        if (idAttributeMethod == null) {
            throw new IllegalStateException("creation not supported, no id-attribute method provided");
        }

        Optional<InternalResponse<T, AttributeDef<T>>> errorResponse = validateRequiredAttributes(request.attributes());
        if (errorResponse.isPresent()) {
            return errorResponse.get();
        }

        List<AttributeDef<T>> mainAttributes = mainAttributes(request);

        Result<T> idOrError = createMainEntity(request, mainAttributes);
        if (!idOrError.isSuccessful()) {
            return idOrError.response();
        }

        InternalResponse<T, AttributeDef<T>> updateResponse = updateSecondaryEntities(
                idOrError.id(),
                request,
                mainAttributes
        );

        return next
                .execute(
                        request
                                .withIds(Collections.singletonList(idOrError.id()))
                                .addGuardedFlagIf(updateResponse.hasErrors(), Guarded.Flag.UPDATE_ERROR)
                )
                .mergeErrors(updateResponse)
                .mergeMessages(updateResponse);
    }

    private Optional<InternalResponse<T, AttributeDef<T>>> validateRequiredAttributes(Collection<AttributeDef<T>> attributes) {
        Set<AttributeDef<T>> missingAttributes = requiredAttributes.stream()
                .filter(a -> !attributes.contains(a))
                .collect(Collectors.toSet());

        if (!missingAttributes.isEmpty()) {
            return Optional.of(
                    new InternalResponse<T, AttributeDef<T>>()
                            .withErrors(
                                    missingAttributes.stream()
                                            .map(a -> String.format("[%s] is required for creation", a))
                                            .collect(Collectors.toList())
                            )
            );
        }

        return Optional.empty();
    }

    /**
     * Returns the list of all attributes that are handled by the same method as the id-attribute, i.e. the ones
     * for which we don't want to execute an update afterwards.
     */
    private List<AttributeDef<T>> mainAttributes(InternalRequest<T, AttributeDef<T>> request) {
        return request
                .attributes(new AttributesGrouper<>())
                .get(idAttributeMethod);
    }

    /**
     * Create the "main" entity instance with its attributes (i.e. the ones that are using the same method as the id
     * attribute)
     */
    private Result<T> createMainEntity(InternalRequest<T, AttributeDef<T>> request, List<AttributeDef<T>> attributes) {
        CreateResult<T> createResult = idAttributeMethod.create(
                request.principal(),
                request.values(attributes)
        );

        return createResult.id()
                .map(Result::success)
                .orElseGet(() -> Result.error(
                        new InternalResponse<T, AttributeDef<T>>()
                                .withErrors(createResult.errors())
                ));

    }

    /**
     * Inserts the remaining attributes (i.e. the ones that are not using the same method as the id-attribute). This
     * action actually becomes an update, as the main entity already exists.
     */
    private InternalResponse<T, AttributeDef<T>> updateSecondaryEntities(T id, InternalRequest<T, AttributeDef<T>> request, List<AttributeDef<T>> alreadyInsertedAttributes) {
        return update.execute(
                request
                        .withIds(Collections.singletonList(id))
                        .withoutAttributes(alreadyInsertedAttributes)
        );
    }

    private static final class Result<T extends Id> {
        private final T id;
        private final InternalResponse<T, AttributeDef<T>> response;

        Result(T id, InternalResponse<T, AttributeDef<T>> response) {
            this.id = id;
            this.response = response;
        }

        static <T extends Id> Result<T> success(T id) {
            if (id == null) {
                throw new NullPointerException("id cannot be null");
            }
            return new Result<>(id, null);
        }

        static <T extends Id> Result<T> error(InternalResponse<T, AttributeDef<T>> response) {
            if (response == null) {
                throw new NullPointerException("error cannot be null");
            }
            return new Result<>(null, response);
        }

        boolean isSuccessful() {
            return id != null;
        }

        InternalResponse<T, AttributeDef<T>> response() {
            return response;
        }

        public T id() {
            return id;
        }
    }
}
