package net.cpollet.read.v2.impl.testsupport;

import net.cpollet.read.v2.api.attribute.AccessLevel;

public class VoidAccessLevel implements AccessLevel {
    public static final VoidAccessLevel INSTANCE = new VoidAccessLevel();

    private VoidAccessLevel() {
        //nothing
    }
}
