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
import net.cpollet.seles.api.domain.IdValidator;
import net.cpollet.seles.impl.Guarded;
import net.cpollet.seles.impl.execution.Context;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Validates the IDs of the {@link InternalRequest} with the help of a {@link IdValidator} instance, removes the IDs
 * from the {@link InternalRequest} for the next {@link Stage} and puts errors in the {@link InternalResponse} for each
 * invalid id.
 */
public final class IdsValidationStage implements Stage<AttributeDef> {
    private final Stage<AttributeDef> next;
    private final IdValidator idValidator;

    public IdsValidationStage(Stage<AttributeDef> next, Context context) {
        this.next = next;
        this.idValidator =context.idValidator;
    }

    public InternalResponse<AttributeDef> execute(final InternalRequest<AttributeDef> request) {
        Collection<Id> invalidIds = idValidator.invalidIds(request.ids());

        return next
                .execute(
                        request
                                .withoutIds(invalidIds)
                                .addGuardedFlagIf(!invalidIds.isEmpty(), Guarded.Flag.INVALID_IDS)
                )
                .withErrors(
                        invalidIds.stream()
                                .map(e -> String.format("[%s] is not a valid id", e))
                                .collect(Collectors.toList())
                );
    }
}
