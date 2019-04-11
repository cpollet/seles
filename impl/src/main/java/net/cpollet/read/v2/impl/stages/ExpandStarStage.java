package net.cpollet.read.v2.impl.stages;

import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.impl.execution.InternalRequest;
import net.cpollet.read.v2.impl.execution.InternalResponse;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Replaces the wildcard attribute '*' with all the attributes of the context in which it's used.
 */
public class ExpandStarStage<T extends Id> implements Stage<T, String> {
    private final Stage<T, String> next;
    private final AttributeStore<T> attributeStore;

    public ExpandStarStage(AttributeStore<T> attributeStore, Stage<T, String> next) {
        this.next = next;
        this.attributeStore = attributeStore;
    }

    @Override
    public InternalResponse<T, String> execute(InternalRequest<T, String> request) {
        if (!request.is(InternalRequest.RequestType.READ)) {
            throw new IllegalStateException("Can only apply ExpandStarStage to a request of type READ");
        }

        if (!request.attributes().contains("*")) {
            return next.execute(request);
        }

        return next.execute(
                request
                        .withoutAttributes(Collections.singleton("*"))
                        .withAttributes(
                                attributeStore.attributes().stream()
                                        .map(AttributeDef::name)
                                        .collect(Collectors.toSet())
                        )
        );
    }
}
