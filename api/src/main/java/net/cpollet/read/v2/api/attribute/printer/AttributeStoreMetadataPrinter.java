package net.cpollet.read.v2.api.attribute.printer;

import net.cpollet.read.v2.api.attribute.AttributeDef;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AttributeStoreMetadataPrinter implements AttributeStorePrinter<Map<String, String>> {
    private final Set<Map<String, String>> document;

    public AttributeStoreMetadataPrinter() {
        this.document = new HashSet<>();
    }

    @Override
    public Set<Map<String, String>> print() {
        return document;
    }

    @Override
    public AttributeStorePrinter<Map<String, String>> attribute(AttributeDef<?> attribute) {
        document.add(attribute.print(new AttributeMetadataPrinter()));
        return this;
    }
}
