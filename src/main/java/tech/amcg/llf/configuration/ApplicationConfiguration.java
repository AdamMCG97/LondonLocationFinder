package tech.amcg.llf.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import tech.amcg.llf.mapper.TubeStepMapper;
import tech.amcg.llf.service.ApiRequestService;
import tech.amcg.llf.mapper.TubeNameMapper;


@Configuration
public class ApplicationConfiguration {

    @Bean
    public ApiRequestService requestMapper(){ return new ApiRequestService();}

    @Bean
    public TubeNameMapper tubeNameMapper() {return new TubeNameMapper();}

    @Bean
    public TubeStepMapper tubeStepMapper() {return new TubeStepMapper();}

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create();
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
