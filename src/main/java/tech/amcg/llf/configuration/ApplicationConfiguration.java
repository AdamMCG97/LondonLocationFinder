package tech.amcg.llf.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import tech.amcg.llf.mapper.QueryPathTrimmer;
import tech.amcg.llf.mapper.ResponseMapper;
import tech.amcg.llf.mapper.TubeStepMapper;
import tech.amcg.llf.service.ApiRequestService;
import tech.amcg.llf.mapper.TubeNameMapper;


@Configuration
public class ApplicationConfiguration {

    @Bean
    public ApiRequestService requestMapper() {
        return new ApiRequestService();
    }

    @Bean
    public TubeNameMapper tubeNameMapper() {
        return new TubeNameMapper();
    }

    @Bean
    public TubeStepMapper tubeStepMapper() {
        return new TubeStepMapper();
    }

    @Bean
    public ResponseMapper responseMapper() {
        return new ResponseMapper(tubeStepMapper());
    }

    @Bean
    public QueryPathTrimmer queryPathTrimmer() {
        return new QueryPathTrimmer();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }
}
