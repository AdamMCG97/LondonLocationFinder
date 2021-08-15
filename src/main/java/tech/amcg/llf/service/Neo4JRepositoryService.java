package tech.amcg.llf.service;

import lombok.extern.slf4j.Slf4j;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.neo4j.ShortestPathResult;
import tech.amcg.llf.domain.neo4j.LegacySingleSourceShortestPathResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResultDto;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;
import tech.amcg.llf.mapper.Neo4JResponseMapper;
import tech.amcg.llf.repository.TubeRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@NoArgsConstructor
public class Neo4JRepositoryService {

    @Autowired
    private TubeRepository tubeRepository;

    private final Neo4JResponseMapper neo4JResponseMapper = new Neo4JResponseMapper();

    @Deprecated
    public List<LegacySingleSourceShortestPathResult> distanceToAllStations(String stationName){
        log.debug(String.format("Received query for legacy distanceToAllStations from station: %s", stationName));
        return tubeRepository.distanceToAllStations(stationName);
    }

    public List<SingleSourceShortestPathResult> dijkstraDistanceToAllStations(String stationName){
        log.debug(String.format("Received query for distanceToAllStations from station: %s", stationName));
        List<SingleSourceShortestPathResult> mappedResults = new ArrayList<>();
        List<SingleSourceShortestPathResultDto> results = tubeRepository.dijkstraDistanceToAllStations(stationName);
        for(SingleSourceShortestPathResultDto result : results) {
            mappedResults.add(neo4JResponseMapper.mapFromDtoToSingleSourceShortestPathResult(result));
        }
        log.debug(String.format("Returned %s results for allStations query for: %s", mappedResults.size(), stationName));
        return mappedResults;
    }


    public List<SingleSourceShortestPathResult> dijkstraDistanceToAllStations(String stationName, List<Double> zones, Integer maxTime){
        log.debug(String.format("Received query for distanceToAllStations from station: %s", stationName));
        List<SingleSourceShortestPathResult> mappedResults = new ArrayList<>();
        List<SingleSourceShortestPathResultDto> results = tubeRepository.dijkstraDistanceToAllStations(stationName, zones, maxTime);
        for(SingleSourceShortestPathResultDto result : results) {
            mappedResults.add(neo4JResponseMapper.mapFromDtoToSingleSourceShortestPathResult(result));
        }
        log.debug(String.format("Returned %s results for allStations query for: %s", mappedResults.size(), stationName));
        return mappedResults;
    }

    public List<ShortestPathResult> dijkstraDetailedJourneyBetween(String firstStation, String secondStation) {
        log.debug(String.format("Received query for detailedJourneyBetween. start: %s & end: %s", firstStation, secondStation));
        return tubeRepository.dijkstraDetailedJourneyBetween(firstStation, secondStation);
    }

}
