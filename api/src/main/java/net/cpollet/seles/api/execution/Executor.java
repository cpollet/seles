package net.cpollet.seles.api.execution;

import net.cpollet.seles.api.attribute.AttributeStore;
import net.cpollet.seles.api.domain.Id;

public interface Executor<T extends Id> {
    AttributeStore<T> attributeStore();

    Response<T> read(Request<T> request);

    Response<T> update(Request<T> request);

    Response<T> create(Request<T> request);

    Response<T> delete(Request<T> request);

    Response<T> search(Request<T> request);
}
