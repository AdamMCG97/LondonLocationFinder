package tech.amcg.llf.process;

import lombok.NoArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.amcg.llf.domain.exception.LLFException;
import tech.amcg.llf.domain.neo4j.ShortestPathResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;
import tech.amcg.llf.domain.response.IndividualJourney;
import tech.amcg.llf.domain.response.LLFResult;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.Response;
import tech.amcg.llf.domain.query.Station;
import tech.amcg.llf.domain.response.mapping.JourneyDetails;
import tech.amcg.llf.domain.response.mapping.JourneyStep;
import tech.amcg.llf.domain.response.mapping.SpecificWalkStep;
import tech.amcg.llf.domain.response.mapping.VariableWalkStep;
import tech.amcg.llf.mapper.TubeStepMapper;
import tech.amcg.llf.service.Neo4JRepositoryService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
public class LocationProcessor {

    @Autowired
    Neo4JRepositoryService neo4JRepositoryService;

    @Autowired
    TubeStepMapper tubeStepMapper;

    public Response process(Query query) {
        query.getPersonParamsList().forEach(person -> {
            try {
                findLocationsForPerson(person, query.getExclusionZones());
            } catch (LLFException e) {
                throw new RuntimeException(e);
            }
        });

        List<LLFResult> matchingLocationsResults = findMatchingLocations(query.getPersonParamsList());

        return new Response(matchingLocationsResults, query);
    }

    private void findLocationsForPerson(Person person, List<Double> exclusionZones) throws LLFException {
        Station nearestStation = person.getNearestStations().get(0);

        List<SingleSourceShortestPathResult> results = neo4JRepositoryService.dijkstraDistanceToAllStations(nearestStation.getName());

        if(results.size() == 0) {
            throw new LLFException(String.format("Station Not Found on our Underground Map: %s. Identified as closest station to %s.", nearestStation.getName(), person.getWorkLocation().getPostcode()));
        }

        results.removeIf(result -> result.getTotalCost() + nearestStation.getWalkTime() > person.getMaximumCommuteTime());
        results.removeIf(result -> exclusionZones.contains(result.getZone()));

        person.setSolutionCandidates(results);
    }

    //unfortunate requirement to query twice due to limitations on properties that can be returned by neo4j query
    private Double acceptableTravelTimeWithLineChanges(String source, String target) {
        ShortestPathResult result = neo4JRepositoryService.dijkstraDetailedJourneyByTubeLine(source, target).get(0);
        Iterator<Double> journeyCostIterator = result.getCosts().iterator();
        Double currentLineId = journeyCostIterator.next();
        Double nextStepLineId;
        Double cumulativeLineIds = currentLineId;
        Double totalTravelTime = result.getTotalCost();
        while(journeyCostIterator.hasNext()) {
            nextStepLineId = journeyCostIterator.next() - cumulativeLineIds;
            if(!nextStepLineId.equals(currentLineId) && !currentLineId.equals(0d)) {
                totalTravelTime += 3;
            }
            cumulativeLineIds += nextStepLineId;
            currentLineId = nextStepLineId;
        }
        return totalTravelTime;
    }

    private List<LLFResult> findMatchingLocations(List<Person> personList) {
        List<LLFResult> matchingList = new ArrayList<>();

        List<String> firstPersonStationsList = personList.get(0).getSolutionCandidates()
                .stream().map(SingleSourceShortestPathResult::getTargetNodeName).collect(Collectors.toList());

        for(String station : firstPersonStationsList) {
            var ref = new Object() {
                boolean optionForAllPeople = true;
            };

            personList.forEach(person -> {
                SingleSourceShortestPathResult journey = findElementInListByString(person.getSolutionCandidates(), station);
                ref.optionForAllPeople = ref.optionForAllPeople
                        && null != journey;
            });

            if(ref.optionForAllPeople) {
                personList.forEach(person -> {
                    SingleSourceShortestPathResult journey = findElementInListByString(person.getSolutionCandidates(), station);
                    Double actualTravelTime = acceptableTravelTimeWithLineChanges(journey.getSourceNodeName(), journey.getTargetNodeName());
                    ref.optionForAllPeople = ref.optionForAllPeople && person.getMaximumCommuteTime() > actualTravelTime;
                    SingleSourceShortestPathResult updatedJourney = SingleSourceShortestPathResult.builder()
                            .index(journey.getIndex())
                            .costs(journey.getCosts())
                            .nodeNames(journey.getNodeNames())
                            .sourceNodeName(journey.getSourceNodeName())
                            .targetNodeName(journey.getTargetNodeName())
                            .totalCost(actualTravelTime)
                            .zone(journey.getZone())
                            .build();

                    person.updateSolutionCandidate(journey, updatedJourney);
                });
            }

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
            Double travelTime = findElementInListByString(person.getSolutionCandidates(), stationName).getTotalCost() + person.getNearestStations().get(0).getWalkTime();

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

    private SingleSourceShortestPathResult findElementInListByString(List<SingleSourceShortestPathResult> list, String itemToFind) {
        return IterableUtils.find(list,
                singleSourceShortestPathResult -> itemToFind.equals(singleSourceShortestPathResult.getTargetNodeName())
        );
    }

    private JourneyDetails getDetailedJourney(Person person, String candidateStationName, int travelTime) {
        List<JourneyStep> resultSteps = new ArrayList<>();
        //add walk step between work location and closest station
        resultSteps.add(new SpecificWalkStep(person.getWorkLocation().getPostcode(), person.getNearestStations().get(0).getName(), person.getNearestStations().get(0).getWalkTime()));
        List<ShortestPathResult> detailedTubeJourney = neo4JRepositoryService.dijkstraDetailedJourneyByTubeLine(person.getNearestStations().get(0).getName(), candidateStationName);
        //add all the tube stops between work station and candidate station
        resultSteps.addAll(tubeStepMapper.map(detailedTubeJourney.get(0), travelTime));
        //add generic walk step from candidate station to anywhere within commute limit
        resultSteps.add(new VariableWalkStep(candidateStationName, person.getMaximumCommuteTime() - travelTime));
        return new JourneyDetails(resultSteps);
    }

}
