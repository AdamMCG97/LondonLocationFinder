package tech.amcg.llf.domain.neo4j;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.neo4j.annotation.QueryResult;
import java.util.ArrayList;

@QueryResult
@Getter @AllArgsConstructor
public class ShortestPathResult {

    long index;
    String sourceNodeName;
    String targetNodeName;
    Double totalCost;
    ArrayList<String> nodeNames;
    ArrayList<Double> costs;

}
