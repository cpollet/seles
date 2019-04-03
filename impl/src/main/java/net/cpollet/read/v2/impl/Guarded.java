package net.cpollet.read.v2.impl;

public interface Guarded<T> {
    enum Flag {
        ATTRIBUTE_CONVERSION_ERROR,
        INVALID_IDS,
        INPUT_VALUE_CONVERSION_ERROR,
        UPDATE_ERROR
    }

    boolean hasGuardFlag(Flag flag);

    T addGuardedFlagIf(boolean condition, Flag flag);
}
