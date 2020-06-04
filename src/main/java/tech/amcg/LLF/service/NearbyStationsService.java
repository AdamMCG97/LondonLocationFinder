package tech.amcg.llf.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.postcode.io.initializers.PostcodeLookup;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tech.amcg.llf.mapper.RequestMapper;
import tech.amcg.llf.domain.WorkLocation;
import tech.amcg.llf.domain.Person;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.Station;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class NearbyStationsService {

    private RequestMapper requestMapper;

    private ObjectMapper objectMapper;

    public NearbyStationsService(ObjectMapper objectMapper, RequestMapper requestMapper){
        this.objectMapper = objectMapper;
        this.requestMapper = requestMapper;
    }

    public void process(Query query) {
        enrichLocationData(query.getPersonParamsList());
        getNearestStationNames(query.getPersonParamsList());
        getClosestStationsByWalk(query.getPersonParamsList());
        query.getPersonParamsList().forEach(person -> { System.out.println(person.getWorkLocation().getPostcode()); person.getNearestStations().forEach(station -> {System.out.println(station.getName());});});
    }

    private void getClosestStationsByWalk(List<Person> personList) {
        personList.forEach(person -> {
            Try.of(() -> findClosestStationsByWalk(person))
                    .onSuccess(person::setNearestStations);
        });
    }

    private List<Station> findClosestStationsByWalk(Person person) {
        List<Station> stationList = new ArrayList<>();

        person.getNearestStations().forEach(station -> {
            Try.of(() -> getWalkingTimeFromStation(station, person.getWorkLocation()))
                    .onSuccess(station::setWalkTime)
                    .onSuccess(time -> stationList.add(station));
        });

        stationList.sort(Comparator.comparingInt(Station::getWalkTime));
        return stationList;
    }

    private int getWalkingTimeFromStation(Station station, WorkLocation distanceFrom) throws JsonProcessingException {
        WebClient webClient = WebClient.create();
        WebClient.UriSpec<WebClient.RequestBodySpec> request = webClient.method(HttpMethod.GET);
        String url = requestMapper.getHereApiRequestUrl(distanceFrom.getLatitude(), distanceFrom.getLongitude(), station.getLatitude(), station.getLongitude());
        WebClient.RequestBodySpec uri = webClient.method(HttpMethod.GET).uri(url);

        String response = uri
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode responseAsJson = objectMapper.readTree(response);
       // System.out.println(responseAsJson.toPrettyString());

        return extractTimeFromJson(responseAsJson);
    }

    private int extractTimeFromJson(JsonNode responseAsJson) {
        return Try.of(() -> responseAsJson.findParent("duration").get("duration").asInt()).getOrNull();
    }

    private void getNearestStationNames(List<Person> personList){
        personList.forEach(person -> {
            Try.of(() -> findNearestStations(person.getWorkLocation().getLatitude(), person.getWorkLocation().getLongitude()))
                    .onSuccess(person::setNearestStations);
        });
    }

    private List<Station> findNearestStations(String latitude, String longitude) throws JsonProcessingException {
        WebClient webClient = WebClient.create();

        WebClient.UriSpec<WebClient.RequestBodySpec> request = webClient.method(HttpMethod.GET);
        String url = requestMapper.getTransportApiRequestUrl(latitude, longitude);
        WebClient.RequestBodySpec uri = webClient.method(HttpMethod.GET).uri(url);

        String response = uri
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode responseAsJson = objectMapper.readTree(response);
        System.out.println(responseAsJson.toPrettyString());

        return extractStationsFromJson(responseAsJson);
    }

    private List<Station> extractStationsFromJson(JsonNode stationJsonResponse) {
        JsonNode stations = stationJsonResponse.get("stations");
        List<Station> stationList = new ArrayList<>();

        stations.forEach(station -> {
            stationList.add( new Station(station.get("name").toString(), station.get("latitude").toString(), station.get("longitude").toString(), convertAtcoToNaptan(station.get("atcocode").toString())));
        });

        return stationList;
    }

    private String convertAtcoToNaptan(String atco) {
        //replace second occurence of 0 with G

        int firstOccurence = atco.indexOf('0');
        String firstPart = atco.substring(0, firstOccurence+1);
        String secondPart = atco.substring(firstOccurence+1).replaceFirst("0", "G");

        return firstPart + secondPart;
    }

    private void enrichLocationData(List<Person> personList) {
        personList.forEach(person -> {
            Try.of(() -> getLatAndLong(person.getWorkLocation()))
                    .onSuccess(latAndLong -> person.getWorkLocation().setLatitude(latAndLong._1))
                    .onSuccess(latAndLong -> person.getWorkLocation().setLongitude(latAndLong._2));
        });
    }

    private Tuple2<String, String> getLatAndLong(WorkLocation workLocation) throws Exception {
        JsonNode lookupResult = objectMapper.readTree(PostcodeLookup.postcode(workLocation.getPostcode()).asJson().get("result").toString());
        return Tuple.of(lookupResult.get("latitude").toString(), lookupResult.get("longitude").toString());
        //System.out.println(lookupResult);
        //System.out.println(String.format("Latitude: {%s}, Longitude: {%s}", lookupResult.get("latitude"), lookupResult.get("longitude")));
    }
}
