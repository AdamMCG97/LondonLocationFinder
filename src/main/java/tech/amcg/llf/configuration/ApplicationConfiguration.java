package tech.amcg.llf.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.amcg.llf.mapper.TubeStepMapper;
import tech.amcg.llf.service.APIRequestService;
import tech.amcg.llf.mapper.TubeNameMapper;


@Configuration
public class ApplicationConfiguration {

    @Bean
    public APIRequestService requestMapper(){ return new APIRequestService();}

    @Bean
    public TubeNameMapper tubeNameMapper() {return new TubeNameMapper();}

    @Bean
    public TubeStepMapper tubeStepMapper() {return new TubeStepMapper();}

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
/*
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
