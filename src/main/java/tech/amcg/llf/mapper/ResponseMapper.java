package tech.amcg.llf.mapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.amcg.llf.domain.exception.LLFException;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.response.IndividualJourney;
import tech.amcg.llf.domain.response.LLFResult;
import tech.amcg.llf.domain.response.mapping.JourneyDetails;
import tech.amcg.llf.domain.response.mapping.JourneyStep;
import tech.amcg.llf.domain.response.mapping.SpecificWalkStep;
import tech.amcg.llf.domain.response.mapping.VariableWalkStep;
import tech.amcg.llf.process.LocationProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@AllArgsConstructor
@Slf4j
public class ResponseMapper {

    private final TubeStepMapper tubeStepMapper;

    public LLFResult mapResult(List<Person> personList, String stationName) {

        List<IndividualJourney> individualJourneys = new ArrayList<>();
        AtomicReference<Double> totalTravelTime = new AtomicReference<>(0d);
        AtomicReference<Double> maximumTravelTime = new AtomicReference<>(0d);

        personList.forEach(person -> {
            SingleSourceShortestPathResult journey = LocationProcessor.findElementInListByString(person.getSolutionCandidates(), stationName);
            Double travelTime = journey.getTotalCost() + person.getNearestStations().get(0).getWalkTime();

            if(travelTime > maximumTravelTime.get()) {
                maximumTravelTime.getAndUpdate( v -> travelTime);
            }
            totalTravelTime.updateAndGet(v -> v + travelTime);
            JourneyDetails journeyDetails = null;
            try {
                journeyDetails = getDetailedJourney(person, journey, travelTime.intValue());
            } catch (LLFException e) {
                log.error("Getting journey details failed.");
                e.printStackTrace();
            }
            individualJourneys.add(new IndividualJourney(person.getPersonID(), person.getWorkLocation().getPostcode(), travelTime, journeyDetails));
        });
        Double averageTime = totalTravelTime.updateAndGet(v -> v / personList.size());
        Double zone = LocationProcessor.findElementInListByString(personList.get(0).getSolutionCandidates(), stationName).getZone();

        return new LLFResult(stationName, individualJourneys, averageTime, zone, maximumTravelTime.get());
    }

    private JourneyDetails getDetailedJourney(Person person, SingleSourceShortestPathResult journey, int travelTime) throws LLFException {
        List<JourneyStep> resultSteps = new ArrayList<>();
        //add walk step between work location and closest station
        resultSteps.add(new SpecificWalkStep(person.getWorkLocation().getPostcode(), person.getNearestStations().get(0).getName(), person.getNearestStations().get(0).getWalkTime()));
        //add all the tube stops between work station and candidate station
        resultSteps.addAll(tubeStepMapper.map(journey));
        //add generic walk step from candidate station to anywhere within commute limit
        resultSteps.add(new VariableWalkStep(journey.getTargetNodeName(), person.getMaximumCommuteTime() - travelTime));
        return new JourneyDetails(resultSteps);
    }

}
