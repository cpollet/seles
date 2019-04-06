package net.cpollet.read.v2.api.domain;

import java.util.Collection;

public interface IdValidator<IdType extends Id> {
    Collection<IdType> invalidIds(Collection<IdType> ids);
}
