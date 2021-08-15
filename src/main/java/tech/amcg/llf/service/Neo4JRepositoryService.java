package tech.amcg.llf.service;

import lombok.extern.slf4j.Slf4j;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.neo4j.LineDataResult;
import tech.amcg.llf.domain.neo4j.ShortestPathResult;
import tech.amcg.llf.domain.neo4j.LegacySingleSourceShortestPathResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResultDto;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;
import tech.amcg.llf.repository.TubeRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
@NoArgsConstructor
public class Neo4JRepositoryService {

    @Autowired
    private TubeRepository tubeRepository;

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
            mappedResults.add(mapFromDtoToSingleSourceShortestPathResult(result));
        }
        log.debug(String.format("Returned %s results for allStations query for: %s", mappedResults.size(), stationName));
        return mappedResults;
    }

    public SingleSourceShortestPathResult mapFromDtoToSingleSourceShortestPathResult(SingleSourceShortestPathResultDto dto) {
        return SingleSourceShortestPathResult.builder()
                .index(dto.getIndex())
                .costs(dto.getCosts())
                .lineData(mapLineData(dto.getLineData().iterator()))
                .nodeNames(dto.getNodeNames())
                .sourceNodeName(dto.getSourceNodeName())
                .targetNodeName(dto.getTargetNodeName())
                .totalCost(dto.getTotalCost())
                .zone(dto.getZone())
                .build();
    }

    //very odd behaviour, lineData behaves as an Iterable<Object> but won't load as anything other than an Iterable of a list type
    public List<LineDataResult> mapLineData(Iterator<List<Object>> iterator) {
        Object line, time, startNodeName, endNodeName;
        List<LineDataResult> mappedResults = new ArrayList<>();
        while(iterator.hasNext()) {
            line = iterator.next();
            time = iterator.next();
            startNodeName = iterator.next();
            endNodeName = iterator.next();
            mappedResults.add(
                    LineDataResult.builder()
                            .line((Long) line)
                            .time((Long) time)
                            .startNodeName((String) startNodeName)
                            .endNodeName((String) endNodeName)
                            .build()
            );
        }
        return mappedResults;
    }

    public List<SingleSourceShortestPathResult> dijkstraDistanceToAllStations(String stationName, List<Double> zones, Integer maxTime){
        log.debug(String.format("Received query for distanceToAllStations from station: %s", stationName));
        List<SingleSourceShortestPathResult> mappedResults = new ArrayList<>();
        List<SingleSourceShortestPathResultDto> results = tubeRepository.dijkstraDistanceToAllStations(stationName, zones, maxTime);
        for(SingleSourceShortestPathResultDto result : results) {
            mappedResults.add(mapFromDtoToSingleSourceShortestPathResult(result));
        }
        log.debug(String.format("Returned %s results for allStations query for: %s", mappedResults.size(), stationName));
        return mappedResults;
    }

    public List<ShortestPathResult> dijkstraDetailedJourneyBetween(String firstStation, String secondStation) {
        log.debug(String.format("Received query for detailedJourneyBetween. start: %s & end: %s", firstStation, secondStation));
        return tubeRepository.dijkstraDetailedJourneyBetween(firstStation, secondStation);
    }

}
