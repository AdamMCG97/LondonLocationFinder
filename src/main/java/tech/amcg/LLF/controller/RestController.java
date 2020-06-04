package tech.amcg.llf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import tech.amcg.llf.domain.WorkLocation;
import tech.amcg.llf.domain.Person;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.ResultSet;
import tech.amcg.llf.repository.TubeRepository;
import tech.amcg.llf.service.QueryProcessorService;

import java.util.Arrays;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    TubeRepository tubeRepository;

    @Autowired
    QueryProcessorService queryProcessorService;

    @RequestMapping(method = RequestMethod.POST, value = "/llf")
    public ResultSet getLLFResults(@RequestBody Query query) throws Exception {
        return queryProcessorService.process(query);
    }

    @RequestMapping("/llfquery")
    public Query getLLFQuery(){
        return new Query(Arrays.asList(new Person( new WorkLocation("W2 6TT"), 20), new Person(new WorkLocation("SE16 6YR"), 30)), "Tube Only", 2, 1500, 2000, true);
    }

    @GetMapping("/neo4jquery")
    public Map<String, Integer> getNeo4jResults(@RequestParam(value = "name") String name) {
        return tubeRepository.distanceToAllStations(name);
    }
}
