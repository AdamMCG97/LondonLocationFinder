package tech.amcg.llf.process;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.amcg.llf.domain.exception.LLFException;
import tech.amcg.llf.domain.neo4j.LineDataResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.query.Station;
import tech.amcg.llf.mapper.QueryPathTrimmer;
import tech.amcg.llf.mapper.ResultsMapper;
import tech.amcg.llf.service.Neo4JRepositoryService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
@Slf4j
public class TubeMapProcessor {

    @Autowired
    Neo4JRepositoryService neo4JRepositoryService;

    @Autowired
    ResultsMapper resultsMapper;

    @Autowired
    QueryPathTrimmer queryPathTrimmer;

    public static final Double STANDARD_TIME_PER_LINE_CHANGE = 3d;

    public void findAllAcceptablePaths(Query query) {
        query.getPersonParamsList().forEach(person -> {
            try {
                findLocationsForPerson(person, query.getExclusionZones());
            } catch (LLFException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void findLocationsForPerson(Person person, List<Double> exclusionZones) throws LLFException {
        Station nearestStation = person.getNearestStations().get(0);

        List<SingleSourceShortestPathResult> shortestPathToAllStations = neo4JRepositoryService.dijkstraDistanceToAllStations(nearestStation.getName(), exclusionZones, person.getMaximumCommuteTime() - nearestStation.getWalkTime());

        if(shortestPathToAllStations.size() == 0) {
            throw new LLFException(
                    String.format("No Stations Found Within Specified Commute Distance For Person: %s. Closest station to %s identified as %s.",
                            person.getPersonID(),
                            person.getWorkLocation().getPostcode(),
                            nearestStation.getName())
            );
        }

        List<SingleSourceShortestPathResult> trimmedResults = shortestPathToAllStations.stream().map(path -> queryPathTrimmer.trim(path)).collect(Collectors.toList());

        removePathsExceedingQueryLimits(trimmedResults, person, nearestStation);

        if(trimmedResults.size() == 0) {
            throw new LLFException(String.format("No suitable paths found for nearest station: %s. Identified as closest station to %s.", nearestStation.getName(), person.getWorkLocation().getPostcode()));
        }

        person.setAcceptablePaths(trimmedResults);
    }

    private void removePathsExceedingQueryLimits(List<SingleSourceShortestPathResult> trimmedResults, Person person, Station nearestStation) {
        List<SingleSourceShortestPathResult> unacceptableSolutions = new ArrayList<>();

        trimmedResults.forEach( result -> {
            Long lineChanges = calculateLineChanges(result.getLineData());
            if(result.getTotalCost() + (lineChanges * STANDARD_TIME_PER_LINE_CHANGE) > person.getMaximumCommuteTime() - nearestStation.getWalkTime()) {
                unacceptableSolutions.add(result);
            }
            else {
                result.setTotalCost(result.getTotalCost() + (lineChanges * STANDARD_TIME_PER_LINE_CHANGE));
            }
        });

        trimmedResults.removeAll(unacceptableSolutions);

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

}
