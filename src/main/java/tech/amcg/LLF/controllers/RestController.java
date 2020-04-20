package tech.amcg.llf.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tech.amcg.llf.objects.WorkLocation;
import tech.amcg.llf.objects.Person;
import tech.amcg.llf.objects.Query;
import tech.amcg.llf.objects.ResultSet;
import tech.amcg.llf.services.QueryProcessorService;

import java.util.Arrays;

@org.springframework.web.bind.annotation.RestController
public class RestController {

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
}
