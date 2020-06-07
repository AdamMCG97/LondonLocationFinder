package tech.amcg.llf.domain.query;

import java.util.List;
import java.util.Map;

public class Person {

    private WorkLocation workLocation;

    private int maximumCommuteTime;

    private List<Station> nearestStations;

    private Map<String, Double> solutionCandidates;

    public Person(){}

    public Person(WorkLocation workLocation, int maximumCommuteTime) {
        this.workLocation = workLocation;
        this.maximumCommuteTime = maximumCommuteTime;
    }

    public WorkLocation getWorkLocation() {
        return workLocation;
    }

    public int getMaximumCommuteTime() {
        return maximumCommuteTime;
    }

    public void setNearestStations(List<Station> nearestStations) {
        this.nearestStations = nearestStations;
    }

    public List<Station> getNearestStations() {
        return nearestStations;
    }

    public Map<String, Double> getSolutionCandidates() {
        return solutionCandidates;
    }

    public void setSolutionCandidates(Map<String, Double> solutionCandidates) {
        this.solutionCandidates = solutionCandidates;
    }

}
