package net.cpollet.seles.client.domain;

import net.cpollet.seles.api.attribute.AccessLevelPredicate;
import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.api.domain.Id;

import java.security.Principal;

public class AccessLevelPredicateImpl implements AccessLevelPredicate<Id> {
    @Override
    public boolean test(Principal principal, AttributeDef<Id> attribute) {
        return attribute.accessLevel() == AccessLevelImpl.PRIVATE;
    }
}
