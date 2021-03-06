package tech.amcg.llf.mapper;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.amcg.llf.domain.exception.LLFException;
import tech.amcg.llf.domain.neo4j.LineDataResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;
import tech.amcg.llf.domain.response.mapping.JourneyStep;
import tech.amcg.llf.domain.response.mapping.SpecificWalkStep;
import tech.amcg.llf.domain.response.mapping.TubeLine;
import tech.amcg.llf.domain.response.mapping.TubeStep;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static tech.amcg.llf.process.LocationProcessor.STANDARD_TIME_PER_LINE_CHANGE;

@NoArgsConstructor
@Slf4j
public class TubeStepMapper {

    public List<JourneyStep> map(SingleSourceShortestPathResult path) throws LLFException {
        List<JourneyStep> resultList = new ArrayList<>();

        Iterator<LineDataResult> pathIterator = path.getLineData().iterator();

        TubeLine currentLine;
        TubeLine previousLine = TubeLine.UNKNOWN;

        while(pathIterator.hasNext()) {
            LineDataResult pathStep = pathIterator.next();
            currentLine = TubeLine.getById(pathStep.getLine().doubleValue());
            if(previousLine != TubeLine.UNKNOWN && currentLine != previousLine) {
                resultList.add(new SpecificWalkStep(previousLine.getFullName(), currentLine.getFullName(), STANDARD_TIME_PER_LINE_CHANGE.intValue()));
            }
            resultList.add(new TubeStep(pathStep.getStartNodeName(), pathStep.getEndNodeName(), currentLine, pathStep.getTime().doubleValue()));
            previousLine = currentLine;
        }
        return resultList;
    }
}
