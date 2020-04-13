/*
 * Copyright 2019 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cpollet.seles.impl.stages;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.attribute.AttributeStore;
import net.cpollet.seles.impl.execution.Context;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Replaces the wildcard attribute '*' with all the attributes of the context in which it's used.
 */
public final class ExpandStarStage implements Stage<String> {
    private final Stage<String> next;
    private final AttributeStore attributeStore;

    public ExpandStarStage(Stage<String> next, Context context) {
        this.next = next;
        this.attributeStore = context.attributeStore;
    }

    @Override
    public InternalResponse<String> execute(InternalRequest<String> request) {
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
