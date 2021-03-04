package tech.amcg.llf.mapper;

import lombok.NoArgsConstructor;
import tech.amcg.llf.domain.neo4j.ShortestPathResult;
import tech.amcg.llf.domain.response.mapping.JourneyStep;
import tech.amcg.llf.domain.response.mapping.SpecificWalkStep;
import tech.amcg.llf.domain.response.mapping.TubeLine;
import tech.amcg.llf.domain.response.mapping.TubeStep;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@NoArgsConstructor
public class TubeStepMapper {

    public List<JourneyStep> map(ShortestPathResult journey, int travelTime) {
        List<JourneyStep> resultList = new ArrayList<>();

        if(journey.getSourceNodeName().equals(journey.getTargetNodeName())) {
            return resultList;
        }

        Iterator<String> journeyStationIterator = journey.getNodeNames().iterator();
        Iterator<Double> journeyCostIterator = journey.getCosts().iterator();

        String startingStation = journeyStationIterator.next();
        Double stepLineId = journeyCostIterator.next();
        Double cumulativeStepIds = stepLineId;
        TubeLine currentLine;
        TubeLine previousLine = TubeLine.UNKNOWN;

        while(journeyStationIterator.hasNext()) {
            String endStation = journeyStationIterator.next();
            stepLineId = journeyCostIterator.next() - cumulativeStepIds;
            cumulativeStepIds += stepLineId;
            currentLine = TubeLine.getById(stepLineId);
            if(previousLine != TubeLine.UNKNOWN && currentLine != previousLine) {
                resultList.add(new SpecificWalkStep(previousLine.getFullName(), currentLine.getFullName(), 3));
            }
            resultList.add(new TubeStep(startingStation, endStation, currentLine, 0d));
            startingStation = endStation;
            previousLine = currentLine;
        }
        return resultList;
    }
}
