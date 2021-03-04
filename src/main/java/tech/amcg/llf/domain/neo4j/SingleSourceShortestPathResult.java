package tech.amcg.llf.domain.neo4j;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;

@QueryResult
@Getter @AllArgsConstructor @Builder
public class SingleSourceShortestPathResult {

    long index;
    String sourceNodeName;
    String targetNodeName;
    Double zone;
    Double totalCost;
    ArrayList<String> nodeNames;
    ArrayList<Double> costs;

}

