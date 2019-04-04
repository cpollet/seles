package net.cpollet.read.v2.impl.execution;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.execution.Request;
import net.cpollet.read.v2.impl.stages.AttributeConversionStage;
import net.cpollet.read.v2.impl.testsupport.StringId;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InternalRequestHelper {
    /**
     * Builds a request that requests all attributes from the store on an empty set of IDs
     *
     * @param store the store containing the attributes to include in the request
     * @return the generated request
     */
    public static InternalRequest<StringId, AttributeDef<StringId>> toAttributeDefInternalRequest(AttributeStore<StringId> store) {
        final Holder<InternalRequest<StringId, AttributeDef<StringId>>> holder = new Holder<>();

        new AttributeConversionStage<>(store, request -> {
            holder.object = request;
            return new InternalResponse<>(Collections.emptyMap());
        }).execute(
                toStringInternalRequest(
                        store.attributes().stream()
                                .map(AttributeDef::name)
                                .collect(Collectors.toList())
                )
        );

        return holder.object;
    }

    public static InternalRequest<StringId, String> toStringInternalRequest(List<String> attributes) {
        return InternalRequest.wrap(
                new Request<>(
                        Collections.emptyList(),
                        attributes
                )
        );
    }

    static class Holder<T> {
        T object;
    }
}