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

import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

/**
 * Implements a {@link InternalRequest} processing stage.
 * <p>
 * Classes implementing this interface must be stateless.
 */
public interface Stage<A> {
    /**
     * Ultimately transforms the {@link InternalRequest} to an {@link InternalResponse}. When delegating the request
     * execution to a lower stage, it is expected to to create a new modified instance of the request.
     */
    InternalResponse<A> execute(InternalRequest<A> request);
}
