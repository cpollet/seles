/*
 * Copyright 2019 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
