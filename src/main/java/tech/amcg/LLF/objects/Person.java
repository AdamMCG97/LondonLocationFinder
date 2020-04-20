package tech.amcg.llf.objects;

import java.util.List;

public class Person {

    private WorkLocation workLocation;

    private int maximumCommuteTime;

    private List<Station> nearestStations;

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

}
