package net.cpollet.read.v2.api.attribute;

import net.cpollet.read.v2.api.domain.Id;

public interface AccessLevelPredicate<T extends Id> {
    boolean test(AttributeDef<T> attribute);
}
