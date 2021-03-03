package tech.amcg.llf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.amcg.llf.domain.neo4j.ShortestPathResult;
import tech.amcg.llf.domain.neo4j.LegacySingleSourceShortestPathResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;
import tech.amcg.llf.service.Neo4JRepositoryService;

import java.util.List;

@RestController
@RequestMapping("/neo4j/v2/query")
public class Neo4JController {

    @Autowired
    Neo4JRepositoryService neo4JRepositoryService;

    @GetMapping("/allstations")
    public List<SingleSourceShortestPathResult> getDistanceToAllStations(@RequestParam(value = "name") String name) {
        return neo4JRepositoryService.dijkstraDistanceToAllStations(name);
    }

    @GetMapping("/detailedjourney")
    public List<ShortestPathResult> getDetailedJoureyBetween(@RequestParam(value = "start") String firstStation, @RequestParam(value = "end") String lastStation) {
        return neo4JRepositoryService.dijkstraDetailedJourneyBetween(firstStation, lastStation);
    }
}
