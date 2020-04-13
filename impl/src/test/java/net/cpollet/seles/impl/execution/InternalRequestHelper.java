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
package net.cpollet.seles.impl.execution;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.attribute.AttributeStore;
import net.cpollet.seles.api.execution.Request;
import net.cpollet.seles.impl.stages.AttributeConversionStage;
import net.cpollet.seles.impl.testsupport.VoidPrincipal;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InternalRequestHelper {
    private InternalRequestHelper() {
        // nothing
    }

    /**
     * Builds a request that requests all attributes from the store on an empty set of IDs
     *
     * @param store the store containing the attributes to include in the request
     * @return the generated request
     */
    public static InternalRequest<AttributeDef> toAttributeDefInternalRequest(AttributeStore store) {
        final Holder<InternalRequest<AttributeDef>> holder = new Holder<>();

        new AttributeConversionStage(
                request -> {
                    holder.object = request;
                    return new InternalResponse<>();
                },
                new Context(null, store, null, null, new DefaultExecutorGuard(), null)
        ).execute(
                toStringInternalRequest(
                        store.attributes().stream()
                                .map(AttributeDef::name)
                                .collect(Collectors.toList())
                )
        );

        return holder.object;
    }

    public static InternalRequest<String> toStringInternalRequest(List<String> attributes) {
        return InternalRequest.wrap(
                InternalRequest.RequestType.READ,
                Request.read(
                        new VoidPrincipal(),
                        Collections.emptyList(),
                        attributes
                )
        );
    }

    static class Holder<T> {
        T object;
    }
}
