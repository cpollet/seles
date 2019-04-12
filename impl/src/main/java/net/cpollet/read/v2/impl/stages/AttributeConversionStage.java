package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.Guarded;
import net.cpollet.read.v2.impl.data.BiMap;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Transforms an {@link InternalRequest}&lt;T, String&gt; to an
 * {@link InternalRequest}&lt;T, {@link AttributeDef}&lt;T&gt;&gt; and does the reverse
 * transformation from a {@link InternalResponse}.
 */
public final class AttributeConversionStage<T extends Id> implements Stage<T, String> {
    private final Stage<T, AttributeDef<T>> next;
    private final AttributeStore<T> attributesStore;

    public AttributeConversionStage(AttributeStore<T> attributesStore, Stage<T, AttributeDef<T>> next) {
        this.next = next;
        this.attributesStore = attributesStore;
    }

    @Override
    public InternalResponse<T, String> execute(final InternalRequest<T, String> request) {
        BiMap<AttributeDef<T>, String> validAttributesMap = new BiMap<>(
                request.attributes().stream()
                        .map(attributesStore::fetch)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toMap(
                                a -> a,
                                AttributeDef::name
                        ))
        );

        Set<String> invalidAttributes = request.attributes().stream()
                .filter(attributeName -> !validAttributesMap.rightContains(attributeName))
                .collect(Collectors.toSet());

        return next
                .execute(
                        request
                                .withoutAttributes(invalidAttributes)
                                .mapAttributes(validAttributesMap)
                                .addGuardedFlagIf(!invalidAttributes.isEmpty(), Guarded.Flag.ATTRIBUTE_CONVERSION_ERROR)
                )
                .mapAttributes(validAttributesMap)
                .withErrors(
                        invalidAttributes.stream()
                                .map(a -> String.format("[%s] is not a valid attribute", a))
                                .collect(Collectors.toSet())
                );
    }
}
