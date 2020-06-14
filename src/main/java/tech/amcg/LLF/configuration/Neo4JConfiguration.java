package tech.amcg.llf.configuration;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import tech.amcg.llf.repository.TubeRepository;

@Configuration
@EnableNeo4jRepositories(basePackages="tech.amcg.llf")
public class Neo4JConfiguration {

    @Bean
    public org.neo4j.ogm.config.Configuration getConfiguration() {
        return new org.neo4j.ogm.config.Configuration.Builder()
                .uri(System.getenv("GRAPHENEDB_BOLT_URL"))
                .credentials(System.getenv("GRAPHENEDB_BOLT_USER"), System.getenv("GRAPHENEDB_BOLT_PASSWORD"))
                .build();
    }

    @Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(sessionFactory());
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new SessionFactory(new org.neo4j.ogm.config.Configuration.Builder()
                .uri(System.getenv("GRAPHENEDB_BOLT_URL"))
                .credentials(System.getenv("GRAPHENEDB_BOLT_USER"), System.getenv("GRAPHENEDB_BOLT_PASSWORD"))
                .build(), "tech.amcg.llf");
    }

/*    @Bean
    public TubeRepository tubeRepository() {
        return new TubeRepository();
    }*/
}