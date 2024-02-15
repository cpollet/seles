package net.cpollet.seles.demo;

import com.coxautodev.graphql.tools.SchemaParser;
import graphql.schema.GraphQLSchema;
import graphql.servlet.SimpleGraphQLHttpServlet;
import net.cpollet.seles.api.execution.Executor;
import net.cpollet.seles.client.domain.PortfolioId;
import net.cpollet.seles.demo.resolvers.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@Configuration
@ImportResource({"classpath*:context.xml"})
public class SelesDemoApplication {
    @Autowired
    @Qualifier("portfolio.executor")
    private Executor<PortfolioId> executor;

    public static void main(String[] args) {
        SpringApplication.run(SelesDemoApplication.class, args);
    }

    private static GraphQLSchema buildSchema() {
        return SchemaParser
                .newParser()
                .file("graphql/schema.graphqls")
//                .dictionary()
                .resolvers(new Query())
                .build()
                .makeExecutableSchema();
    }

    @Bean
    public ServletRegistrationBean<SimpleGraphQLHttpServlet> graphQLServlet() {
        return new ServletRegistrationBean<>(
                SimpleGraphQLHttpServlet.newBuilder(buildSchema()).build(),
                "/graphql"
        );
    }
}
