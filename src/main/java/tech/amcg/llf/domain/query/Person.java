package tech.amcg.llf.domain.query;

import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;

import java.util.List;

public class Person {

    private String personID;

    private WorkLocation workLocation;

    private int maximumCommuteTime;

    private List<Station> nearestStations;

    private List<SingleSourceShortestPathResult> solutionCandidates;

    private int maximumWalkTime;

    public Person(){}

    public Person(WorkLocation workLocation, int maximumCommuteTime, String personID, int maximumWalkTime) {
        this.workLocation = workLocation;
        this.maximumCommuteTime = maximumCommuteTime;
        this.personID = personID;
        this.maximumWalkTime = maximumWalkTime;
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

    public List<SingleSourceShortestPathResult> getSolutionCandidates() {
        return solutionCandidates;
    }

    public void setSolutionCandidates(List<SingleSourceShortestPathResult> solutionCandidates) {
        this.solutionCandidates = solutionCandidates;
    }

    public String getPersonID() {
        return personID;
    }

    public int getMaximumWalkTime() {
        return maximumWalkTime;
    }
}
