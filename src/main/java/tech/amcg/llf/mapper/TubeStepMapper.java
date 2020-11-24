package tech.amcg.llf.mapper;

import tech.amcg.llf.domain.neo4j.ShortestPathResult;
import tech.amcg.llf.domain.response.mapping.TubeLine;
import tech.amcg.llf.domain.response.mapping.TubeStep;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TubeStepMapper {

    public TubeStepMapper() {}

    public List<TubeStep> map(List<ShortestPathResult> journey) {
        List<TubeStep> resultList = new ArrayList<>();
        if(journey.size() < 2) {
            return resultList;
        }
        Iterator<ShortestPathResult> journeyIterator = journey.stream().iterator();
        ShortestPathResult startingStation = journeyIterator.next();
        while(journeyIterator.hasNext()) {
            ShortestPathResult endStation = journeyIterator.next();
            resultList.add(new TubeStep(startingStation.getName(), endStation.getName(), TubeLine.UNKNOWN, endStation.getCost().intValue() - startingStation.getCost().intValue()));
            startingStation = endStation;
        }
        return resultList;
    }
}
