package tech.amcg.llf.domain.neo4j;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class LegacySingleSourceShortestPathResult {

    String destination;
    Double distance;
    Double zone;

    public LegacySingleSourceShortestPathResult(String destination, Double distance, Double zone) {
        this.destination = destination;
        this.distance = distance;
        this.zone = zone;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getZone() {
        return zone;
    }

    public void setZone(Double zone) {
        this.zone = zone;
    }
}
