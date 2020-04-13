package tech.amcg.LLF.objects;

import java.util.List;

public class LLFPerson {

    private LLFLocation workLocation;

    private int maximumCommuteTime;

    private List<LLFLocation> nearestStations;

    public LLFPerson(){}

    public LLFPerson(LLFLocation workLocation, int maximumCommuteTime) {
        this.workLocation = workLocation;
        this.maximumCommuteTime = maximumCommuteTime;
    }

    public LLFLocation getWorkLocation() {
        return workLocation;
    }

    public int getMaximumCommuteTime() {
        return maximumCommuteTime;
    }

    public void setNearestStations(List<LLFLocation> nearestStations) {
        this.nearestStations = nearestStations;
    }

    public List<LLFLocation> getNearestStations() {
        return nearestStations;
    }
}
