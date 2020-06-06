package tech.amcg.llf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.amcg.llf.domain.neo4j.AllStationsResult;
import tech.amcg.llf.domain.neo4j.SingleJourneyResult;
import tech.amcg.llf.service.Neo4JRepositoryService;

import java.util.List;

@RestController
@RequestMapping("/neo4j/query")
public class Neo4JController {

    @Autowired
    Neo4JRepositoryService neo4JRepositoryService;

    @GetMapping("/allstations")
    public List<AllStationsResult> getDistanceToAllStations(@RequestParam(value = "name") String name) {
        return neo4JRepositoryService.distanceToAllStations(name);
    }

    @GetMapping("/detailedjourney")
    public List<SingleJourneyResult> getDetailedJoureyBetween(@RequestParam(value = "start") String firstStation, @RequestParam(value = "end") String lastStation) {
        return neo4JRepositoryService.detailedJourneyBetween(firstStation, lastStation);
    }
}
