package net.cpollet.seles.api.attribute.printer;

import net.cpollet.seles.api.attribute.AccessLevel;
import net.cpollet.seles.api.attribute.AttributeDef;

import java.util.Set;

public interface AttributePrinter<T> {
    T print();

    AttributePrinter<T> name(String name);

    AttributePrinter<T> accessLevel(AccessLevel accessLevel);

    AttributePrinter<T> deprecated(boolean deprecated);

    AttributePrinter<T> modes(Set<AttributeDef.Mode> modes);
}
