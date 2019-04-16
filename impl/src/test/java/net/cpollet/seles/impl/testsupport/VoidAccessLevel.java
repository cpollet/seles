package net.cpollet.seles.impl.testsupport;

import net.cpollet.seles.api.attribute.AccessLevel;

public class VoidAccessLevel implements AccessLevel {
    public static final VoidAccessLevel INSTANCE = new VoidAccessLevel();

    private VoidAccessLevel() {
        //nothing
    }
}
