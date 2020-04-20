package tech.amcg.llf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amcg.llf.objects.Query;
import tech.amcg.llf.objects.ResultSet;

@Service
public class QueryProcessorService {

    @Autowired
    private NearbyStationsService nearbyStationsService;

    @Autowired
    private LocationEvaluationService locationEvaluationService;

    public QueryProcessorService(){}

    public ResultSet process(Query query) {
        nearbyStationsService.process(query);
        locationEvaluationService.evaluate(query);
        return new ResultSet("placeholder");
    }

}
