package net.cpollet.read.v2.api.attribute.printer;

import net.cpollet.read.v2.api.attribute.AccessLevel;
import net.cpollet.read.v2.api.attribute.AttributeDef;

import java.util.Set;

public interface AttributePrinter<T> {
    T print();

    AttributePrinter<T> name(String name);

    AttributePrinter<T> accessLevel(AccessLevel accessLevel);

    AttributePrinter<T> deprecated(boolean deprecated);

    AttributePrinter<T> modes(Set<AttributeDef.Mode> modes);
}
