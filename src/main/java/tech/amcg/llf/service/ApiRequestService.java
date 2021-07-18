package tech.amcg.llf.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@NoArgsConstructor
public class ApiRequestService {

    @Value("${llf.api.here.key}")
    String hereApiKey;

    @Value("${llf.api.transport.key}")
    String transportApiKey;

    @Value("${llf.api.here.uri}")
    String hereApiBaseUri;

    @Value("${llf.api.transport.uri}")
    String transportApiBaseUri;

    @Autowired
    WebClient webClient;

    private static final String transportApiPath = "/uk/tube/stations/near.json";

    private static final String hereApiPath = "/routes";

    public String getHereApiRequestUrl(String startLat, String startLong, String endLat, String endLong) {
        return String.format("%s%s?transportMode=pedestrian&origin=%s,%s&destination=%s,%s&return=summary&apiKey=%s",
                hereApiBaseUri, hereApiPath, startLat, startLong, endLat, endLong, hereApiKey);
    }

    public String getTransportApiRequestUrl(String lat, String lon) {
        return String.format("%s%s?lat=%s&lon=%s&page=1&rpp=1&app_id=bf215419&app_key=%s",
                transportApiBaseUri, transportApiPath, lat, lon, transportApiKey);
    }

    public String getString(String url) {

        log.debug(String.format("Making GET request to url: %s", url));

        return webClient.method(HttpMethod.GET)
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }
}
