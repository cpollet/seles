package net.cpollet.read.v2.api.attribute.printer;

import net.cpollet.read.v2.api.attribute.AttributeDef;

import java.util.Set;

public interface AttributeStorePrinter<T> {
    Set<T> print();

    AttributeStorePrinter<T> attribute(AttributeDef<?> attribute);
}
