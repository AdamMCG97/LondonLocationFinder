package tech.amcg.llf.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.Response;
import tech.amcg.llf.domain.exception.LLFException;
import tech.amcg.llf.process.LocationProcessor;
import tech.amcg.llf.process.NearbyStationsProcessor;

@Service
public class QueryProcessorService {

    @Autowired
    private NearbyStationsProcessor nearbyStationsProcessor;

    @Autowired
    private LocationProcessor locationProcessor;

    public QueryProcessorService(){}

    public Response process(Query query) throws LLFException, UnirestException {
        nearbyStationsProcessor.process(query);
        return locationProcessor.process(query);
    }

}
