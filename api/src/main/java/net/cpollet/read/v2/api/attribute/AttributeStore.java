package net.cpollet.read.v2.api.attribute;

import net.cpollet.read.v2.api.attribute.printer.AttributeStorePrinter;
import net.cpollet.read.v2.api.domain.Id;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface AttributeStore<IdType extends Id> {
    Optional<AttributeDef<IdType>> fetch(String attributeName);

    Collection<AttributeDef<IdType>> attributes();

    // FIXME really useful?
    <T> Set<T> print(AttributeStorePrinter<T> printer);
}
