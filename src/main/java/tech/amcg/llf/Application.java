package tech.amcg.llf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import static org.springframework.core.env.AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME;

@SpringBootApplication
@EnableNeo4jRepositories("tech.amcg.llf.repository")
public class Application {
    public static void main(String[] args){

        String environmentName = System.getenv("DEPLOYMENT_ENVIRONMENT");

        if(null == environmentName) {
            environmentName = "local";
        }

        System.setProperty(ACTIVE_PROFILES_PROPERTY_NAME, environmentName);

        SpringApplication.run(Application.class, args);
    }
}
