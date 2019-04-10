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
public class CreateRequestExecutionStage<IdType extends Id> implements Stage<IdType, AttributeDef<IdType>> {
    private final Stage<IdType, AttributeDef<IdType>> next;
    private final Stage<IdType, AttributeDef<IdType>> update;
    private final AttributeDef<IdType> idAttribute;
    private final Set<AttributeDef<IdType>> requiredAttributes;

    public CreateRequestExecutionStage(AttributeStore<IdType> store, Stage<IdType, AttributeDef<IdType>> update, Stage<IdType, AttributeDef<IdType>> next) {
        this.next = next;
        this.update = update;
        this.idAttribute = store.idAttribute().orElse(null);
        this.requiredAttributes = Collections.unmodifiableSet(
                store.attributes().stream()
                        .filter(a -> a.modes().contains(AttributeDef.Mode.CREATE))
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public InternalResponse<IdType, AttributeDef<IdType>> execute(InternalRequest<IdType, AttributeDef<IdType>> request) {
        if (idAttribute == null) {
            return new InternalResponse<IdType, AttributeDef<IdType>>()
                    .withErrors(Collections.singleton("creation not supported, no id-attribute provided"));
        }

        Set<AttributeDef<IdType>> missingAttributes = requiredAttributes.stream()
                .filter(a -> !request.attributes().contains(a))
                .collect(Collectors.toSet());

        if (!missingAttributes.isEmpty()) {
            return new InternalResponse<IdType, AttributeDef<IdType>>()
                    .withErrors(
                            missingAttributes.stream()
                                    .map(a -> String.format("[%s] is required for creation", a))
                                    .collect(Collectors.toList())
                    );
        }

        Map<Method<IdType>, List<AttributeDef<IdType>>> attributesGroupedByMethod = request.attributes(new AttributesGrouper<>());

        // we create the "main" entity instance with its attributes (i.e. the ones that are using the same method as the id attribute)
        CreateResult<IdType> createResult = idAttribute.method().create(
                request.values(
                        attributesGroupedByMethod.get(idAttribute.method())
                )
        );

        if (!createResult.id().isPresent()) {
            return new InternalResponse<IdType, AttributeDef<IdType>>()
                    .withErrors(createResult.errors());
        }
        IdType id = createResult.id().get();

        // we continue with the remaining attributes, which actually becomes an update...
        InternalResponse<IdType, AttributeDef<IdType>> updateResponse = update.execute(
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
