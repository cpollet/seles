package net.cpollet.read.v2.client;

import net.cpollet.read.v2.api.AttributeStore;
import net.cpollet.read.v2.api.domain.Request;
import net.cpollet.read.v2.client.domain.PersonId;
import net.cpollet.read.v2.client.domain.PortfolioId;
import net.cpollet.read.v2.impl.AttributeDef;
import net.cpollet.read.v2.impl.DefaultAttributeStore;
import net.cpollet.read.v2.impl.ReadImpl;
import net.cpollet.read.v2.impl.methods.FetchResult;
import net.cpollet.read.v2.impl.methods.Method;
import net.cpollet.read.v2.impl.methods.NestedMethod;
import net.cpollet.read.v2.impl.methods.StandardMethod;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Reader {
    private static final Method<PortfolioId> standardPortfolio = new StandardMethod<>();
    private static final Method<PersonId> standardPerson = new StandardMethod<>();

    public static void main(String[] args) {
        AttributeStore<PortfolioId> portfolioAttributeStore = new DefaultAttributeStore<>("portfolio");
        AttributeStore<PersonId> personAttributeStore = new DefaultAttributeStore<>("person");

        ReadImpl<PortfolioId> portfolioReader = new ReadImpl<>(portfolioAttributeStore, new DefaultIdValidator<>());
        ReadImpl<PersonId> personReader = new ReadImpl<>(personAttributeStore, new DefaultIdValidator<>());

        configurePortfolioAttributesStore(portfolioAttributeStore, personReader);
        configurePersonAttributesStore(personAttributeStore, portfolioReader);

        System.out.println(
                portfolioReader.execute(
                        new Request<>(
                                Arrays.asList(
                                        new PortfolioId("100000"),
                                        new PortfolioId("200000"),
                                        new PortfolioId("999999")
                                ),
                                Arrays.asList(
                                        "id",
                                        "status",
                                        "ownerId",
                                        "currency",
                                        "unknown",
                                        "owner.email",
                                        "owner.unknown",
                                        "owner.id_unknown",
                                        "owner.portfolio.id"
                                )
                        )
                )
        );
        System.out.println(
                portfolioReader.execute(
                        new Request<>(
                                Collections.singletonList(new PortfolioId("100000")),
                                Collections.singletonList("*")
                        )
                )
        );
    }

    private static void configurePortfolioAttributesStore(AttributeStore<PortfolioId> portfolioAttributeStore, ReadImpl<PersonId> personReader) {
        portfolioAttributeStore.add("id", new AttributeDef<>("id", standardPortfolio));
        portfolioAttributeStore.add("status", new AttributeDef<>("status", standardPortfolio));
        portfolioAttributeStore.add("currency", new AttributeDef<>("currency", standardPortfolio));
        portfolioAttributeStore.add("ownerId", new AttributeDef<>("ownerId", new Method<PortfolioId>() {
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
        portfolioAttributeStore.add(new NestedMethod<>(
                "owner", portfolioAttributeStore.fetch("ownerId"), personReader, o -> new PersonId((Integer) o)
        ));
    }

    private static void configurePersonAttributesStore(AttributeStore<PersonId> personAttributeStore, ReadImpl<PortfolioId> portfolioReader) {
        personAttributeStore.add("email", new AttributeDef<>("email", standardPerson));
        personAttributeStore.add("portfolioId", new AttributeDef<>("portfolioId", new Method<PersonId>() {
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
        personAttributeStore.add(new NestedMethod<>(
                "portfolio", personAttributeStore.fetch("portfolioId"), portfolioReader, o -> new PortfolioId((String) o)
        ));
    }
}
