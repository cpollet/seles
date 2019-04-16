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
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

import java.util.stream.Collectors;

/**
 * Puts warning in the {@link InternalResponse} for each deprecated {@link AttributeDef} used in the
 * {@link InternalRequest}.
 */
public final class LogDeprecatedStage<T extends Id> implements Stage<T, AttributeDef<T>> {
    private final Stage<T, AttributeDef<T>> next;

    public LogDeprecatedStage(Stage<T, AttributeDef<T>> next) {
        this.next = next;
    }

    @Override
    public InternalResponse<T, AttributeDef<T>> execute(InternalRequest<T, AttributeDef<T>> request) {
        return next.execute(request)
                .withMessages(
                        request.attributes().stream()
                                .filter(AttributeDef::deprecated)
                                .map(a -> String.format("[%s] is deprecated", a))
                                .collect(Collectors.toSet())
                );
    }
}
