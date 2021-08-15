package tech.amcg.llf.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.exception.LLFException;
import tech.amcg.llf.domain.response.LLFResult;
import tech.amcg.llf.process.TubeMapProcessor;
import tech.amcg.llf.process.ResultsProcessor;
import tech.amcg.llf.process.WorkLocationProcessor;

import java.util.List;

@Service
@Slf4j
@NoArgsConstructor
public class LLFService {

    @Autowired
    private WorkLocationProcessor workLocationProcessor;

    @Autowired
    private TubeMapProcessor tubeMapProcessor;

    @Autowired
    private ResultsProcessor resultsProcessor;

    public List<LLFResult> processQuery(Query query) throws LLFException {
        log.debug(String.format("Received query: %s", query.toString()));

        workLocationProcessor.findNearestStationsByWalkingDistance(query);

        tubeMapProcessor.findAllAcceptablePaths(query);

        return resultsProcessor.generateResults(query);
    }

    public boolean validatePostcode(String postcode) throws LLFException {
        return workLocationProcessor.isValidPostcode(postcode);
    }

}
