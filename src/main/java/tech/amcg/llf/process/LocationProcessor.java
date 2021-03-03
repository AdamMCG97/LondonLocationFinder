package tech.amcg.llf.process;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.amcg.llf.domain.exception.LLFException;
import tech.amcg.llf.domain.neo4j.ShortestPathResult;
import tech.amcg.llf.domain.response.IndividualJourney;
import tech.amcg.llf.domain.response.LLFResult;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.Response;
import tech.amcg.llf.domain.query.Station;
import tech.amcg.llf.domain.neo4j.LegacySingleSourceShortestPathResult;
import tech.amcg.llf.domain.response.mapping.JourneyDetails;
import tech.amcg.llf.domain.response.mapping.JourneyStep;
import tech.amcg.llf.domain.response.mapping.SpecificWalkStep;
import tech.amcg.llf.domain.response.mapping.VariableWalkStep;
import tech.amcg.llf.mapper.TubeStepMapper;
import tech.amcg.llf.service.Neo4JRepositoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class LocationProcessor {

    @Autowired
    Neo4JRepositoryService neo4JRepositoryService;

    @Autowired
    TubeStepMapper tubeStepMapper;

    LocationProcessor(){}

    public Response process(Query query) {
        query.getPersonParamsList().forEach(person -> {
            try {
                findLocationsForPerson(person, query.getExclusionZones());
            } catch (LLFException e) {
                throw new RuntimeException(e);
            }
        });

        return new Response(findMatchingLocations(query.getPersonParamsList()), query);
    }

    private void findLocationsForPerson(Person person, List<Double> exclusionZones) throws LLFException {
        Station nearestStation = person.getNearestStations().get(0);

        List<LegacySingleSourceShortestPathResult> results = neo4JRepositoryService.distanceToAllStations(nearestStation.getName());

        if(results.size() == 0) {
            throw new LLFException(String.format("Station Not Found on our Underground Map: %s. Identified as closest station to %s.", nearestStation.getName(), person.getWorkLocation().getPostcode()));
        }

        results.removeIf(result -> result.getDistance() + nearestStation.getWalkTime() > person.getMaximumCommuteTime());
        results.removeIf(result -> exclusionZones.contains(result.getZone()));

        person.setSolutionCandidates(results);
    }

    private List<LLFResult> findMatchingLocations(List<Person> personList) {
        List<LLFResult> matchingList = new ArrayList<>();

        List<String> firstPersonStationsList = personList.get(0).getSolutionCandidates()
                .stream().map(LegacySingleSourceShortestPathResult::getDestination).collect(Collectors.toList());

        for(String station : firstPersonStationsList) {
            var ref = new Object() {
                boolean optionForAllPeople = true;
            };

            personList.forEach(person -> ref.optionForAllPeople = ref.optionForAllPeople
                    && !(null == (findElementInListByString(person.getSolutionCandidates(), station))));

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
            Double travelTime = findElementInListByString(person.getSolutionCandidates(), stationName).getDistance() + person.getNearestStations().get(0).getWalkTime();

            if(travelTime > maximumTravelTime.get()) {
                maximumTravelTime.getAndUpdate( v -> travelTime);
            }
            totalTravelTime.updateAndGet(v -> v + travelTime);
            JourneyDetails journeyDetails = getDetailedJourney(person, stationName, travelTime.intValue());
            individualJourneys.add(new IndividualJourney(person.getPersonID(), person.getWorkLocation().getPostcode(), travelTime, journeyDetails));
        });

        Double averageTime = totalTravelTime.updateAndGet(v -> v / personList.size());
        Double zone = findElementInListByString(personList.get(0).getSolutionCandidates(), stationName).getZone();

        return new LLFResult(stationName, individualJourneys, averageTime, zone, maximumTravelTime.get());
    }

    private LegacySingleSourceShortestPathResult findElementInListByString(List<LegacySingleSourceShortestPathResult> list, String itemToFind) {
        return IterableUtils.find(list,
                singleSourceShortestPathResult -> itemToFind.equals(singleSourceShortestPathResult.getDestination())
        );
    }

    private JourneyDetails getDetailedJourney(Person person, String candidateStationName, int travelTime) {
        List<JourneyStep> resultSteps = new ArrayList<>();
        //add walk step between work location and closest station
        resultSteps.add(new SpecificWalkStep(person.getWorkLocation().getPostcode(), person.getNearestStations().get(0).getName(), person.getNearestStations().get(0).getWalkTime()));
        List<ShortestPathResult> detailedTubeJourney = neo4JRepositoryService.dijkstraDetailedJourneyBetween(person.getNearestStations().get(0).getName(), candidateStationName);
        //add all the tube stops between work station and candidate station
        resultSteps.addAll(tubeStepMapper.map(detailedTubeJourney.get(0)));
        //add generic walk step from candidate station to anywhere within commute limit
        resultSteps.add(new VariableWalkStep(candidateStationName, person.getMaximumCommuteTime() - travelTime));
        return new JourneyDetails(resultSteps);
    }

}
