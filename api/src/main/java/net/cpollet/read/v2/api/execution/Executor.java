package net.cpollet.read.v2.api.execution;

import net.cpollet.read.v2.api.domain.Id;

public interface Executor<IdType extends Id> {
    Response<IdType> read(Request<IdType> request);

    default Response<IdType> update(Request<IdType> request) {
        throw new IllegalStateException();
    }

    default Response<IdType> reset(Request<IdType> request) {
        throw new IllegalStateException();
    }

    default Response<IdType> create(Request<IdType> request) {
        throw new IllegalStateException();
    }

    default Response<IdType> delete(Request<IdType> request) {
        throw new IllegalStateException();
    }
}
