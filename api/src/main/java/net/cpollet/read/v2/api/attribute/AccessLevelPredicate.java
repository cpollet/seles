package net.cpollet.read.v2.api.attribute;

import net.cpollet.read.v2.api.domain.Id;

import java.security.Principal;

public interface AccessLevelPredicate<T extends Id> {
    boolean test(Principal principal, AttributeDef<T> attribute);
}
