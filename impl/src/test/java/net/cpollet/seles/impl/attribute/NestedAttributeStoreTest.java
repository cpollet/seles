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
import net.cpollet.seles.api.attribute.AttributeStore;
import net.cpollet.seles.api.domain.Id;
import net.cpollet.seles.impl.conversion.NoopValueConverter;
import net.cpollet.seles.impl.methods.NestedMethod;
import net.cpollet.seles.impl.testsupport.NoopExecutor;
import net.cpollet.seles.impl.testsupport.StringId;
import net.cpollet.seles.impl.testsupport.VoidAccessLevel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("squid:S00100")
class NestedAttributeStoreTest {
    @Test
    void fetch_returnsAttribute_whenNotNested() {
        // GIVEN
        AttributeDef<StringId> attribute = new AttributeDef<>("idAttribute", null, false, null, null, null, null);
        DirectAttributeStore<StringId> store = new DirectAttributeStore<>("idAttribute", Collections.singleton(
                attribute
        ));
        NestedAttributeStore<StringId> nestedStore = new NestedAttributeStore<>(store, Collections.emptySet());

        // WHEN
        Optional<AttributeDef<StringId>> optionalAttribute = nestedStore.fetch("idAttribute");

        // THEN
        Assertions.assertThat(optionalAttribute)
                .isPresent()
                .contains(attribute);
    }

    @Test
    void fetch_returnsNestedAttribute_whenFound() {
        // GIVEN
        AttributeDef<StringId> innerId = new AttributeDef<>(
                "innerId",
                null,
                false,
                null,
                null,
                null,
                null
        );
        DirectAttributeStore<StringId> outerStore = new DirectAttributeStore<>(Collections.singleton(
                innerId
        ));

        AttributeDef<StringId> innerAttribute = new AttributeDef<>(
                "attribute",
                VoidAccessLevel.INSTANCE_1,
                false,
                null,
                Collections.singleton(AttributeDef.Mode.CREATE),
                null,
                null
        );
        DirectAttributeStore<StringId> innerStore = new DirectAttributeStore<>(Collections.singletonList(
                innerAttribute
        ));

        @SuppressWarnings("unchecked") Collection<NestedAttributeStore.NestedAttributes<Id>> list = Collections.singleton(
                new NestedAttributeStore.NestedAttributes(
                        "prefix", "innerId", new NoopExecutor<>(innerStore), o -> o
                )
        );
        NestedAttributeStore<StringId> nestedStore = new NestedAttributeStore<>(outerStore, list);

        // WHEN
        Optional<AttributeDef<StringId>> optionalAttribute = nestedStore.fetch("prefix.attribute");

        // THEN
        Assertions.assertThat(optionalAttribute)
                .isPresent()
                .hasValueSatisfying(a -> {
                            Assertions.assertThat(a.name())
                                    .isEqualTo("prefix.attribute");
                            Assertions.assertThat(a.accessLevel())
                                    .isSameAs(VoidAccessLevel.INSTANCE_1);
                            Assertions.assertThat(a.deprecated())
                                    .isFalse();
                            Assertions.assertThat(a.method())
                                    .isInstanceOf(NestedMethod.class);
                            Assertions.assertThat(a.modes())
                                    .isEqualTo(Collections.singleton(AttributeDef.Mode.CREATE));
                            Assertions.assertThat(a.caster())
                                    .isSameAs(NoopValueConverter.instance());
                            Assertions.assertThat(a.converter())
                                    .isSameAs(NoopValueConverter.instance());
                        }
                );
    }

    @Test
    void new_fails_whenNestedAttribute_alreadyExists() {
        // GIVEN
        AttributeDef<StringId> innerId = new AttributeDef<>(
                "innerId",
                null,
                false,
                null,
                null,
                null,
                null
        );
        AttributeDef<StringId> nested = new AttributeDef<>(
                "prefix.attribute",
                null,
                false,
                null,
                null,
                null,
                null
        );
        DirectAttributeStore<StringId> outerStore = new DirectAttributeStore<>(Arrays.asList(
                innerId, nested
        ));

        AttributeDef<StringId> innerAttribute = new AttributeDef<>(
                "attribute",
                VoidAccessLevel.INSTANCE_1,
                false,
                null,
                Collections.singleton(AttributeDef.Mode.CREATE),
                null,
                null
        );
        DirectAttributeStore<StringId> innerStore = new DirectAttributeStore<>(Collections.singletonList(
                innerAttribute
        ));

        @SuppressWarnings("unchecked") List<NestedAttributeStore.NestedAttributes<Id>> list = Collections.singletonList(
                new NestedAttributeStore.NestedAttributes(
                        "prefix", "innerId", new NoopExecutor<>(innerStore), o -> o
                )
        );

        try {
            new NestedAttributeStore<>(outerStore, list);
        } catch (IllegalStateException e) {
            Assertions.assertThat(e.getMessage())
                    .isEqualTo("Attribute [prefix.attribute] already exists");
            return;
        }

        Assertions.fail("Exception not thrown");
    }

    @Test
    void attributes_returnsAllAttributes() {
        // GIVEN
        AttributeDef<StringId> innerId = new AttributeDef<>(
                "innerId",
                null,
                false,
                null,
                null,
                null,
                null
        );
        DirectAttributeStore<StringId> outerStore = new DirectAttributeStore<>(Collections.singleton(
                innerId
        ));

        AttributeDef<StringId> innerAttribute = new AttributeDef<>(
                "attribute",
                VoidAccessLevel.INSTANCE_1,
                false,
                null,
                Collections.singleton(AttributeDef.Mode.CREATE),
                null,
                null
        );
        DirectAttributeStore<StringId> innerStore = new DirectAttributeStore<>(Collections.singletonList(
                innerAttribute
        ));

        @SuppressWarnings("unchecked") Collection<NestedAttributeStore.NestedAttributes<Id>> list = Collections.singleton(
                new NestedAttributeStore.NestedAttributes(
                        "prefix", "innerId", new NoopExecutor<>(innerStore), o -> o
                )
        );
        NestedAttributeStore<StringId> nestedStore = new NestedAttributeStore<>(outerStore, list);

        // WHEN
        Collection<AttributeDef<StringId>> attributes = nestedStore.attributes();

        // THEN
        Assertions.assertThat(attributes)
                .hasSize(2)
                .anyMatch(a -> a.name().equals("prefix.attribute"))
                .anyMatch(a -> a.name().equals("innerId"));
    }

    @Test
    void idAttribute_delegatesToParentStore() {
        // GIVEN
        AttributeStoreSpy store = new AttributeStoreSpy();
        NestedAttributeStore<StringId> nestedStore = new NestedAttributeStore<>(store, Collections.emptySet());

        // WHEN
        nestedStore.idAttribute();

        // THEN
        Assertions.assertThat(store.idAttributeCalled)
                .isTrue();
    }

    private class AttributeStoreSpy implements AttributeStore<StringId> {
        private boolean idAttributeCalled = false;

        @Override
        public Optional<AttributeDef<StringId>> fetch(String attributeName) {
            return Optional.empty();
        }

        @Override
        public Optional<AttributeDef<StringId>> idAttribute() {
            idAttributeCalled = true;
            return Optional.empty();
        }

        @Override
        public Collection<AttributeDef<StringId>> attributes() {
            return Collections.emptySet();
        }
    }
}
