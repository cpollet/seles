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
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.api.execution.Executor;
import net.cpollet.seles.api.execution.Request;
import net.cpollet.seles.api.execution.Response;

public class NoopExecutor<T extends Id> implements Executor<T> {
    private final AttributeStore<T> store;

    public NoopExecutor(AttributeStore<T> store) {
        this.store = store;
    }

    @Override
    public AttributeStore<T> attributeStore() {
        return store;
    }

    @Override
    public Response<T> read(Request<T> request) {
        return null;
    }

    @Override
    public Response<T> update(Request<T> request) {
        return null;
    }

    @Override
    public Response<T> create(Request<T> request) {
        return null;
    }

    @Override
    public Response<T> delete(Request<T> request) {
        return null;
    }

    @Override
    public Response<T> search(Request<T> request) {
        return null;
    }
}
