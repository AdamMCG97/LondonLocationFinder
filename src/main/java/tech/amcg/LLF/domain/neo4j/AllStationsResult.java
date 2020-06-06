package tech.amcg.llf.domain.neo4j;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class AllStationsResult {

    String destination;
    Double distance;

    public AllStationsResult(String destination, Double distance) {
        this.destination = destination;
        this.distance = distance;
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

}
