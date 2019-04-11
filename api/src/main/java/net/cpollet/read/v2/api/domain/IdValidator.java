package net.cpollet.read.v2.api.domain;

import java.util.Collection;

public interface IdValidator<T extends Id> {
    Collection<T> invalidIds(Collection<T> ids);
}
