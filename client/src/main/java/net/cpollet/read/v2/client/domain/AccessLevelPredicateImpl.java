package net.cpollet.read.v2.client.domain;

import net.cpollet.read.v2.api.attribute.AccessLevelPredicate;
import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;

import java.security.Principal;

public class AccessLevelPredicateImpl implements AccessLevelPredicate<Id> {
    @Override
    public boolean test(Principal principal, AttributeDef<Id> attribute) {
        return attribute.accessLevel() == AccessLevelImpl.PRIVATE;
    }
}
