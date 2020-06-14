package tech.amcg.llf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories("tech.amcg.llf.repository")
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }
}
