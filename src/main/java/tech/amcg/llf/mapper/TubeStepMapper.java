package tech.amcg.llf.mapper;

import tech.amcg.llf.domain.neo4j.ShortestPathResult;
import tech.amcg.llf.domain.response.mapping.TubeLine;
import tech.amcg.llf.domain.response.mapping.TubeStep;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TubeStepMapper {

    public TubeStepMapper() {}

    public List<TubeStep> map(ShortestPathResult journey) {
        List<TubeStep> resultList = new ArrayList<>();

        if(journey.getSourceNodeName().equals(journey.getTargetNodeName())) {
            resultList.add(new TubeStep(journey.getSourceNodeName(), journey.getTargetNodeName(), TubeLine.UNKNOWN, journey.getTotalCost()));
        }

        Iterator<String> journeyStationIterator = journey.getNodeNames().iterator();
        Iterator<Double> journeyCostIterator = journey.getCosts().iterator();

        String startingStation = journeyStationIterator.next();
        Double stepCost = journeyCostIterator.next();

        while(journeyStationIterator.hasNext()) {
            String endStation = journeyStationIterator.next();
            stepCost = journeyCostIterator.next();
            resultList.add(new TubeStep(startingStation, endStation, TubeLine.UNKNOWN, stepCost));
            startingStation = endStation;
        }
        return resultList;
    }
}
