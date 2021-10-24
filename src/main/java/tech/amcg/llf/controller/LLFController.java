package tech.amcg.llf.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.amcg.llf.domain.exception.LlfException;
import tech.amcg.llf.domain.query.WorkLocation;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.LlfQuery;
import tech.amcg.llf.domain.response.LlfResult;
import tech.amcg.llf.service.LlfService;

import java.util.Arrays;
import java.util.List;

@RestController
class LlfController {

    @Autowired
    LlfService LLFService;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(method = RequestMethod.POST, value = "/llf")
    public List<LlfResult> getLlfResults(@RequestBody String jsonQuery) throws JsonProcessingException, LlfException {
        LlfQuery query = objectMapper.readValue(jsonQuery, LlfQuery.class);
        return LLFService.processQuery(query);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/validatepostcode")
    public String validatePostcode(@RequestParam String postcode) throws LlfException {
        return LLFService.validatePostcode(postcode) ? "True" : "False";
    }

    @RequestMapping("/llfquery")
    public String getLlfQuery() throws JsonProcessingException {
        LlfQuery sampleQuery = LlfQuery.builder()
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
