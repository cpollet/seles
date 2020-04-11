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
import net.cpollet.seles.impl.attribute.AttributesGrouper;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Executes a UPDATE {@link InternalRequest}.
 */
public final class UpdateRequestExecutionStage implements Stage<AttributeDef> {
    private final Stage<AttributeDef> next;

    public UpdateRequestExecutionStage(Stage<AttributeDef> next) {
        this.next = next;
    }

    @Override
    public InternalResponse<AttributeDef> execute(InternalRequest<AttributeDef> request) {
        List<String> errors = new ArrayList<>();

        request.attributes(new AttributesGrouper()).forEach(
                (method, attributes) -> errors.addAll(
                        method.update(
                                request.principal(),
                                request.values(attributes),
                                request.ids()
                        )
                )
        );

        return next
                .execute(
                        request.addGuardedFlagIf(!errors.isEmpty(), Guarded.Flag.UPDATE_ERROR)
                )
                .withErrors(errors);
    }
}
