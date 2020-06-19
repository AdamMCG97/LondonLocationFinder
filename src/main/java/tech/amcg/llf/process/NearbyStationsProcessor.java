package tech.amcg.llf.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.postcode.io.initializers.PostcodeLookup;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tech.amcg.llf.domain.exception.LLFException;
import tech.amcg.llf.domain.query.WorkLocation;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.query.Station;
import tech.amcg.llf.mapper.TubeNameMapper;
import tech.amcg.llf.service.APIRequestService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class NearbyStationsProcessor {

    private tech.amcg.llf.service.APIRequestService APIRequestService;

    private ObjectMapper objectMapper;

    private TubeNameMapper tubeNameMapper;

    public NearbyStationsProcessor(ObjectMapper objectMapper, APIRequestService APIRequestService, TubeNameMapper tubeNameMapper){
        this.objectMapper = objectMapper;
        this.APIRequestService = APIRequestService;
        this.tubeNameMapper = tubeNameMapper;
    }

    public void process(Query query) throws UnirestException, LLFException {
        enrichLocationData(query.getPersonParamsList());
        getNearestStationNames(query.getPersonParamsList());
        getClosestStationsByWalk(query.getPersonParamsList());
        query.getPersonParamsList().forEach(person -> { System.out.println(person.getWorkLocation().getPostcode()); person.getNearestStations().forEach(station -> System.out.println(station.getName()));});
    }

    private void getClosestStationsByWalk(List<Person> personList) {
        personList.forEach(person -> Try.of(() -> findClosestStationsByWalk(person))
                .onSuccess(person::setNearestStations));
    }

    private List<Station> findClosestStationsByWalk(Person person) {
        List<Station> stationList = new ArrayList<>();

        person.getNearestStations().forEach(station -> Try.of(() -> getWalkingTimeFromStation(station, person.getWorkLocation()))
                .onSuccess(station::setWalkTime)
                .onSuccess(time -> stationList.add(station)));

        stationList.forEach(station -> tubeNameMapper.map(station));

        stationList.sort(Comparator.comparingInt(Station::getWalkTime));
        return stationList;
    }

    private int getWalkingTimeFromStation(Station station, WorkLocation distanceFrom) throws JsonProcessingException {
        WebClient webClient = WebClient.create();

        String url = APIRequestService.getHereApiRequestUrl(distanceFrom.getLatitude(), distanceFrom.getLongitude(), station.getLatitude(), station.getLongitude());
        WebClient.RequestBodySpec uri = webClient.method(HttpMethod.GET).uri(url);

        String response = uri
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if(null == response) {
            return Integer.MAX_VALUE;
        }

        JsonNode responseAsJson = objectMapper.readTree(response);

        return Math.round(extractTimeFromJson(responseAsJson) / 60f);
    }

    private int extractTimeFromJson(JsonNode responseAsJson) {
        return Try.of(() -> responseAsJson.findParent("duration").get("duration").asInt()).getOrNull();
    }

    private void getNearestStationNames(List<Person> personList){
        personList.forEach(person -> Try.of(() -> findNearestStations(person.getWorkLocation().getLatitude(), person.getWorkLocation().getLongitude()))
                .onSuccess(person::setNearestStations));
    }

    private List<Station> findNearestStations(String latitude, String longitude) throws JsonProcessingException {
        WebClient webClient = WebClient.create();

        String url = APIRequestService.getTransportApiRequestUrl(latitude, longitude);
        WebClient.RequestBodySpec uri = webClient.method(HttpMethod.GET).uri(url);

        String response = uri
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if(null == response) {
            return new ArrayList<>();
        }

        JsonNode responseAsJson = objectMapper.readTree(response);
        System.out.println(responseAsJson.toPrettyString());

        return extractStationsFromJson(responseAsJson);
    }

    private List<Station> extractStationsFromJson(JsonNode stationJsonResponse) {
        JsonNode stations = stationJsonResponse.get("stations");
        List<Station> stationList = new ArrayList<>();

        stations.forEach(station -> stationList.add( new Station(station.get("name").asText(), station.get("latitude").toString(), station.get("longitude").toString(), convertAtcoToNaptan(station.get("atcocode").toString()))));

        return stationList;
    }

    private String convertAtcoToNaptan(String atco) {
        //replace second occurence of 0 with G

        int firstOccurence = atco.indexOf('0');
        String firstPart = atco.substring(0, firstOccurence+1);
        String secondPart = atco.substring(firstOccurence+1).replaceFirst("0", "G");

        return firstPart + secondPart;
    }

    private void enrichLocationData(List<Person> personList) throws UnirestException, LLFException {
        for (Person person : personList) {
            if (!PostcodeLookup.isValid(person.getWorkLocation().getPostcode())) {
                throw new LLFException(String.format("Invalid postcode: %s", person.getWorkLocation().getPostcode()));
            }
        }
        personList.forEach(person -> Try.of(() -> getLatAndLong(person.getWorkLocation()))
                .onSuccess(latAndLong -> person.getWorkLocation().setLatitude(latAndLong._1))
                .onSuccess(latAndLong -> person.getWorkLocation().setLongitude(latAndLong._2)));
    }

    private Tuple2<String, String> getLatAndLong(WorkLocation workLocation) throws Exception {
        JsonNode lookupResult = objectMapper.readTree(PostcodeLookup.postcode(workLocation.getPostcode()).asJson().get("result").toString());
        return Tuple.of(lookupResult.get("latitude").toString(), lookupResult.get("longitude").toString());
    }
}
