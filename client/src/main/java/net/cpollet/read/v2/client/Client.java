package net.cpollet.read.v2.client;

import net.cpollet.read.v2.api.attribute.AttributeDef;
import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.execution.Executor;
import net.cpollet.read.v2.api.methods.FetchResult;
import net.cpollet.read.v2.api.methods.Method;
import net.cpollet.read.v2.api.execution.Request;
import net.cpollet.read.v2.client.domain.PersonId;
import net.cpollet.read.v2.client.domain.PortfolioId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("context.xml");

        AttributeStore<PortfolioId> portfolioAttributeStore = (AttributeStore<PortfolioId>) context.getBean("portfolio.attributeStore");
        Executor<PortfolioId> portfolioExecutor = (Executor<PortfolioId>) context.getBean("portfolio.executor");
        Method<PortfolioId> portfolioStandard = (Method<PortfolioId>) context.getBean("portfolio.standard");

        AttributeStore<PersonId> personAttributeStore = (AttributeStore<PersonId>) context.getBean("person.attributeStore");
        Executor<PersonId> personExecutor = (Executor<PersonId>) context.getBean("person.executor");
        Method<PersonId> personStandard = (Method<PersonId>) context.getBean("person.standard");

        configurePersonAttributesStore(personAttributeStore, portfolioExecutor, personStandard);
        configurePortfolioAttributesStore(portfolioAttributeStore, personExecutor, portfolioStandard);

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

    private static void configurePortfolioAttributesStore(AttributeStore<PortfolioId> portfolioAttributeStore, Executor<PersonId> personExecutor, Method<PortfolioId> standardPortfolio) {
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
        portfolioAttributeStore.nest(
                "owner",
                portfolioAttributeStore.fetch("ownerId").orElseThrow(IllegalStateException::new),
                personExecutor,
                o -> new PersonId((Integer) o)
        );
    }

    private static void configurePersonAttributesStore(AttributeStore<PersonId> personAttributeStore, Executor<PortfolioId> portfolioExecutor, Method<PersonId> standardPerson) {
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
        personAttributeStore.nest(
                "portfolio",
                personAttributeStore.fetch("portfolioId").orElseThrow(IllegalStateException::new),
                portfolioExecutor,
                o -> new PortfolioId((String) o)
        );
    }
}
