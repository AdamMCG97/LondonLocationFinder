package tech.amcg.llf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.ResultSet;

@Service
public class QueryProcessorService {

    @Autowired
    private NearbyStationsService nearbyStationsService;

    @Autowired
    private LocationEvaluationService locationEvaluationService;

    @Autowired
    private LocationFindingService locationFindingService;

    public QueryProcessorService(){}

    public ResultSet process(Query query) {
        nearbyStationsService.process(query);
        locationFindingService.findLocations(query);
        locationEvaluationService.evaluate(query);

        return new ResultSet("placeholder");
    }

}
