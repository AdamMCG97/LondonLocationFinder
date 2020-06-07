package tech.amcg.llf.domain.response;

import java.util.List;

public class LocationCandidate {

    private List<Double> travelTime;

    private String stationName;

    private Double averageTravelTime;

    public LocationCandidate(List<Double> travelTime, String stationName) {
        this.travelTime = travelTime;
        this.stationName = stationName;
        this.averageTravelTime = calculateAverage();
    }

    public List<Double> getTravelTime() {
        return travelTime;
    }

    public String getStationName() {
        return stationName;
    }

    public Double getAverageTravelTime() {
        return averageTravelTime;
    }

    private Double calculateAverage() {
      Double total = 0d;

        for(Double time:travelTime) {
            total += time;
        }
        return total;
    }
}
