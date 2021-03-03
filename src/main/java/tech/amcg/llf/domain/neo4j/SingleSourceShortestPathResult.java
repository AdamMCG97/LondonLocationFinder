package tech.amcg.llf.domain.neo4j;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;

@QueryResult
public class SingleSourceShortestPathResult {

    long index;
    String sourceNodeName;
    String targetNodeName;
    Double totalCost;
    ArrayList<String> nodeNames;
    ArrayList<Double> costs;

    public SingleSourceShortestPathResult(long index, String sourceNodeName, String targetNodeName, Double totalCost, ArrayList<String> nodeNames, ArrayList<Double> costs) {
        this.index = index;
        this.sourceNodeName = sourceNodeName;
        this.targetNodeName = targetNodeName;
        this.totalCost = totalCost;
        this.nodeNames = nodeNames;
        this.costs = costs;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getSourceNodeName() {
        return sourceNodeName;
    }

    public void setSourceNodeName(String sourceNodeName) {
        this.sourceNodeName = sourceNodeName;
    }

    public String getTargetNodeName() {
        return targetNodeName;
    }

    public void setTargetNodeName(String targetNodeName) {
        this.targetNodeName = targetNodeName;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public ArrayList<String> getNodeNames() {
        return nodeNames;
    }

    public void setNodeNames(ArrayList<String> nodeNames) {
        this.nodeNames = nodeNames;
    }

    public ArrayList<Double> getCosts() {
        return costs;
    }

    public void setCosts(ArrayList<Double> costs) {
        this.costs = costs;
    }
}

