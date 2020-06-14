package tech.amcg.llf.configuration;

import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import tech.amcg.llf.repository.TubeRepository;
import org.neo4j.ogm.config.Configuration;

@org.springframework.context.annotation.Configuration
@ComponentScan("tech.amcg.llf")
@EnableNeo4jRepositories(basePackages="tech.amcg.llf.repository")
public class Neo4JConfiguration {

    @Bean
    public Configuration getConfiguration() {
        return new Configuration.Builder()
                .uri(System.getenv("GRAPHENEDB_URL"))
                //.credentials(System.getenv("GRAPHENEDB_BOLT_USER"), System.getenv("GRAPHENEDB_BOLT_PASSWORD"))
                .build();
    }

    @Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(sessionFactory());
    }

    @Bean
    public SessionFactory sessionFactory() {
        return new SessionFactory(getConfiguration());
    }

/*    @Bean
    public TubeRepository tubeRepository() {
        return new TubeRepository();
    }*/
}