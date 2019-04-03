package net.cpollet.read.v2.impl.attribute;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.attribute.printer.AttributeStorePrinter;
import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.api.execution.Executor;
import net.cpollet.read.v2.impl.conversion.NoopValueConverter;
import net.cpollet.read.v2.impl.methods.NestedMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NestedAttributeStore<IdType extends Id> implements AttributeStore<IdType> {
    private static final Logger LOGGER = LoggerFactory.getLogger(NestedAttributeStore.class);

    private final AttributeStore<IdType> parentStore;
    private final String context;

    private final Collection<NestedMethod<IdType, Id>> nestedAttributes;
    private final Map<String, AttributeDef<IdType>> nestedAttributesCache;

    public NestedAttributeStore(AttributeStore<IdType> parentStore, String context, List<NestedAttributes> attributes) {
        this.parentStore = parentStore;
        this.context = context;
        this.nestedAttributes = attributes.stream()
                .map(a -> new NestedMethod<IdType, Id>(
                                a.prefix,
                                parentStore.fetch(a.attribute).orElseThrow(IllegalArgumentException::new),
                                a.executor,
                                a.idProvider
                        )
                )
                .collect(Collectors.toList());

        this.nestedAttributesCache = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<AttributeDef<IdType>> fetch(String attributeName) {
        Optional<AttributeDef<IdType>> attribute = parentStore.fetch(attributeName);

        if (attribute.isPresent()) {
            return attribute;
        }

        return fetchNested(attributeName);
    }

    private Optional<AttributeDef<IdType>> fetchNested(String attributeName) {
        if (nestedAttributesCache.containsKey(attributeName)) {
            return Optional.of(nestedAttributesCache.get(attributeName));
        }

        return nestedAttributes.stream()
                .filter(m -> m.supports(attributeName))
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        l -> {
                            if (l.isEmpty()) {
                                LOGGER.info("Attribute '{}:{}' does not exist", context, attributeName);
                                return Optional.empty();
                            }

                            if (l.size() > 1) {
                                LOGGER.info("Attribute '{}:{}' is supported by multiple nested methods, expected only one", context, attributeName);
                                return Optional.empty();
                            }

                            AttributeDef<IdType> attributeDef = new AttributeDef<>(
                                    attributeName,
                                    false,
                                    false,
                                    l.get(0),
                                    NoopValueConverter.instance(),
                                    NoopValueConverter.instance()
                            );
                            nestedAttributesCache.put(attributeName, attributeDef);
                            return Optional.of(attributeDef);
                        }
                ));
    }

    @Override
    public Collection<AttributeDef<IdType>> directAttributes() {
        return parentStore.directAttributes();
    }

    @Override
    public <T> Set<T> print(AttributeStorePrinter<T> printer) {
        return parentStore.print(printer);
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
