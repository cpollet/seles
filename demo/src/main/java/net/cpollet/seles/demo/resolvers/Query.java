package net.cpollet.seles.demo.resolvers;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import java.util.Collections;
import java.util.List;

public class Query implements GraphQLQueryResolver {
    public List<Portfolio> portfolios() {
        Portfolio portfolio = new Portfolio();
        portfolio.put("id", "456123");
        return Collections.singletonList(
                portfolio
        );
    }
}
