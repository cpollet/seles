package net.cpollet.read.v2.api.attribute.printer;

import java.util.HashMap;
import java.util.Map;

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
}
