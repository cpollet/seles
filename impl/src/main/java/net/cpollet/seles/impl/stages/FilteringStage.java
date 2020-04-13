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

import net.cpollet.seles.api.attribute.AccessLevelPredicate;
import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.impl.execution.Context;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Removes {@link AttributeDef} from the {@link InternalRequest} according to the ({@link AttributeDef#accessLevel()}
 * and passes the newly created request object to the lower {@link Stage}. Puts '*****' as a value in the
 * {@link InternalResponse} for each removed attribute.
 */
public final class FilteringStage implements Stage<AttributeDef> {
    private final Stage<AttributeDef> next;
    private final AccessLevelPredicate predicate;

    public FilteringStage(Stage<AttributeDef> next, Context context) {
        this.predicate = context.filteringPredicate;
        this.next = next;
    }

    @Override
    public InternalResponse<AttributeDef> execute(InternalRequest<AttributeDef> request) {
        Set<AttributeDef> filteredAttributes = request.attributes().stream()
                .filter(attribute -> predicate.test(request.principal(), attribute))
                .collect(Collectors.toSet());

        Map<Id, Map<AttributeDef, String>> filteredValues = request.ids().stream()
                .collect(Collectors.toMap(
                        id -> id,
                        id -> filteredAttributes.stream()
                                .collect(Collectors.toMap(
                                        a -> a,
                                        a -> "*****"
                                )))
                );

        return next.execute(
                request.withoutAttributes(filteredAttributes)
        )
                .append(filteredValues)
                .withMessages(
                        filteredAttributes.stream()
                                .map(a -> String.format("[%s] is filtered", a.name()))
                                .collect(Collectors.toSet())
                );
    }
}
