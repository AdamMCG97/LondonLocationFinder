package tech.amcg.llf.domain;

import tech.amcg.llf.domain.mapping.Journey;
import tech.amcg.llf.domain.mapping.LocationCandidate;

import java.util.List;

public class Response {

    private List<Journey> journeyList;

    private List<LocationCandidate> matchingStationList;

    public Response(List<LocationCandidate> matchingStationList) {
        this.matchingStationList = matchingStationList;
    }

    public Response(){}

    public List<Journey> getJourneyList() {
        return journeyList;
    }

    public void setJourneyList(List<Journey> journeyList) {
        this.journeyList = journeyList;
    }

    public List<LocationCandidate> getMatchingStationList() {
        return matchingStationList;
    }

    public void setMatchingStationList(List<LocationCandidate> matchingStationList) {
        this.matchingStationList = matchingStationList;
    }
}
