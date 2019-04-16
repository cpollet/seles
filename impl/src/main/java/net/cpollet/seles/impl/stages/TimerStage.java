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
 * Compute execution time between the beginning of execution, until the lower {@link Stage} returns and puts the value
 * (in milliseconds) in the {@link InternalResponse}.
 */
public final class TimerStage<T extends Id> implements Stage<T, String> {
    private final Stage<T, String> next;

    public TimerStage(Stage<T, String> next) {
        this.next = next;
    }

    @Override
    public InternalResponse<T, String> execute(InternalRequest<T, String> request) {
        long start = System.currentTimeMillis();
        return next.execute(request)
                .withExecutionTime(
                        System.currentTimeMillis() - start
                );
    }
}
