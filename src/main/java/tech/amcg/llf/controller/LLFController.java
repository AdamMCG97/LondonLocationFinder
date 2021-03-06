package tech.amcg.llf.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tech.amcg.llf.domain.query.WorkLocation;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.response.LLFResult;
import tech.amcg.llf.service.QueryProcessorService;

import java.util.Arrays;
import java.util.List;

@RestController
public class LLFController {

    @Autowired
    QueryProcessorService queryProcessorService;

    ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(method = RequestMethod.POST, value = "/llf")
    public List<LLFResult> getLLFResults(@RequestBody String jsonQuery) throws Exception {
        Query query = objectMapper.readValue(jsonQuery, Query.class);
        return queryProcessorService.process(query);
    }

    @RequestMapping("/llfquery")
    public String getLLFQuery() throws JsonProcessingException {
        Query sampleQuery = Query.builder()
                .exclusionZones(Arrays.asList(1d,1.5d,2d))
                .differentCommuteMaximums(true)
                .personParamsList(Arrays.asList(
                        Person.builder()
                            .personID("Ryan")
                            .workLocation(WorkLocation.builder().postcode("W2 6TT").build())
                            .maximumCommuteTime(40)
                            .maximumWalkTime(15)
                            .build(),
                        Person.builder()
                            .personID("Adam")
                            .workLocation(WorkLocation.builder().postcode("E14 5AH").build())
                            .maximumCommuteTime(30)
                            .maximumWalkTime(15)
                            .build()))
                .lowerBoundPriceRange(1500)
                .upperBoundPriceRange(2000)
                .numberOfBedrooms(2)
                .build();

        return objectMapper.writeValueAsString(sampleQuery);
    }
}
