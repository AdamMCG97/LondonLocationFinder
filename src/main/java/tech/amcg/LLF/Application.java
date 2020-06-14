package tech.amcg.llf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableNeo4jRepositories(basePackages = "tech.amcg.llf")
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
