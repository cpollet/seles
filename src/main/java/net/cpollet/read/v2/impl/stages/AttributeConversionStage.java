package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.AttributeDef;
import net.cpollet.read.v2.api.AttributeStore;
import net.cpollet.read.v2.impl.InternalRequest;
import net.cpollet.read.v2.impl.InternalResponse;
import net.cpollet.read.v2.impl.data.BiMap;

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
        Set<AttributeDef<IdType>> attributes = request.attributes().stream()
                .map(attributesStore::fetch)
                .collect(Collectors.toSet());

        Set<String> invalidAttributes = attributes.stream()
                .filter(AttributeDef::invalid)
                .map(AttributeDef::name)
                .collect(Collectors.toSet());

        BiMap<AttributeDef<IdType>, String> validAttributesMap = new BiMap<>(
                attributes.stream()
                        .filter(a -> !a.invalid())
                        .collect(Collectors.toMap(a -> a, AttributeDef::name))
        );

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
