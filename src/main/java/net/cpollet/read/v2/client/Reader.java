package net.cpollet.read.v2.client;

import net.cpollet.read.v2.api.domain.Request;
import net.cpollet.read.v2.api.domain.Response;
import net.cpollet.read.v2.client.domain.PortfolioId;
import net.cpollet.read.v2.impl.ReadImpl;

import java.util.Arrays;
import java.util.List;

public class Reader {
    public static void main(String[] args) {
        PortfolioAttributeStore.INSTANCE.init();
        PersonAttributeStore.INSTANCE.init();

        ReadImpl<PortfolioId> read = new ReadImpl<>(PortfolioAttributeStore.INSTANCE);

        List<PortfolioId> ids = Arrays.asList(
                new PortfolioId("100000"),
                new PortfolioId("200000"),
                new PortfolioId("999999")
        );
        List<String> attributes = Arrays.asList("status", "ownerId", "currency", "unknown", "owner.email", "owner.id", "owner.portfolio.id");
        Request<PortfolioId> request = new Request<>(ids, attributes);

        Response<PortfolioId> response = read.execute(request);

        System.out.println(response);
    }
}
