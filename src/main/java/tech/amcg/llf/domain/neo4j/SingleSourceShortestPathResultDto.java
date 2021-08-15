package tech.amcg.llf.domain.neo4j;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Builder;
import lombok.ToString;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.ArrayList;
import java.util.List;

@QueryResult
@Getter @AllArgsConstructor @Builder @ToString
public class SingleSourceShortestPathResultDto {

    long index;
    String sourceNodeName;
    String targetNodeName;
    Double zone;
    Double totalCost;
    Iterable<List<Object>> lineData;
    ArrayList<String> nodeNames;
    List<Double> costs;

}

