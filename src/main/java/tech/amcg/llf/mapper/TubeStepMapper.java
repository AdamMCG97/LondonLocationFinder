package tech.amcg.llf.mapper;

import tech.amcg.llf.domain.neo4j.LegacyShortestPathResult;
import tech.amcg.llf.domain.response.mapping.TubeLine;
import tech.amcg.llf.domain.response.mapping.TubeStep;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TubeStepMapper {

    public TubeStepMapper() {}

    public List<TubeStep> map(List<LegacyShortestPathResult> journey) {
        List<TubeStep> resultList = new ArrayList<>();
        if(journey.size() < 2) {
            return resultList;
        }
        Iterator<LegacyShortestPathResult> journeyIterator = journey.stream().iterator();
        LegacyShortestPathResult startingStation = journeyIterator.next();
        while(journeyIterator.hasNext()) {
            LegacyShortestPathResult endStation = journeyIterator.next();
            resultList.add(new TubeStep(startingStation.getName(), endStation.getName(), TubeLine.UNKNOWN, endStation.getCost().intValue() - startingStation.getCost().intValue()));
            startingStation = endStation;
        }
        return resultList;
    }
}
