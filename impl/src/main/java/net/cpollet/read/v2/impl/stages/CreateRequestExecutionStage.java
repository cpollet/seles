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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Executes a CREATE {@link InternalRequest} and returns the created entity's ID.
 */
public final class CreateRequestExecutionStage<T extends Id> implements Stage<T, AttributeDef<T>> {
    private final Stage<T, AttributeDef<T>> next;
    private final Stage<T, AttributeDef<T>> update;
    private final AttributeDef<T> idAttribute;
    private final Set<AttributeDef<T>> requiredAttributes;

    public CreateRequestExecutionStage(AttributeStore<T> store, Stage<T, AttributeDef<T>> update, Stage<T, AttributeDef<T>> next) {
        this.next = next;
        this.update = update;
        this.idAttribute = store.idAttribute().orElse(null);
        this.requiredAttributes = Collections.unmodifiableSet(
                store.attributes().stream()
                        .filter(a -> a.modes().contains(AttributeDef.Mode.CREATE))
                        .collect(Collectors.toSet())
        );
    }

    // TODO deserves a refactoring...
    @Override
    public InternalResponse<T, AttributeDef<T>> execute(InternalRequest<T, AttributeDef<T>> request) {
        if (idAttribute == null) {
            return new InternalResponse<T, AttributeDef<T>>()
                    .withErrors(Collections.singleton("creation not supported, no id-attribute provided"));
        }

        Set<AttributeDef<T>> missingAttributes = requiredAttributes.stream()
                .filter(a -> !request.attributes().contains(a))
                .collect(Collectors.toSet());

        if (!missingAttributes.isEmpty()) {
            return new InternalResponse<T, AttributeDef<T>>()
                    .withErrors(
                            missingAttributes.stream()
                                    .map(a -> String.format("[%s] is required for creation", a))
                                    .collect(Collectors.toList())
                    );
        }

        Map<Method<T>, List<AttributeDef<T>>> attributesGroupedByMethod = request.attributes(new AttributesGrouper<>());

        // we create the "main" entity instance with its attributes (i.e. the ones that are using the same method as the id attribute)
        CreateResult<T> createResult = idAttribute.method().create(
                request.principal(),
                request.values(
                        attributesGroupedByMethod.get(idAttribute.method())
                )
        );

        if (!createResult.id().isPresent()) {
            return new InternalResponse<T, AttributeDef<T>>()
                    .withErrors(createResult.errors());
        }
        T id = createResult.id().get();

        // we continue with the remaining attributes, which actually becomes an update...
        InternalResponse<T, AttributeDef<T>> updateResponse = update.execute(
                request.withIds(Collections.singletonList(id))
                        .withoutAttributes(attributesGroupedByMethod.get(idAttribute.method()))
        );

        return next
                .execute(
                        request
                                .withIds(Collections.singletonList(id))
                                .addGuardedFlagIf(updateResponse.hasErrors(), Guarded.Flag.UPDATE_ERROR)
                )
                .mergeErrors(updateResponse)
                .mergeMessages(updateResponse);
    }
}
