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
import net.cpollet.seles.impl.Guarded;
import net.cpollet.seles.impl.execution.Context;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Validates that the {@link AttributeDef} are valid for the current {@link InternalRequest}, i.e. for instance that a
 * read {@link InternalRequest} only contains {@link AttributeDef} supporting the read {@link AttributeDef.Mode}. It
 * removes the attributes that are not valid and add an errors for each of them in the {@link InternalResponse}.
 */
public final class ModeValidationStage implements Stage<AttributeDef> {
    private final Stage<AttributeDef> next;
    private final AttributeDef.Mode mode;

    public ModeValidationStage(Stage<AttributeDef> next, Context context) {
        this.next = new RequestHaltStage<>(
                next,
                req -> context.guard.haltOnModeError && req.hasGuardFlag(Guarded.Flag.INVALID_MODE)
        );
        this.mode = context.mode;
    }

    @Override
    public InternalResponse<AttributeDef> execute(InternalRequest<AttributeDef> request) {
        Collection<AttributeDef> invalidModes = request.attributes().stream()
                .filter(a -> !a.supports(mode))
                .collect(Collectors.toList());

        return next.execute(
                request
                        .withoutAttributes(invalidModes)
                        .addGuardedFlagIf(!invalidModes.isEmpty(), Guarded.Flag.INVALID_MODE)
        ).withErrors(
                invalidModes.stream()
                        .map(a -> String.format("[%s] does not support [%s]", a, mode))
                        .collect(Collectors.toSet())
        );
    }
}
