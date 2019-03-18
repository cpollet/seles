package net.cpollet.read.v2.impl;

import net.cpollet.read.v2.api.domain.Id;

public interface AttributeStore<IdType extends Id> {
    //private static final Method standard = new StandardMethod<>();

    AttributeDef<IdType> fetch(String attributeName);
    /*@SuppressWarnings("unchecked")
    public AttributeDef<IdType> fetch(String attributeName) {
        String localAttributeName = attributeName.split("\\.", 2)[0];
        switch (localAttributeName) {
            case "status":
                return new AttributeDef<>("status", standard);
            case "ownerId":
                return new AttributeDef<>("ownerId", standard);
            case "currency":
                return new AttributeDef<>("currency", new StandardMethod<>());
            case "owner":
                return new NestedMethod<>(new ReadImpl<PersonId>(new AttributeStore<>()));
            default:
                return AttributeDef.invalid(attributeName);
        }
    }*/
}
