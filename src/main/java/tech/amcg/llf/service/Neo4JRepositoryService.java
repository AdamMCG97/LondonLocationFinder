package tech.amcg.llf.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.neo4j.ShortestPathResult;
import tech.amcg.llf.domain.neo4j.LegacySingleSourceShortestPathResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;
import tech.amcg.llf.process.NearbyStationsProcessor;
import tech.amcg.llf.repository.TubeRepository;

import java.util.List;

@Service
@Slf4j
public class Neo4JRepositoryService {

    @Autowired
    private TubeRepository tubeRepository;

    public Neo4JRepositoryService() {
    }

    @Deprecated
    public List<LegacySingleSourceShortestPathResult> distanceToAllStations(String stationName){
        log.debug(String.format("Received query for legacy distanceToAllStations from station: %s", stationName));
        return tubeRepository.distanceToAllStations(stationName);
        //TODO: Enrich distance times by adding time for changes between lines
    }

    public List<SingleSourceShortestPathResult> dijkstraDistanceToAllStations(String stationName){
        log.debug(String.format("Received query for distanceToAllStations from station: %s", stationName));
        return tubeRepository.dijkstraDistanceToAllStations(stationName);
        //TODO: Enrich distance times by adding time for changes between lines
    }

    public List<ShortestPathResult> dijkstraDetailedJourneyBetween(String firstStation, String secondStation) {
        log.debug(String.format("Received query for detailedJourneyBetween start: %s & end: %s", firstStation, secondStation));
        return tubeRepository.dijkstraDetailedJourneyBetween(firstStation, secondStation);
    }

}
