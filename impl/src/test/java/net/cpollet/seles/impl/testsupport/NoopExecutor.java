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
package net.cpollet.seles.impl.testsupport;

import net.cpollet.seles.api.attribute.AttributeStore;
import net.cpollet.seles.api.execution.Executor;
import net.cpollet.seles.api.execution.Request;
import net.cpollet.seles.api.execution.Response;

public class NoopExecutor implements Executor {
    private final AttributeStore store;

    public NoopExecutor(AttributeStore store) {
        this.store = store;
    }

    @Override
    public AttributeStore attributeStore() {
        return store;
    }

    @Override
    public Response read(Request request) {
        return null;
    }

    @Override
    public Response update(Request request) {
        return null;
    }

    @Override
    public Response create(Request request) {
        return null;
    }

    @Override
    public Response delete(Request request) {
        return null;
    }

    @Override
    public Response search(Request request) {
        return null;
    }
}
