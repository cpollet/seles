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
package net.cpollet.seles.impl.attribute;

import net.cpollet.seles.api.attribute.AttributeDef;
import net.cpollet.seles.impl.testsupport.StringId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@SuppressWarnings("squid:S00100")
class DirectAttributeStoreTest {
    @Test
    void fetch_returnsAttribute_whenFound() {
        // GIVEN
        AttributeDef<StringId> attribute = new AttributeDef<>("idAttribute", null, false, null, null, null, null);
        DirectAttributeStore<StringId> store = new DirectAttributeStore<>("idAttribute", Collections.singletonList(
                attribute
        ));

        // WHEN
        Optional<AttributeDef<StringId>> optionalAttribute = store.fetch("idAttribute");

        // THEN
        Assertions.assertThat(optionalAttribute)
                .isPresent()
                .contains(attribute);
    }

    @Test
    void fetch_returnsEmptyAttribute_whenNotFound() {
        // GIVEN
        AttributeDef<StringId> attribute = new AttributeDef<>("idAttribute", null, false, null, null, null, null);
        DirectAttributeStore<StringId> store = new DirectAttributeStore<>("idAttribute", Collections.singletonList(
                attribute
        ));

        // WHEN
        Optional<AttributeDef<StringId>> optionalAttribute = store.fetch("unkonown");

        // THEN
        Assertions.assertThat(optionalAttribute)
                .isNotPresent();
    }

    @Test
    void idAttribute_returnsIdAttribute_whenDefined() {
        // GIVEN
        AttributeDef<StringId> attribute = new AttributeDef<>("idAttribute", null, false, null, null, null, null);
        DirectAttributeStore<StringId> store = new DirectAttributeStore<>("idAttribute", Collections.singletonList(
                attribute
        ));

        // WHEN
        Optional<AttributeDef<StringId>> optionalAttribute = store.idAttribute();

        // THEN
        Assertions.assertThat(optionalAttribute)
                .isPresent()
                .contains(attribute);
    }

    @Test
    void idAttribute_returnsEmptyIdAttribute_whenNoAttributes() {
        // GIVEN
        DirectAttributeStore<StringId> store = new DirectAttributeStore<>("idAttribute", Collections.emptyList());

        // WHEN
        Optional<AttributeDef<StringId>> optionalAttribute = store.idAttribute();

        // THEN
        Assertions.assertThat(optionalAttribute)
                .isNotPresent();
    }

    @Test
    void idAttribute_returnsEmptyIdAttribute_whenNotFound() {
        // GIVEN
        AttributeDef<StringId> attribute = new AttributeDef<>("idAttribute", null, false, null, null, null, null);
        DirectAttributeStore<StringId> store = new DirectAttributeStore<>("unknown", Collections.singletonList(
                attribute
        ));

        // WHEN
        Optional<AttributeDef<StringId>> optionalAttribute = store.idAttribute();

        // THEN
        Assertions.assertThat(optionalAttribute)
                .isNotPresent();
    }

    @Test
    void idAttribute_returnsEmptyIdAttribute_whenNotDefined() {
        // GIVEN
        AttributeDef<StringId> attribute = new AttributeDef<>("idAttribute", null, false, null, null, null, null);
        DirectAttributeStore<StringId> store = new DirectAttributeStore<>(Collections.singletonList(
                attribute
        ));

        // WHEN
        Optional<AttributeDef<StringId>> optionalAttribute = store.idAttribute();

        // THEN
        Assertions.assertThat(optionalAttribute)
                .isNotPresent();
    }

    @Test
    void attributes_returnsAttributesCollection() {
        // GIVEN
        AttributeDef<StringId> attribute = new AttributeDef<>("idAttribute", null, false, null, null, null, null);
        DirectAttributeStore<StringId> store = new DirectAttributeStore<>("idAttribute", Collections.singletonList(
                attribute
        ));

        // WHEN
        Collection<AttributeDef<StringId>> attributes = store.attributes();

        // THEN
        Assertions.assertThat(attributes)
                .hasSize(1)
                .contains(attribute);
    }

    @Test
    void attributes_returnsUnmodifiableCollection() {
        AttributeDef<StringId> attribute = new AttributeDef<>("idAttribute", null, false, null, null, null, null);
        DirectAttributeStore<StringId> store = new DirectAttributeStore<>("idAttribute", Collections.singletonList(
                attribute
        ));

        Collection<AttributeDef<StringId>> attributes = store.attributes();

        try {
            // WHEN
            attributes.add(new AttributeDef<>("attribute", null, false, null, null, null, null));
        } catch (UnsupportedOperationException e) {
            // THEN
            return;
        }

        Assertions.fail("exception not thrown");
    }
}
