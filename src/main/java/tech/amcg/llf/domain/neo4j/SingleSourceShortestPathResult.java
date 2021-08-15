package tech.amcg.llf.domain.neo4j;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter @AllArgsConstructor @Builder @ToString @Setter
public class SingleSourceShortestPathResult {

    long index;
    String sourceNodeName;
    String targetNodeName;
    Double zone;
    Double totalCost;
    List<LineDataResult> lineData;
    ArrayList<String> nodeNames;
    List<Double> costs;

}

