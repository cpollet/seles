package net.cpollet.read.v2.api.attribute;

import net.cpollet.read.v2.api.domain.Id;

public interface AccessLevelPredicate<IdType extends Id> {
    boolean test(AttributeDef<IdType> attribute);
}
