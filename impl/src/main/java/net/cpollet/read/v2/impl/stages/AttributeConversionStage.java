package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;
import net.cpollet.read.v2.impl.data.BiMap;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AttributeConversionStage<IdType extends Id> implements Stage<IdType, String> {
    private final Stage<IdType, AttributeDef<IdType>> next;
    private final AttributeStore<IdType> attributesStore;

    public AttributeConversionStage(Stage<IdType, AttributeDef<IdType>> next, AttributeStore<IdType> attributesStore) {
        this.next = next;
        this.attributesStore = attributesStore;
    }

    @Override
    public InternalResponse<IdType, String> execute(final InternalRequest<IdType, String> request) {
        BiMap<AttributeDef<IdType>, String> validAttributesMap = new BiMap<>(
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
                )
                .mapAttributes(validAttributesMap)
                .withErrors(
                        invalidAttributes.stream()
                                .map(a -> String.format("[%s] is not a valid attribute", a))
                                .collect(Collectors.toSet())
                );
    }
}
