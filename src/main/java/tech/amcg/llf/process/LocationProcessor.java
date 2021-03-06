package tech.amcg.llf.process;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.amcg.llf.domain.exception.LLFException;
import tech.amcg.llf.domain.neo4j.LineDataResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;
import tech.amcg.llf.domain.response.LLFResult;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.query.Station;
import tech.amcg.llf.mapper.QueryPathTrimmer;
import tech.amcg.llf.mapper.ResponseMapper;
import tech.amcg.llf.service.Neo4JRepositoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
@Slf4j
public class LocationProcessor {

    @Autowired
    Neo4JRepositoryService neo4JRepositoryService;

    @Autowired
    ResponseMapper responseMapper;

    @Autowired
    QueryPathTrimmer queryPathTrimmer;

    public static final Double STANDARD_TIME_PER_LINE_CHANGE = 3d;

    public List<LLFResult> process(Query query) {
        query.getPersonParamsList().forEach(person -> {
            try {
                findLocationsForPerson(person, query.getExclusionZones());
            } catch (LLFException e) {
                throw new RuntimeException(e);
            }
        });

        return findMatchingLocations(query.getPersonParamsList());
    }

    private void findLocationsForPerson(Person person, List<Double> exclusionZones) throws LLFException {
        Station nearestStation = person.getNearestStations().get(0);

        List<SingleSourceShortestPathResult> results = neo4JRepositoryService.dijkstraDistanceToAllStations(nearestStation.getName(), exclusionZones, person.getMaximumCommuteTime() - nearestStation.getWalkTime());

        if(results.size() == 0) {
            throw new LLFException(String.format("Station Not Found on our Underground Map: %s. Identified as closest station to %s.", nearestStation.getName(), person.getWorkLocation().getPostcode()));
        }

        List<SingleSourceShortestPathResult> trimmedResults = results.stream().map(path -> queryPathTrimmer.trim(path)).collect(Collectors.toList());

        List<SingleSourceShortestPathResult> unacceptableSolutions = new ArrayList<>();

        trimmedResults.forEach(result -> {
            Long lineChanges = calculateLineChanges(result.getLineData());
            if(result.getTotalCost() + (lineChanges * STANDARD_TIME_PER_LINE_CHANGE) > person.getMaximumCommuteTime() - nearestStation.getWalkTime()) {
                unacceptableSolutions.add(result);
            }
            else {
                result.setTotalCost(result.getTotalCost() + (lineChanges * STANDARD_TIME_PER_LINE_CHANGE));
            }
        });

        trimmedResults.removeAll(unacceptableSolutions);

        if(trimmedResults.size() == 0) {
            throw new LLFException(String.format("No suitable paths found for nearest station: %s. Identified as closest station to %s.", nearestStation.getName(), person.getWorkLocation().getPostcode()));
        }

        person.setSolutionCandidates(trimmedResults);
    }

    private Long calculateLineChanges(List<LineDataResult> lineData) {
        Long lineChanges = 0L;
        Long previousLine = 0L;
        for(LineDataResult step : lineData) {
            if(!step.getLine().equals(previousLine) && !previousLine.equals(0L)) {
                lineChanges++;
            }
            previousLine = step.getLine();
        }
        return lineChanges;
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
                matchingList.add(responseMapper.mapResult(personList, station));
            }
        }

        return matchingList;
    }

    public static SingleSourceShortestPathResult findElementInListByString(List<SingleSourceShortestPathResult> list, String itemToFind) {
        return IterableUtils.find(list,
                singleSourceShortestPathResult -> itemToFind.equals(singleSourceShortestPathResult.getTargetNodeName())
        );
    }
}
