package net.cpollet.read.v2.impl.attribute;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.attribute.AttributeStore;
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
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DefaultAttributeStore<IdType extends Id> implements AttributeStore<IdType> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAttributeStore.class);

    private final String context;
    private final Map<String, AttributeDef<IdType>> attributes;
    private final Map<String, AttributeDef<IdType>> nestedAttributesCache;
    private final Collection<NestedMethod<IdType, ? extends Id>> nestedAttributes;

    public DefaultAttributeStore(String context, List<AttributeDef<IdType>> attributes) {
        this.context = context;
        this.attributes = new ConcurrentHashMap<>(
                attributes.stream()
                        .collect(Collectors.toMap(
                                AttributeDef::name,
                                a -> a
                        ))
        );
        this.nestedAttributesCache = new ConcurrentHashMap<>();
        this.nestedAttributes = new Vector<>();
    }

    @Override
    public void add(String name, AttributeDef<IdType> def) {
        attributes.put(name, def);
    }

    @Override
    public <NestedIdType extends Id> void nest(String prefix, AttributeDef<IdType> attribute, Executor<NestedIdType> executor, Function<Object, NestedIdType> idProvider) {
        nestedAttributes.add(new NestedMethod<>(prefix, attribute, executor, idProvider));
    }

    @Override
    public Optional<AttributeDef<IdType>> fetch(String attributeName) {
        if (attributes.containsKey(attributeName)) {
            return Optional.of(attributes.get(attributeName));
        }
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
        return attributes.values();
    }
}
