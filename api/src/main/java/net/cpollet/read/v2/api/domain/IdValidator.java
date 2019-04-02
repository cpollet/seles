package net.cpollet.read.v2.api.domain;

import net.cpollet.read.v2.api.domain.Id;

import java.util.Collection;

public interface IdValidator<IdType extends Id> {
    Collection<IdType> invalidIds(Collection<IdType> ids);
}
