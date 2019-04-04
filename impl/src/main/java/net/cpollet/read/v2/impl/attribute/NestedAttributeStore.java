package net.cpollet.read.v2.impl.attribute;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.execution.Executor;
import net.cpollet.read.v2.impl.conversion.NoopValueConverter;
import net.cpollet.read.v2.impl.methods.NestedMethod;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NestedAttributeStore<IdType extends Id> implements AttributeStore<IdType> {
    private final Map<String, AttributeDef<IdType>> store;

    public NestedAttributeStore(AttributeStore<IdType> parentStore, List<NestedAttributes<Id>> attributes) {
        HashMap<String, AttributeDef<IdType>> tmpStore = new HashMap<>(
                parentStore.attributes().stream()
                        .collect(
                                Collectors.toMap(
                                        AttributeDef::name,
                                        a -> a
                                )
                        )
        );

        attributes.forEach(
                a -> {
                    NestedMethod<IdType, Id> method = new NestedMethod<>(
                            a.prefix,
                            parentStore.fetch(a.attribute).orElseThrow(IllegalArgumentException::new),
                            a.executor,
                            a.idProvider
                    );
                    a.executor.attributeStore().attributes().forEach(
                            na -> {
                                String attributeName = String.format("%s.%s", a.prefix, na.name());

                                if (tmpStore.containsKey(attributeName)) {
                                    throw new IllegalStateException(String.format("Attribute [%s] already exists", attributeName));
                                }

                                tmpStore.put(
                                        attributeName,
                                        new AttributeDef<>(
                                                attributeName,
                                                na.filtered(),
                                                na.deprecated(),
                                                method,
                                                na.modes(),
                                                NoopValueConverter.instance(),
                                                NoopValueConverter.instance()
                                        )
                                );
                            }
                    );

                }
        );

        this.store = Collections.unmodifiableMap(tmpStore);
    }

    @Override
    public Optional<AttributeDef<IdType>> fetch(String attributeName) {
        return Optional.ofNullable(store.get(attributeName));
    }

    @Override

    public Collection<AttributeDef<IdType>> attributes() {
        return store.values();
    }

    public static class NestedAttributes<NestedIdType extends Id> {
        private final String prefix;
        private final String attribute;
        private final Executor<NestedIdType> executor;
        private final Function<Object, NestedIdType> idProvider;

        public NestedAttributes(String prefix, String attribute, Executor<NestedIdType> executor, Function<Object, NestedIdType> idProvider) {
            this.prefix = prefix;
            this.attribute = attribute;
            this.executor = executor;
            this.idProvider = idProvider;
        }
    }
}
