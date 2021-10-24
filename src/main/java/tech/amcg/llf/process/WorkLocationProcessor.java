package tech.amcg.llf.process;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.postcode.io.initializers.PostcodeLookup;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.exception.LlfException;
import tech.amcg.llf.domain.query.WorkLocation;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.LlfQuery;
import tech.amcg.llf.domain.query.Station;
import tech.amcg.llf.domain.query.Point;
import tech.amcg.llf.mapper.StationNameMapper;
import tech.amcg.llf.service.ApiRequestService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class WorkLocationProcessor {

    private final ApiRequestService apiRequestService;

    private final ObjectMapper objectMapper;

    private final StationNameMapper stationNameMapper;

    public WorkLocationProcessor(ObjectMapper objectMapper, ApiRequestService apiRequestService, StationNameMapper stationNameMapper){
        this.objectMapper = objectMapper;
        this.apiRequestService = apiRequestService;
        this.stationNameMapper = stationNameMapper;
    }

    public void findNearestStationsByWalkingDistance(LlfQuery query) throws LlfException {
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

    private int getWalkingTimeFromStation(Station station, WorkLocation distanceFrom) throws LlfException {
        String url = apiRequestService.getHereApiRequestUrl(distanceFrom.getPoint(), station.getPoint());
        String response = apiRequestService.getString(url);
        JsonNode responseAsJson;

        try {
            responseAsJson = objectMapper.readTree(response);
        } catch (JsonProcessingException ex) {
            throw new LlfException(
                    String.format("Error Reading Json Response: %s",
                            ex.getMessage()),
                    ex);
        }

        Integer walkTime = extractTimeFromJson(responseAsJson);

        if(null == walkTime) {
            throw new LlfException(String.format("Walk Time Not Found In Json Response: %s", responseAsJson.toPrettyString()));
        }

        return walkTime > 0 ? Math.round(walkTime / 60f) : walkTime;
    }

    private Integer extractTimeFromJson(JsonNode responseAsJson) {
        return Try.of(() -> responseAsJson.findParent("duration").get("duration").asInt()).getOrNull();
    }

    private void getNearestStationNames(List<Person> personList) throws LlfException {
        personList.forEach(person ->
                Try.of(() -> findNearestStations(person.getWorkLocation().getPoint()))
                .onSuccess(person::setNearestStations)
        );
        for (Person person : personList) {
            if(null == person.getNearestStations() || person.getNearestStations().size() == 0) {
                throw new LlfException(String.format("No Stations found for person: %s", person.toString()));
            }
        }
    }

    private List<Station> findNearestStations(Point latitudeAndLongitude) throws LlfException {
        String url = apiRequestService.getTransportApiRequestUrl(latitudeAndLongitude);
        String response = apiRequestService.getString(url);
        JsonNode responseAsJson;

        try {
            responseAsJson = objectMapper.readTree(response);
        } catch (JsonProcessingException ex) {
            throw new LlfException(
                    String.format("Error Reading Json Response: %s",
                            ex.getMessage()),
                    ex);
        }
        return extractStationsFromJson(responseAsJson);
    }

    private List<Station> extractStationsFromJson(JsonNode stationJsonResponse) {
        JsonNode stations = stationJsonResponse.get("stations");
        List<Station> stationList = new ArrayList<>();

        stations.forEach(
                station -> stationList.add(
                    Station.builder()
                        .name(station.get("name").asText())
                        .point(new Point(station.get("latitude").toString(), station.get("longitude").toString()))
                        .build()
                )
        );
        return stationList;
    }

    private void enrichLocationData(List<Person> personList) throws LlfException {
        for (Person person : personList) {
            //From UI, validity should be handled as user inputs postcode and prior to query being submitted
            //added validity check here for requests that don't originate from the UI, i.e. postman prototype
            if(!isValidPostcode(person.getWorkLocation().getPostcode())) {
                throw new LlfException(String.format("Invalid Postcode: %s", person.getWorkLocation().getPostcode()));
            }
        }
        personList.forEach(person ->
                Try.of(() -> getLatAndLong(person.getWorkLocation()))
                .onSuccess(latAndLong -> person.getWorkLocation().setPoint(latAndLong))
                .onFailure(Throwable::printStackTrace)
        );
    }

    public boolean isValidPostcode(String postcode) throws LlfException {
        try {
            return PostcodeLookup.isValid(postcode);
        } catch (UnirestException ex) {
            throw new LlfException(String.format("Error Validating Postcode: %s", ex.getMessage()), ex);
        }
    }

    private Point getLatAndLong(WorkLocation workLocation) throws LlfException {
        try {
            JsonNode postcodeLookup = objectMapper.readTree(PostcodeLookup.postcode(workLocation.getPostcode()).asJson().get("result").toString());
            return new Point(postcodeLookup.get("latitude").toString(), postcodeLookup.get("longitude").toString());
        } catch (Exception ex) {
            throw new LlfException(String.format("Error Looking Up Postcode: %s", ex.getMessage()), ex);
        }
    }
}
