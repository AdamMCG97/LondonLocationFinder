package tech.amcg.llf.mapper;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.amcg.llf.domain.neo4j.LineDataResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Slf4j
public class QueryPathTrimmer {

    public SingleSourceShortestPathResult trim(SingleSourceShortestPathResult result) {
        List<LineDataResult> trimmedLineData = result.getLineData();
        List<String> nodeNames = result.getNodeNames();
        //remove final node from check as trimming works by checking starting station, no steps will exist with the end node as a starting station
        nodeNames.remove(nodeNames.size() - 1);
        for(String node : nodeNames) {
            List<LineDataResult> duplicateSteps = trimmedLineData.stream().filter(step -> step.getStartNodeName().equals(node)).collect(Collectors.toList());
            if(duplicateSteps.size() > 1) {
                LineDataResult chosenStep = null;
                List<LineDataResult> fastestSteps = findFastestDuplicates(duplicateSteps);
                if(fastestSteps.size() > 1) {
                    if(nodeNames.indexOf(node) != 0) {
                        String previousNode = nodeNames.get(nodeNames.indexOf(node) - 1);
                        LineDataResult previousStep = trimmedLineData.stream().filter(step -> step.getStartNodeName().equals(previousNode)).collect(Collectors.toList()).get(0);
                        List<LineDataResult> fastStepContinuedOnLine = fastestSteps.stream().filter(step -> step.getLine().equals(previousStep.getLine())).collect(Collectors.toList());
                        if (fastStepContinuedOnLine.size() == 1) {
                            chosenStep = fastStepContinuedOnLine.get(0);
                        }
                    }
                    if(null == chosenStep){
                        List<Long> lines = fastestSteps.stream().map(LineDataResult::getLine).collect(Collectors.toList());
                        List<String> remainingNodes = nodeNames.subList(nodeNames.indexOf(node), nodeNames.size() - 1);
                        Long preferredLine = findPreferredLine(lines, remainingNodes, trimmedLineData);
                        chosenStep = fastestSteps.stream().filter(step -> step.getLine().equals(preferredLine)).collect(Collectors.toList()).get(0);
                    }
                }
                else {
                    chosenStep = fastestSteps.get(0);
                }
                duplicateSteps.remove(chosenStep);
                trimmedLineData.removeAll(duplicateSteps);
            }
            else if(duplicateSteps.size() == 0) {
                log.error(String.format("No steps found in path: %s for Node: %s.", result.getLineData().toString(), node));
            }
        }
        result.setLineData(trimmedLineData);
        return result;
    }

    private Long findPreferredLine(List<Long> lines, List<String> remainingNodes, List<LineDataResult> trimmedLineData) {
        if(remainingNodes.size() == 0) {
            return lines.get(0);
        }
        Long prefferedLine = null;
        Iterator<String> remainingNodeIterator = remainingNodes.iterator();
        var ref = new Object() {
            String currentNode = remainingNodeIterator.next();
        };
        while (null == prefferedLine) {
            List<LineDataResult> duplicateSteps = trimmedLineData.stream().filter(step -> step.getStartNodeName().equals(ref.currentNode)).collect(Collectors.toList());
            if (duplicateSteps.size() > 1) {
                List<LineDataResult> fastestSteps = findFastestDuplicates(duplicateSteps);
                if (fastestSteps.size() > 1) {
                    List<Long> nextStepLines = fastestSteps.stream().map(LineDataResult::getLine).collect(Collectors.toList());
                    if(lines.containsAll(nextStepLines) && remainingNodeIterator.hasNext()) {
                        ref.currentNode = remainingNodeIterator.next();
                    }
                    else {
                        prefferedLine = lines.stream().findAny().filter(line -> line.equals(duplicateSteps.get(0).getLine())).orElse(lines.get(0));
                    }
                }
            } else {
                prefferedLine = lines.stream().findAny().filter(line -> line.equals(duplicateSteps.get(0).getLine())).orElse(lines.get(0));
            }
        }
        return prefferedLine;
    }

    private List<LineDataResult> findFastestDuplicates(List<LineDataResult> duplicateSteps) {
        LineDataResult fastestStep = Collections.min(duplicateSteps, Comparator.comparing(LineDataResult::getTime));
        return duplicateSteps.stream().filter(step -> step.getTime().equals(fastestStep.getTime())).collect(Collectors.toList());
    }

}
