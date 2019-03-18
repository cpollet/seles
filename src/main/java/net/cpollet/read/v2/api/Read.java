package net.cpollet.read.v2.api;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.domain.Request;
import net.cpollet.read.v2.api.domain.Response;

public interface Read<IdType extends Id> {
    Response<IdType> execute(Request<IdType> request);
}
