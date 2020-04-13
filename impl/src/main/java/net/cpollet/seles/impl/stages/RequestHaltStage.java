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

import net.cpollet.seles.impl.Guarded;
import net.cpollet.seles.impl.execution.InternalRequest;
import net.cpollet.seles.impl.execution.InternalResponse;

import java.util.function.Function;

/**
 * Halts the processing of the {@link InternalRequest} and returns an empty {@link InternalResponse} when the guard
 * function returns true.
 *
 * @see Guarded
 */
public final class RequestHaltStage<A> implements Stage<A> {
    private final Stage<A> next;
    private final Function<Guarded, Boolean> guard;

    public RequestHaltStage(Stage<A> next, Function<Guarded, Boolean> guard) {
        this.next = next;
        this.guard = guard;
    }

    @Override
    public InternalResponse<A> execute(InternalRequest<A> request) {
        if (guard.apply(request)) {
            return new InternalResponse<>();
        }

        return next.execute(request);
    }
}
