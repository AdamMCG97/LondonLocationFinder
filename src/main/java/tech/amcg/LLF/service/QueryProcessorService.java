package tech.amcg.llf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.Response;

@Service
public class QueryProcessorService {

    @Autowired
    private NearbyStationsService nearbyStationsService;

    @Autowired
    private LocationFindingService locationFindingService;

    public QueryProcessorService(){}

    public Response process(Query query) {
        nearbyStationsService.process(query);
        return locationFindingService.findLocations(query);
    }

}
