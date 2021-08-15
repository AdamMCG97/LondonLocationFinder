package tech.amcg.llf.domain.query;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Person {

    private String personID;

    private WorkLocation workLocation;

    private int maximumCommuteTime;

    private int maximumWalkTime;

    private List<Station> nearestStations;

    private List<SingleSourceShortestPathResult> acceptablePaths;

}
