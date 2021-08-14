package tech.amcg.llf.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.exception.LLFException;
import tech.amcg.llf.domain.response.LLFResult;
import tech.amcg.llf.process.LocationProcessor;
import tech.amcg.llf.process.NearbyStationsProcessor;

import java.util.List;


@Service
@Slf4j
@NoArgsConstructor
public class QueryProcessorService {

    @Autowired
    private NearbyStationsProcessor nearbyStationsProcessor;

    @Autowired
    private LocationProcessor locationProcessor;

    public List<LLFResult> process(Query query) throws LLFException {
        log.debug(String.format("Received query: %s", query.toString()));

        nearbyStationsProcessor.process(query);
        return locationProcessor.process(query);
    }

}
