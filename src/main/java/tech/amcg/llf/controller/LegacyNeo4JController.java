package tech.amcg.llf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.amcg.llf.domain.neo4j.LegacySingleSourceShortestPathResult;
import tech.amcg.llf.service.Neo4JRepositoryService;

import java.util.List;

@RestController
@RequestMapping("/neo4j/v1/query")
public class LegacyNeo4JController {

    @Autowired
    Neo4JRepositoryService neo4JRepositoryService;

    @GetMapping("/allstations")
    public List<LegacySingleSourceShortestPathResult> getDistanceToAllStations(@RequestParam(value = "name") String name) {
        return neo4JRepositoryService.distanceToAllStations(name);
    }
}
