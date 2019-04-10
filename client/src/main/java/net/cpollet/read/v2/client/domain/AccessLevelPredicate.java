package net.cpollet.read.v2.client.domain;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;

import java.util.function.Predicate;

public class AccessLevelPredicate<IdType extends Id> implements Predicate<AttributeDef<IdType>> {
    @Override
    public boolean test(AttributeDef<IdType> attribute) {
        return attribute.accessLevel() == AccessLevelImpl.PRIVATE;
    }
}
