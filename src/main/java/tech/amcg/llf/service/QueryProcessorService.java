package tech.amcg.llf.service;

import com.mashape.unirest.http.exceptions.UnirestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(QueryProcessorService.class);

    public Response process(Query query) throws LLFException, UnirestException {
        logger.debug(String.format("Received query: %s", query.toString()));

        nearbyStationsProcessor.process(query);
        return locationProcessor.process(query);
    }

}
