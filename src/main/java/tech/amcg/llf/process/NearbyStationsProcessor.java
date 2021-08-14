package tech.amcg.llf.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.postcode.io.initializers.PostcodeLookup;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.exception.LLFException;
import tech.amcg.llf.domain.query.WorkLocation;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.query.Station;
import tech.amcg.llf.mapper.StationNameMapper;
import tech.amcg.llf.service.ApiRequestService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class NearbyStationsProcessor {

    private final ApiRequestService apiRequestService;

    private final ObjectMapper objectMapper;

    private final StationNameMapper stationNameMapper;

    public NearbyStationsProcessor(ObjectMapper objectMapper, ApiRequestService apiRequestService, StationNameMapper stationNameMapper){
        this.objectMapper = objectMapper;
        this.apiRequestService = apiRequestService;
        this.stationNameMapper = stationNameMapper;
    }

    public void process(Query query) throws LLFException {
        enrichLocationData(query.getPersonParamsList());
        getNearestStationNames(query.getPersonParamsList());
        getClosestStationsByWalk(query.getPersonParamsList());
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

        stationList.forEach(stationNameMapper::mapTransportApiStationNameToNeo4jStationName);

        stationList.sort(Comparator.comparingInt(Station::getWalkTime));
        return stationList;
    }

    private int getWalkingTimeFromStation(Station station, WorkLocation distanceFrom) throws LLFException {
        String url = apiRequestService.getHereApiRequestUrl(distanceFrom.getLatitude(), distanceFrom.getLongitude(), station.getLatitude(), station.getLongitude());
        String response = apiRequestService.getString(url);
        JsonNode responseAsJson;

/*        if(null == response) {
            return Integer.MAX_VALUE;
        }*/

        try {
            responseAsJson = objectMapper.readTree(response);
        } catch (JsonProcessingException ex) {
            throw new LLFException(
                    String.format("Error Reading Json Response: %s",
                            ex.getMessage()),
                    ex);
        }

        return Math.round(extractTimeFromJson(responseAsJson) / 60f);
    }

    private int extractTimeFromJson(JsonNode responseAsJson) {
        return Try.of(() -> responseAsJson.findParent("duration").get("duration").asInt()).getOrNull();
    }

    private void getNearestStationNames(List<Person> personList) throws LLFException {
        personList.forEach(person -> Try.of(() -> findNearestStations(person.getWorkLocation().getLatitude(), person.getWorkLocation().getLongitude()))
                .onSuccess(person::setNearestStations)
        );
        for (Person person : personList) {
            if(null == person.getNearestStations() || person.getNearestStations().size() == 0) {
                throw new LLFException(String.format("No Stations found for person: %s", person.toString()));
            }
        }
    }

    private List<Station> findNearestStations(String latitude, String longitude) throws LLFException {
        String url = apiRequestService.getTransportApiRequestUrl(latitude, longitude);
        String response = apiRequestService.getString(url);
        JsonNode responseAsJson;

/*        if(null == response) {
            return new ArrayList<>();
        }*/

        try {
            responseAsJson = objectMapper.readTree(response);
        } catch (JsonProcessingException ex) {
            throw new LLFException(
                    String.format("Error Reading Json Response: %s",
                            ex.getMessage()),
                    ex);
        }
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

    private void enrichLocationData(List<Person> personList) throws LLFException {
        for (Person person : personList) {
            try {
                if (!PostcodeLookup.isValid(person.getWorkLocation().getPostcode())) {
                    throw new LLFException(String.format("Invalid postcode: %s", person.getWorkLocation().getPostcode()));
                }
            } catch (UnirestException ex) {
                throw new LLFException(
                        String.format("Error Validating Postcode: %s",
                                ex.getMessage()),
                        ex);
            }
        }
        personList.forEach(person ->
                Try.of(() -> getLatAndLong(person.getWorkLocation()))
                .onSuccess(latAndLong -> person.getWorkLocation().setLatitude(latAndLong._1))
                .onSuccess(latAndLong -> person.getWorkLocation().setLongitude(latAndLong._2)));
    }

    private Tuple2<String, String> getLatAndLong(WorkLocation workLocation) throws Exception {
        JsonNode lookupResult = objectMapper.readTree(PostcodeLookup.postcode(workLocation.getPostcode()).asJson().get("result").toString());
        return Tuple.of(lookupResult.get("latitude").toString(), lookupResult.get("longitude").toString());
    }
}
