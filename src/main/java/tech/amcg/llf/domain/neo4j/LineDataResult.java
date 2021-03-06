package tech.amcg.llf.domain.neo4j;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.neo4j.annotation.QueryResult;

@QueryResult
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LineDataResult {

    Long line;
    Long time;
    String startNodeName;
    String endNodeName;

}
