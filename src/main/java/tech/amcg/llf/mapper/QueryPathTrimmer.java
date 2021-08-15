package tech.amcg.llf.mapper;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.amcg.llf.domain.neo4j.LineDataResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Slf4j
public class QueryPathTrimmer {

    List<LineDataResult> trimmedLineData;
    ArrayList<String> nodeNames;

    public SingleSourceShortestPathResult trim(SingleSourceShortestPathResult result) {
        trimmedLineData = result.getLineData();
        nodeNames = result.getNodeNames();
        //remove final node from check as trimming works by checking starting station, no steps will exist with the end node as a starting station
        nodeNames.remove(nodeNames.size() - 1);
        for(String node : nodeNames) {
            long stepOccurrencesInPath = stepOccurrencesInPath(node);
            if(stepOccurrencesInPath > 1) {
                LineDataResult bestStep = selectBestOfDuplicateSteps(node);
                removeAllOtherDuplicatesFromPath(node, bestStep);
            }
            else if(stepOccurrencesInPath == 0) {
                log.error(String.format("No steps found in path: %s for Node: %s.", trimmedLineData.toString(), node));
            }
        }
        result.setLineData(trimmedLineData);
        return result;
    }

    private void removeAllOtherDuplicatesFromPath(String node, LineDataResult bestStep) {
        List<LineDataResult> duplicateStepsForNode = getStepsInPathByNode(node);
        duplicateStepsForNode.remove(bestStep);
        trimmedLineData.removeAll(duplicateStepsForNode);
    }

    private LineDataResult selectBestOfDuplicateSteps(String node) {
        List<LineDataResult> duplicateStepsForNode = getStepsInPathByNode(node);
        List<LineDataResult> fastestSteps = findDuplicateStepsWithFastestTime(duplicateStepsForNode);

        if(fastestSteps.size() == 1) {
            return fastestSteps.get(0);
        }
        else {
            return selectBestOfFastestSteps(node, fastestSteps);
        }
    }

    private LineDataResult selectBestOfFastestSteps(String node, List<LineDataResult> fastestSteps) {
        LineDataResult chosenStep = null;
        if(!isStartingStation(node)) {
            chosenStep = selectStepWithoutLineChangeFromPrevious(node, fastestSteps);
        }
        //if a step without a line change from previous node is not an option
        if(null == chosenStep) {
            chosenStep = selectStepOnSameLineAsNextStep(node, fastestSteps);
        }
        //if a step without a line change for previous or following node is not an option
        if(null == chosenStep) {
            //return any step as it is inconsequential
            chosenStep = fastestSteps.get(0);
        }
        return chosenStep;
    }

    private LineDataResult selectStepOnSameLineAsNextStep(String node, List<LineDataResult> fastestSteps) {
        List<Long> lines = fastestSteps.stream().map(LineDataResult::getLine).collect(Collectors.toList());
        Long preferredLine = findPreferredLine(lines, node);
        List<LineDataResult> fastStepContinuedOnLine = fastestSteps.stream().filter(step -> step.getLine().equals(preferredLine)).collect(Collectors.toList());
        if (fastStepContinuedOnLine.size() == 1) {
            return fastStepContinuedOnLine.get(0);
        }
        else if (fastStepContinuedOnLine.size() > 1) {
            log.error(String.format("Duplicates of the same step exist on path. Duplicate steps: %s. Full path: %s", trimmedLineData.toString(), fastestSteps.toString()));
        }
        return null;
    }

    private LineDataResult selectStepWithoutLineChangeFromPrevious(String node, List<LineDataResult> fastestSteps) {
        String previousNode = getPreviousNodeName(node);
        LineDataResult previousStep = getStepsInPathByNode(previousNode).get(0);
        List<LineDataResult> fastStepContinuedOnLine = fastestSteps.stream().filter(step -> step.getLine().equals(previousStep.getLine())).collect(Collectors.toList());
        if (fastStepContinuedOnLine.size() == 1) {
            return fastStepContinuedOnLine.get(0);
        }
        else if (fastStepContinuedOnLine.size() > 1) {
            log.error(String.format("Duplicates of the same step exist on path. Duplicate steps: %s. Full path: %s", trimmedLineData.toString(), fastestSteps.toString()));
        }
        return null;
    }

    private String getPreviousNodeName(String node) {
        if(isStartingStation(node)) {
            return null;
        }
        return nodeNames.get(nodeNames.indexOf(node) - 1);
    }

    private String getNextNodeName(String node) {
        if(isEndStation(node)) {
            return null;
        }
        return nodeNames.get(nodeNames.indexOf(node) + 1);
    }

    private boolean isStartingStation(String node) {
        return nodeNames.indexOf(node) == 0;
    }

    private boolean isEndStation(String node) {
        return nodeNames.indexOf(node) == nodeNames.size() - 1;
    }

    private long stepOccurrencesInPath(String node) {
        return getStepsInPathByNode(node).size();
    }

    private List<LineDataResult> getStepsInPathByNode(String node) {
        return trimmedLineData.stream().filter(step -> step.getStartNodeName().equals(node)).collect(Collectors.toList());
    }

    private Long findPreferredLine(List<Long> lines, String node) {
        if(isEndStation(node)) {
            return null;
        }
        String nextNode = getNextNodeName(node);
        List<LineDataResult> stepsForNextNode = getStepsInPathByNode(nextNode);
        if (stepsForNextNode.size() > 1) {
            return chooseLineFromFastestStep(stepsForNextNode, lines, nextNode);
        } else {
            return getLineInListIfExists(lines, stepsForNextNode.get(0).getLine());
        }
    }

    private Long chooseLineFromFastestStep(List<LineDataResult> stepsForNextNode, List<Long> lines, String nextNode) {
        List<LineDataResult> fastestSteps = findDuplicateStepsWithFastestTime(stepsForNextNode);
        if (fastestSteps.size() > 1) {
            List<Long> nextStepLines = fastestSteps.stream().map(LineDataResult::getLine).collect(Collectors.toList());
            if(lines.containsAll(nextStepLines)) {
                //recursively check rest of path until a preferred line is found
                return findPreferredLine(lines, nextNode);
            }
            else {
                return getLineInListIfExists(lines, stepsForNextNode.get(0).getLine());
            }
        }
        else {
            return getLineInListIfExists(lines, stepsForNextNode.get(0).getLine());
        }
    }

    private Long getLineInListIfExists(List<Long> lines, Long desiredLine) {
        return (lines.contains(desiredLine)) ? desiredLine : null;
    }

    private List<LineDataResult> findDuplicateStepsWithFastestTime(List<LineDataResult> duplicateSteps) {
        LineDataResult fastestStep = Collections.min(duplicateSteps, Comparator.comparing(LineDataResult::getTime));
        return duplicateSteps.stream().filter(step -> step.getTime().equals(fastestStep.getTime())).collect(Collectors.toList());
    }

}
