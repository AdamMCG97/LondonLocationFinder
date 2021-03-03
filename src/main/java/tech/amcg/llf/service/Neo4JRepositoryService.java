package tech.amcg.llf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.neo4j.ShortestPathResult;
import tech.amcg.llf.domain.neo4j.LegacySingleSourceShortestPathResult;
import tech.amcg.llf.domain.neo4j.LegacyShortestPathResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;
import tech.amcg.llf.repository.TubeRepository;

import java.util.List;

@Service
public class Neo4JRepositoryService {

    @Autowired
    private TubeRepository tubeRepository;

    public Neo4JRepositoryService() {
    }

    public List<LegacySingleSourceShortestPathResult> distanceToAllStations(String stationName){
        return tubeRepository.distanceToAllStations(stationName);
        //TODO: Enrich distance times by adding time for changes between lines
    }

    public List<LegacyShortestPathResult> detailedJourneyBetween(String firstStation, String secondStation) {
        return tubeRepository.detailedJourneyBetween(firstStation, secondStation);
    }

    public List<SingleSourceShortestPathResult> dijkstraDistanceToAllStations(String stationName){
        return tubeRepository.dijkstraDistanceToAllStations(stationName);
        //TODO: Enrich distance times by adding time for changes between lines
    }

    public List<ShortestPathResult> dijkstraDetailedJourneyBetween(String firstStation, String secondStation) {
        return tubeRepository.dijkstraDetailedJourneyBetween(firstStation, secondStation);
    }

}
