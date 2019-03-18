package net.cpollet.read.v2.impl;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.impl.methods.Method;
import net.cpollet.read.v2.impl.methods.StandardMethod;

public class AttributeStore<IdType extends Id> {
    private static final Method standard = new StandardMethod<>();

    @SuppressWarnings("unchecked")
    public AttributeDef<IdType> fetch(String attributeName) {
        switch (attributeName) {
            case "status":
                return new AttributeDef<>("status", standard);
            case "ownerId":
                return new AttributeDef<>("ownerId", standard);
            case "currency":
                return new AttributeDef<>("currency", new StandardMethod<>());
            default:
                return AttributeDef.invalid(attributeName);
        }
    }
}
