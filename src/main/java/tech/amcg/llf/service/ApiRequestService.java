package tech.amcg.llf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

public class ApiRequestService {

    private static final String hereApiKey = System.getenv("HERE_API_KEY");
    private static final String transportApiKey = System.getenv("TRANSPORT_API_KEY");

    @Autowired
    WebClient webClient;

    public ApiRequestService(){}

    public String getHereApiRequestUrl(String startLat, String startLong, String endLat, String endLong){
        return String.format("https://router.hereapi.com/v8/routes?transportMode=pedestrian&origin=%s,%s&destination=%s,%s&return=summary&apiKey=%s",
                startLat, startLong, endLat, endLong, hereApiKey);
    }

    public String getTransportApiRequestUrl(String lat, String lon){
        return String.format("http://transportapi.com/v3/uk/tube/stations/near.json?lat=%s&lon=%s&page=1&rpp=1&app_id=bf215419&app_key=%s",
                lat, lon, transportApiKey);
    }

    public String get(String url) {

        return webClient.method(HttpMethod.GET)
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();

    }
}
