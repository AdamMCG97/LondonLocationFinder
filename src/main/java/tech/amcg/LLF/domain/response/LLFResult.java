package tech.amcg.llf.domain.response;

import java.util.List;

public class LLFResult {

    private String name;

    private List<IndividualJourney> individualJourneys;

    private Double averageTravelTime;

    private Double maximumTravelTime;

    private int zone;

    public LLFResult(String name, List<IndividualJourney> individualJourneys, Double averageTravelTime, int zone, Double maximumTravelTime) {
        this.name = name;
        this.individualJourneys = individualJourneys;
        this.averageTravelTime = averageTravelTime;
        this.zone = zone;
        this.maximumTravelTime = maximumTravelTime;
    }

    public String getName() {
        return name;
    }

    public List<IndividualJourney> getIndividualJourneys() {
        return individualJourneys;
    }

    public int getZone() {
        return zone;
    }

    public Double getAverageTravelTime() {
        return averageTravelTime;
    }

    public Double getMaximumTravelTime() {
        return maximumTravelTime;
    }
}
