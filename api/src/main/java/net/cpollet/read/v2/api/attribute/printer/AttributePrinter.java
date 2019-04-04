package net.cpollet.read.v2.api.attribute.printer;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.domain.Id;

import java.util.Set;

public interface AttributePrinter<T> {
    T print();

    AttributePrinter<T> name(String name);

    AttributePrinter<T> filtered(boolean filtered);

    AttributePrinter<T> deprecated(boolean deprecated);

    AttributePrinter<T> modes(Set<AttributeDef.Mode> modes);
}
