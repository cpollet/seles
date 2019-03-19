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

public class PersonAttributeStore implements AttributeStore<PersonId> {
    static final PersonAttributeStore INSTANCE = new PersonAttributeStore();
    private static final Method standard = new StandardMethod<PortfolioId>();

    private final Map<String, AttributeDef<PersonId>> attributes;

    private PersonAttributeStore() {
        attributes = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void init() {
        attributes.put("email", new AttributeDef<>("email", standard));
        attributes.put("portfolioId", new AttributeDef<>("portfolioId", new Method<PersonId>() {
            @Override
            public FetchResult<PersonId> fetch(List<AttributeDef<PersonId>> attributes, Collection<PersonId> ids) {
                return new FetchResult<>(
                        ids.stream()
                                .collect(Collectors.toMap(
                                        id -> id,
                                        id -> attributes.stream()
                                                .collect(Collectors.toMap(
                                                        a -> a,
                                                        a -> String.valueOf(id.get())
                                                ))
                                        )

                                )
                );
            }
        }));

        NestedMethod<PersonId, PortfolioId> nested = new NestedMethod<>(
                "portfolio",
                attributes.get("portfolioId"),
                new ReadImpl<>(PortfolioAttributeStore.INSTANCE),
                o -> new PortfolioId((String) o)
        );

        attributes.put("portfolio.id", new AttributeDef<>("portfolio.id", nested));
    }

    public AttributeDef<PersonId> fetch(String attributeName) {
        if (attributes.containsKey(attributeName)) {
            return attributes.get(attributeName);
        }

        return AttributeDef.invalid(attributeName);
    }
}
