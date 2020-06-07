package tech.amcg.llf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tech.amcg.llf.domain.query.WorkLocation;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.Response;
import tech.amcg.llf.service.Neo4JRepositoryService;
import tech.amcg.llf.service.QueryProcessorService;

import java.util.Arrays;

@org.springframework.web.bind.annotation.RestController
public class LLFController {

    @Autowired
    Neo4JRepositoryService neo4JRepositoryService;

    @Autowired
    QueryProcessorService queryProcessorService;

    @RequestMapping(method = RequestMethod.POST, value = "/llf")
    public Response getLLFResults(@RequestBody Query query) throws Exception {
        return queryProcessorService.process(query);
    }

    @RequestMapping("/llfquery")
    public Query getLLFQuery(){
        return new Query(Arrays.asList(new Person( new WorkLocation("W2 6TT"), 20), new Person(new WorkLocation("SE16 6YR"), 30)), "Tube Only", 2, 1500, 2000, true);
    }

}
