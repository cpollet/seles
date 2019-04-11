package net.cpollet.read.v2.api.execution;

import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.domain.Id;

public interface Executor<T extends Id> {
    AttributeStore<T> attributeStore();

    Response<T> read(Request<T> request);

    Response<T> update(Request<T> request);

    Response<T> create(Request<T> request);

    Response<T> delete(Request<T> request);

    Response<T> search(Request<T> request);
}
