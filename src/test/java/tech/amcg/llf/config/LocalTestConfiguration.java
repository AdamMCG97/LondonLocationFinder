package tech.amcg.llf.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class LocalTestConfiguration {

    @Value("${test.wiremock.port:6000}")
    int wireMockPort;

    @Bean
    public WireMockServer getWireMockServer() {
        return new WireMockServer(wireMockPort);
    }

}
