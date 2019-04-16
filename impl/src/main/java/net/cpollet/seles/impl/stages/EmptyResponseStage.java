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

import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

/**
 * Returns an empty {@link InternalResponse} no matter what the {@link InternalRequest} contains.
 *
 * @param <T> the entity's ID type
 * @param <A> the attributes types (usually either {@link String} or
 *            {@link net.cpollet.seles.api.attribute.AttributeDef}
 */
public final class EmptyResponseStage<T extends Id, A> implements Stage<T, A> {
    @Override
    public InternalResponse<T, A> execute(InternalRequest<T, A> request) {
        return new InternalResponse<>();
    }
}
