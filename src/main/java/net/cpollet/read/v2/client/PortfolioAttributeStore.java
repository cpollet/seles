package net.cpollet.read.v2.client;

import net.cpollet.read.v2.api.domain.Id;
import net.cpollet.read.v2.client.domain.PortfolioId;
import net.cpollet.read.v2.impl.AttributeDef;
import net.cpollet.read.v2.impl.AttributeStore;
import net.cpollet.read.v2.impl.methods.FetchResult;
import net.cpollet.read.v2.impl.methods.Method;
import net.cpollet.read.v2.impl.methods.NestedMethod;
import net.cpollet.read.v2.impl.methods.StandardMethod;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PortfolioAttributeStore implements AttributeStore<PortfolioId> {
    private static final Method standard = new StandardMethod<PortfolioId>();

    private final Map<String, AttributeDef<PortfolioId>> attributes;

    @SuppressWarnings("unchecked")
    public PortfolioAttributeStore() {
        attributes = new HashMap<>();
        attributes.put("id", new AttributeDef<>("id", standard));
        attributes.put("status", new AttributeDef<>("status", standard));
        attributes.put("ownerId", new AttributeDef<>("ownerId", new Method<PortfolioId>() {
            @Override
            public FetchResult<PortfolioId> fetch(List<AttributeDef<PortfolioId>> attributes, Collection<PortfolioId> ids) {
                return new FetchResult<>(
                        ids.stream()
                                .collect(Collectors.toMap(
                                        id -> id,
                                        id -> attributes.stream()
                                                .collect(Collectors.toMap(
                                                        a -> a,
                                                        a -> Integer.valueOf(id.get())
                                                ))
                                        )

                                )
                );
            }
        }));
        attributes.put("currency", new AttributeDef<>("currency", standard));
    }

    @Override
    public AttributeDef<PortfolioId> fetch(String attributeName) {
        return attributes.getOrDefault(attributeName, AttributeDef.invalid(attributeName));
    }

    @Override
    public <NestedIdType extends Id> void nest(Collection<String> nestedAttributes, NestedMethod<PortfolioId, NestedIdType> nestedMethod) {
        nestedAttributes.forEach(
                a -> attributes.put(a, new AttributeDef<>(a, nestedMethod))
        );
    }
}
