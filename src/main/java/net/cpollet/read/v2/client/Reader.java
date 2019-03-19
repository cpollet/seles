package net.cpollet.read.v2.client;

import net.cpollet.read.v2.api.domain.Request;
import net.cpollet.read.v2.api.domain.Response;
import net.cpollet.read.v2.client.domain.PersonId;
import net.cpollet.read.v2.client.domain.PortfolioId;
import net.cpollet.read.v2.impl.ReadImpl;
import net.cpollet.read.v2.impl.methods.NestedMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Reader {
    public static void main(String[] args) {
        PortfolioAttributeStore portfolioAttributeStore = new PortfolioAttributeStore();
        PersonAttributeStore personAttributeStore = new PersonAttributeStore();

        ReadImpl<PortfolioId> portfolioReader = new ReadImpl<>(portfolioAttributeStore);
        ReadImpl<PersonId> personReader = new ReadImpl<>(personAttributeStore);

        portfolioAttributeStore.nest(
                Arrays.asList("owner.email", "owner.id_unknown", "owner.portfolio.id"),
                new NestedMethod<>(
                        "owner", portfolioAttributeStore.fetch("ownerId"), personReader, o -> new PersonId((Integer) o)
                )
        );
        personAttributeStore.nest(
                Collections.singletonList("portfolio.id"),
                new NestedMethod<>(
                        "portfolio", personAttributeStore.fetch("portfolioId"), portfolioReader, o -> new PortfolioId((String) o)
                )
        );

        List<PortfolioId> ids = Arrays.asList(
                new PortfolioId("100000"),
                new PortfolioId("200000"),
                new PortfolioId("999999")
        );
        List<String> attributes = Arrays.asList("status", "ownerId", "currency", "unknown", "owner.email", "owner.unknown", "owner.id_unknown", "owner.portfolio.id");
        Request<PortfolioId> request = new Request<>(ids, attributes);

        Response<PortfolioId> response = portfolioReader.execute(request);

        System.out.println(response);
    }
}
