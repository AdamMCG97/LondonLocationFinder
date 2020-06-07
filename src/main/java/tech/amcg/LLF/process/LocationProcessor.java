package tech.amcg.llf.process;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.exception.LLFException;
import tech.amcg.llf.domain.response.IndividualJourney;
import tech.amcg.llf.domain.response.LLFResult;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.Response;
import tech.amcg.llf.domain.query.Station;
import tech.amcg.llf.domain.neo4j.AllStationsResult;
import tech.amcg.llf.service.Neo4JRepositoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class LocationProcessor {

    @Autowired
    Neo4JRepositoryService neo4JRepositoryService;

    LocationProcessor(){}

    public Response process(Query query) {
        query.getPersonParamsList().forEach(person -> {
            try {
                findLocationsForPerson(person);
            } catch (LLFException e) {
                throw new RuntimeException(e);
            }
        });

        return new Response(findMatchingLocations(query.getPersonParamsList()), query);
    }

    private void findLocationsForPerson(Person person) throws LLFException {
        Map<String, Double> acceptableStations = new HashMap<>();
        Station nearestStation = person.getNearestStations().get(0);

        List<AllStationsResult> results = neo4JRepositoryService.distanceToAllStations(nearestStation.getName());

        if(results.size() == 0) {
            throw new LLFException(String.format("Station Not Found on our Underground Map: %s. Identified as closest station to %s.", nearestStation.getName(), person.getWorkLocation().getPostcode()));
        }

        results.removeIf(result -> result.getDistance() + nearestStation.getWalkTime() > person.getMaximumCommuteTime());
        results.forEach(result -> acceptableStations.put(result.getDestination(), result.getDistance()));

        person.setSolutionCandidates(acceptableStations);
    }

    private List<LLFResult> findMatchingLocations(List<Person> personList) {
        List<LLFResult> matchingList = new ArrayList<>();

        for(String station : personList.get(0).getSolutionCandidates().keySet()) {
            var ref = new Object() {
                boolean optionForAllPeople = true;
            };

            personList.forEach(person -> ref.optionForAllPeople = ref.optionForAllPeople && person.getSolutionCandidates().containsKey(station));

            if(ref.optionForAllPeople) {
                matchingList.add(generateResultObject(personList, station));
            }
        }

        return matchingList;
    }

    private LLFResult generateResultObject(List<Person> personList, String stationName) {

        List<IndividualJourney> individualJourneys = new ArrayList<>();
        AtomicReference<Double> totalTravelTime = new AtomicReference<>(0d);
        AtomicReference<Double> maximumTravelTime = new AtomicReference<>(0d);

        personList.forEach(person -> {
            Double travelTime = person.getSolutionCandidates().get(stationName) + person.getNearestStations().get(0).getWalkTime();

            if(travelTime > maximumTravelTime.get()) {
                maximumTravelTime.getAndUpdate( v -> travelTime);
            }
            totalTravelTime.updateAndGet(v -> v + travelTime);
            individualJourneys.add(new IndividualJourney(person.getPersonID(), person.getWorkLocation().getPostcode(), travelTime));
        });

        Double averageTime = totalTravelTime.updateAndGet(v -> v / personList.size());

        return new LLFResult(stationName, individualJourneys, averageTime, temporaryRandomIntGenerator(), maximumTravelTime.get());
    }

    private Integer temporaryRandomIntGenerator() {
        Random random = new Random();

        return random.nextInt(7 - 1 + 1) + 1;
    }
}
