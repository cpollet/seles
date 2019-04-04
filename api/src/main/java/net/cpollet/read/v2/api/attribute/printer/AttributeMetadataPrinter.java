package net.cpollet.read.v2.api.attribute.printer;

import net.cpollet.read.v2.api.attribute.AttributeDef;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AttributeMetadataPrinter implements AttributePrinter<Map<String, String>> {
    private final Map<String, String> document;

    public AttributeMetadataPrinter() {
        this.document = new HashMap<>();
    }

    @Override
    public Map<String, String> print() {
        return document;
    }

    @Override
    public AttributePrinter<Map<String, String>> name(String name) {
        document.put("name", name);
        return this;
    }

    @Override
    public AttributePrinter<Map<String, String>> filtered(boolean filtered) {
        document.put("filtered", String.valueOf(filtered));
        return this;
    }

    @Override
    public AttributePrinter<Map<String, String>> deprecated(boolean deprecated) {
        document.put("deprecated", String.valueOf(deprecated));
        return this;
    }

    @Override
    public AttributePrinter<Map<String, String>> modes(Set<AttributeDef.Mode> modes) {
        document.put("modes", modes.stream().map(Enum::toString).collect(Collectors.joining(",")));
        return this;
    }
}
