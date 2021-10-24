package tech.amcg.llf.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.LlfQuery;
import tech.amcg.llf.domain.exception.LlfException;
import tech.amcg.llf.domain.response.LlfResult;
import tech.amcg.llf.process.TubeMapProcessor;
import tech.amcg.llf.process.ResultsProcessor;
import tech.amcg.llf.process.WorkLocationProcessor;

import java.util.List;

@Service
@Slf4j
@NoArgsConstructor
public class LlfService {

    @Autowired
    private WorkLocationProcessor workLocationProcessor;

    @Autowired
    private TubeMapProcessor tubeMapProcessor;

    @Autowired
    private ResultsProcessor resultsProcessor;

    public List<LlfResult> processQuery(LlfQuery query) throws LlfException {
        log.debug(String.format("Received query: %s", query.toString()));

        workLocationProcessor.findNearestStationsByWalkingDistance(query);

        tubeMapProcessor.findAllAcceptablePaths(query);

        return resultsProcessor.generateResults(query);
    }

    public boolean validatePostcode(String postcode) throws LlfException {
        return workLocationProcessor.isValidPostcode(postcode);
    }

}
