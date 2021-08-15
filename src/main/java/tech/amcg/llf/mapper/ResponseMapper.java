package tech.amcg.llf.mapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
import tech.amcg.llf.process.ResultsProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Slf4j
public class ResponseMapper {

    @NonNull
    private final TubeStepMapper tubeStepMapper;

    private List<IndividualJourney> individualJourneys;
    private AtomicReference<Double> totalTravelTime;
    private AtomicReference<Double> maximumTravelTime;

    public LLFResult mapResult(List<Person> personList, String stationName) {

        individualJourneys = new ArrayList<>();
        totalTravelTime = new AtomicReference<>(0d);
        maximumTravelTime = new AtomicReference<>(0d);

        personList.forEach( person -> {
            IndividualJourney journeyForPerson = buildJourneyForPerson(person, stationName);
            individualJourneys.add(journeyForPerson);
        });

        Double averageTime = totalTravelTime.updateAndGet(v -> v / personList.size());
        Double zone = ResultsProcessor.findElementInListByString(personList.get(0).getAcceptablePaths(), stationName).getZone();

        return LLFResult.builder()
                .name(stationName)
                .individualJourneys(individualJourneys)
                .averageTravelTime(averageTime)
                .maximumTravelTime(maximumTravelTime.get())
                .zone(zone)
                .build();
    }

    private IndividualJourney buildJourneyForPerson(Person person, String stationName) {
        SingleSourceShortestPathResult journey = ResultsProcessor.findElementInListByString(person.getAcceptablePaths(), stationName);
        Double travelTime = journey.getTotalCost() + person.getNearestStations().get(0).getWalkTime();

        if(travelTime > maximumTravelTime.get()) {
            maximumTravelTime.getAndUpdate( v -> travelTime);
        }
        totalTravelTime.updateAndGet(v -> v + travelTime);
        JourneyDetails journeyDetails = getDetailedJourney(person, journey, travelTime.intValue());

        return IndividualJourney.builder()
                .personID(person.getPersonID())
                .workPostcode(person.getWorkLocation().getPostcode())
                .travelTime(travelTime)
                .journeyDetails(journeyDetails)
                .build();
    }

    private JourneyDetails getDetailedJourney(Person person, SingleSourceShortestPathResult journey, int travelTime) {
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
