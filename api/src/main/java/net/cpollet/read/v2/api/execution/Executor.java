package net.cpollet.read.v2.api.execution;

import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.domain.Id;

public interface Executor<IdType extends Id> {
    AttributeStore<IdType> attributeStore();

    Response<IdType> read(Request<IdType> request);

    Response<IdType> update(Request<IdType> request);

    Response<IdType> create(Request<IdType> request);

    Response<IdType> delete(Request<IdType> request);

    Response<IdType> search(Request<IdType> request);
}
