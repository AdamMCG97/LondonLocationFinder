package tech.amcg.llf.domain.neo4j;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
@Getter @AllArgsConstructor
public class LegacySingleSourceShortestPathResult {

    String destination;
    Double distance;
    Double zone;

}
