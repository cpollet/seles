/*
 * Copyright 2019 Christophe Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.cpollet.seles.client;

import com.google.gson.GsonBuilder;
import net.cpollet.seles.api.attribute.AttributeStore;
import net.cpollet.seles.api.attribute.printer.AttributeMetadataPrinter;
import net.cpollet.seles.api.execution.Executor;
import net.cpollet.seles.api.execution.Request;
import net.cpollet.seles.client.domain.DefaultPrincipal;
import net.cpollet.seles.client.domain.PortfolioId;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Client {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
        @SuppressWarnings("unchecked") Executor<PortfolioId> portfolioExecutor = (Executor<PortfolioId>) context.getBean("portfolio.executor");
        @SuppressWarnings("unchecked") AttributeStore<PortfolioId> portfolioAttributeStore = (AttributeStore<PortfolioId>) context.getBean("portfolio.attributeStore");

        printMetadata(portfolioAttributeStore);
        read(portfolioExecutor);
        write(portfolioExecutor);
        delete(portfolioExecutor);
        create(portfolioExecutor);
        search(portfolioExecutor);
    }

    private static void printMetadata(AttributeStore<PortfolioId> portfolioAttributeStore) {
        System.out.println("-- METADATA ------");
        System.out.println(
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(
                                portfolioAttributeStore.attributes().stream()
                                        .map(a -> a.print(new AttributeMetadataPrinter()))
                                        .collect(Collectors.toList())
                        )
        );
    }

    private static void read(Executor<PortfolioId> portfolioExecutor) {
        System.out.println("-- READ ------");
        System.out.println(
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(
                                portfolioExecutor.read(
                                        Request.read(
                                                new DefaultPrincipal("name"),
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
                                                        "owner.portfolioId",
                                                        "owner.address.street"
                                                )
                                        )
                                )
                        )
        );
        System.out.println(
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(
                                portfolioExecutor.read(
                                        Request.read(
                                                new DefaultPrincipal("name"),
                                                Collections.singletonList(new PortfolioId("200000")),
                                                Collections.singletonList("*")
                                        )
                                )
                        )
        );
    }

    private static void write(Executor<PortfolioId> portfolioExecutor) {
        System.out.println("-- WRITE ------");
        Map<String, Object> attributesValues = new HashMap<>();
        attributesValues.put("status", 40);
        attributesValues.put("ownerId", 123123);
        attributesValues.put("currency", "CHF");
        attributesValues.put("hidden", "hidden");
        attributesValues.put("unknown", "?");

        System.out.println(
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(
                                portfolioExecutor.update(
                                        Request.write(
                                                new DefaultPrincipal("name"),
                                                Arrays.asList(
                                                        new PortfolioId("100000"),
                                                        new PortfolioId("200000"),
                                                        new PortfolioId("999999")
                                                ),
                                                attributesValues
                                        )
                                )
                        )
        );
    }

    private static void delete(Executor<PortfolioId> portfolioExecutor) {
        System.out.println("-- DELETE ------");
        System.out.println(
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(
                                portfolioExecutor.delete(
                                        Request.delete(
                                                new DefaultPrincipal("name"),
                                                Arrays.asList(
                                                        new PortfolioId("100000"),
                                                        new PortfolioId("200000"),
                                                        new PortfolioId("999999")
                                                ),
                                                Arrays.asList(
                                                        "id",
                                                        "status"
                                                )
                                        )
                                )
                        )
        );
    }

    private static void create(Executor<PortfolioId> portfolioExecutor) {
        System.out.println("-- CREATE ------");
        Map<String, Object> attributesValues = new HashMap<>();
        attributesValues.put("status", 40);
        attributesValues.put("ownerId", 123123);
        attributesValues.put("currency", "CHF");
        attributesValues.put("description", "lol");

        System.out.println(
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(
                                portfolioExecutor.create(
                                        Request.create(
                                                new DefaultPrincipal("name"),
                                                attributesValues
                                        )
                                )
                        )
        );
    }

    private static void search(Executor<PortfolioId> portfolioExecutor) {
        System.out.println("-- SEARCH ------");
        Map<String, Object> attributesValues = new HashMap<>();
        attributesValues.put("status", 30);
        attributesValues.put("ownerId", 123456);
        attributesValues.put("currency", "CHF");
        attributesValues.put("description", "...");

        System.out.println(
                new GsonBuilder()
                        .setPrettyPrinting()
                        .create()
                        .toJson(
                                portfolioExecutor.search(
                                        Request.search(
                                                new DefaultPrincipal("name"),
                                                attributesValues
                                        )
                                )
                        )
        );
    }
}
