package tech.amcg.llf.domain.neo4j;

import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
public class LegacyShortestPathResult {

    String name;
    Double cost;

    public LegacyShortestPathResult(String name, Double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}
