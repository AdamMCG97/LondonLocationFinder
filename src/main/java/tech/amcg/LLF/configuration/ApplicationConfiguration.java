package tech.amcg.llf.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.amcg.llf.mappers.RequestMapper;
import tech.amcg.llf.services.LocationEvaluationService;
import tech.amcg.llf.services.NearbyStationsService;
import tech.amcg.llf.services.QueryProcessorService;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public RequestMapper requestMapper(){ return new RequestMapper();}

/*@Bean(value="objectMapper")
public ObjectMapper objectMapper(){
    return new ObjectMapper();
}

@Bean(value="queryProcessorService")
public QueryProcessorService queryProcessorService() {
    return new QueryProcessorService();
}

@Bean(value="nearbyStationsService")
public NearbyStationsService nearbyStationsService(ObjectMapper objectMapper){
    return new NearbyStationsService(objectMapper);
}

@Bean(value="locationEvaluationService")
public LocationEvaluationService locationEvaluationService(){
    return new LocationEvaluationService();
}*/
}
