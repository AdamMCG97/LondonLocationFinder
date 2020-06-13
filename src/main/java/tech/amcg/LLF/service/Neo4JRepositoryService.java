package tech.amcg.llf.service;

import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;
import tech.amcg.llf.domain.neo4j.ShortestPathResult;
import tech.amcg.llf.repository.TubeRepository;

import java.util.List;

@Service
public class Neo4JRepositoryService {

    private final TubeRepository tubeRepository;

    public Neo4JRepositoryService(TubeRepository tubeRepository) {
        this.tubeRepository = tubeRepository;
    }

    public List<SingleSourceShortestPathResult> distanceToAllStations(String stationName){
        return tubeRepository.distanceToAllStations(stationName);
        //TODO: Enrich distance times by adding time for changes between lines
    }

    public List<ShortestPathResult> detailedJourneyBetween(String firstStation, String secondStation) {
        return tubeRepository.detailedJourneyBetween(firstStation, secondStation);
    }
}
