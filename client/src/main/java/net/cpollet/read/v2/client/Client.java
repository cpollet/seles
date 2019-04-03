package net.cpollet.read.v2.client;

import com.google.gson.GsonBuilder;
import net.cpollet.read.v2.api.attribute.AttributeStore;
import net.cpollet.read.v2.api.attribute.printer.AttributeStoreMetadataPrinter;
import net.cpollet.read.v2.api.execution.Executor;
import net.cpollet.read.v2.api.execution.Request;
import net.cpollet.read.v2.client.domain.PortfolioId;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Client {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        @SuppressWarnings("unchecked") Executor<PortfolioId> portfolioExecutor = (Executor<PortfolioId>) context.getBean("portfolio.executor");
        @SuppressWarnings("unchecked") AttributeStore<PortfolioId> portfolioAttributeStore = (AttributeStore<PortfolioId>) context.getBean("portfolio.attributeStore");

        System.out.println(
                new GsonBuilder().setPrettyPrinting().create().toJson(portfolioAttributeStore.print(new AttributeStoreMetadataPrinter()))
        );
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
                                        "owner.portfolio.id",
                                        "owner.address.street" // TODO implement
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
}
