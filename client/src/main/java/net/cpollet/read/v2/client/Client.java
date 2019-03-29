package net.cpollet.read.v2.client;

import net.cpollet.read.v2.api.Executor;
import net.cpollet.read.v2.api.domain.Request;
import net.cpollet.read.v2.client.domain.PersonId;
import net.cpollet.read.v2.client.domain.PortfolioId;
import net.cpollet.read.v2.impl.AttributeDef;
import net.cpollet.read.v2.impl.AttributeStore;
import net.cpollet.read.v2.impl.CachedIdValidator;
import net.cpollet.read.v2.impl.DefaultAttributeStore;
import net.cpollet.read.v2.impl.ExecutorImpl;
import net.cpollet.read.v2.impl.methods.FetchResult;
import net.cpollet.read.v2.impl.methods.Method;
import net.cpollet.read.v2.impl.methods.NestedMethod;
import net.cpollet.read.v2.impl.methods.StandardMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private static final Method<PortfolioId> standardPortfolio = new StandardMethod<>();
    private static final Method<PersonId> standardPerson = new StandardMethod<>();

    public static void main(String[] args) {
        AttributeStore<PortfolioId> portfolioAttributeStore = new DefaultAttributeStore<>("portfolio");
        AttributeStore<PersonId> personAttributeStore = new DefaultAttributeStore<>("person");

        Executor<PortfolioId> portfolioExecutor = new ExecutorImpl<>(
                portfolioAttributeStore,
                new CachedIdValidator<>(new DefaultIdValidator<>())
        );
        Executor<PersonId> personExecutor = new ExecutorImpl<>(
                personAttributeStore,
                new CachedIdValidator<>(new DefaultIdValidator<>())
        );

        configurePortfolioAttributesStore(portfolioAttributeStore, personExecutor);
        configurePersonAttributesStore(personAttributeStore, portfolioExecutor);

        read(portfolioExecutor);
        write(portfolioExecutor);
    }

    private static void read(Executor<PortfolioId> portfolioExecutor) {
        System.out.println(
                portfolioExecutor.read(
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
                portfolioExecutor.read(
                        new Request<>(
                                Collections.singletonList(new PortfolioId("200000")),
                                Collections.singletonList("*")
                        )
                )
        );
    }

    private static void write(Executor<PortfolioId> portfolioExecutor) {
        Map<String, Object> attributesValues = new HashMap<>();
        attributesValues.put("status", 40);
        attributesValues.put("ownerId", 123123);
        attributesValues.put("currency", "CHF");
        attributesValues.put("hidden", "hidden");
        attributesValues.put("unknown", "?");

        System.out.println(
                portfolioExecutor.update(
                        new Request<>(
                                Arrays.asList(
                                        new PortfolioId("100000"),
                                        new PortfolioId("200000"),
                                        new PortfolioId("999999")
                                ),
                                attributesValues
                        )
                )
        );
    }

    private static void configurePortfolioAttributesStore(AttributeStore<PortfolioId> portfolioAttributeStore, Executor<PersonId> personExecutor) {
        portfolioAttributeStore.add("id", new AttributeDef<>("id", standardPortfolio));
        portfolioAttributeStore.add("status", new AttributeDef<>("status", standardPortfolio));
        portfolioAttributeStore.add("currency", new AttributeDef<>("currency", standardPortfolio));
        portfolioAttributeStore.add("hidden", new AttributeDef<>("hidden", standardPortfolio));
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

            @Override
            public Collection<String> update(Map<AttributeDef<PortfolioId>, Object> attributeValues, Collection<PortfolioId> ids) {
                ids.forEach(
                        id -> attributeValues.forEach((a, v) -> LOGGER.info("{}:{} -> {}", id, a, v))
                );

                return Collections.emptyList();
            }
        }));
        portfolioAttributeStore.add(new NestedMethod<>(
                "owner", portfolioAttributeStore.fetch("ownerId").orElseThrow(IllegalStateException::new), personExecutor, o -> new PersonId((Integer) o)
        ));
    }

    private static void configurePersonAttributesStore(AttributeStore<PersonId> personAttributeStore, Executor<PortfolioId> portfolioExecutor) {
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

            @Override
            public Collection<String> update(Map<AttributeDef<PersonId>, Object> attributeValues, Collection<PersonId> ids) {
                ids.forEach(
                        id -> attributeValues.forEach((a, v) -> LOGGER.info("{}:{} -> {}", id, a, v))
                );

                return Collections.emptyList();
            }
        }));
        personAttributeStore.add(new NestedMethod<>(
                "portfolio", personAttributeStore.fetch("portfolioId").orElseThrow(IllegalStateException::new), portfolioExecutor, o -> new PortfolioId((String) o)
        ));
    }
}
