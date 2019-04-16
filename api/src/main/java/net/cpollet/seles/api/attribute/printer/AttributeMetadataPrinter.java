package net.cpollet.seles.api.attribute.printer;

import net.cpollet.seles.api.attribute.AccessLevel;
import net.cpollet.seles.api.attribute.AttributeDef;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class AttributeMetadataPrinter implements AttributePrinter<Map<String, Object>> {
    private final Map<String, Object> document;

    public AttributeMetadataPrinter() {
        this.document = new HashMap<>();
    }

    @Override
    public Map<String, Object> print() {
        return document;
    }

    @Override
    public AttributePrinter<Map<String, Object>> name(String name) {
        document.put("name", name);
        return this;
    }

    @Override
    public AttributePrinter<Map<String, Object>> accessLevel(AccessLevel accessLevel) {
        document.put("accessLevel", accessLevel.toString());
        return this;
    }

    @Override
    public AttributePrinter<Map<String, Object>> deprecated(boolean deprecated) {
        document.put("deprecated", deprecated);
        return this;
    }

    @Override
    public AttributePrinter<Map<String, Object>> modes(Set<AttributeDef.Mode> modes) {
        document.put("modes", modes);
        return this;
    }
}
