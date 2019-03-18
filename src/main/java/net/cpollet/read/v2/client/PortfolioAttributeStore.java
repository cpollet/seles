package net.cpollet.read.v2.client;

import net.cpollet.read.v2.client.domain.PersonId;
import net.cpollet.read.v2.client.domain.PortfolioId;
import net.cpollet.read.v2.impl.AttributeDef;
import net.cpollet.read.v2.impl.AttributeStore;
import net.cpollet.read.v2.impl.ReadImpl;
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
    static final PortfolioAttributeStore INSTANCE = new PortfolioAttributeStore();
    private static final Method standard = new StandardMethod<PortfolioId>();

    private final Map<String, AttributeDef<PortfolioId>> attributes;

    private PortfolioAttributeStore() {
        attributes = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void init() {
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

        NestedMethod<PortfolioId> nested = new NestedMethod<>(
                "owner",
                attributes.get("ownerId"),
                new ReadImpl<>(PersonAttributeStore.INSTANCE),
                o -> new PersonId((Integer) o)
        );

        // FIXME dynamic but cached nested attributes
        attributes.put("owner.email", new AttributeDef<>("owner.email", nested));
        attributes.put("owner.id", new AttributeDef<>("owner.id", nested));
        attributes.put("owner.portfolio.id", new AttributeDef<>("owner.portfolio.id", nested));
    }

    public AttributeDef<PortfolioId> fetch(String attributeName) {
        return attributes.getOrDefault(attributeName, AttributeDef.invalid(attributeName));
    }
}
