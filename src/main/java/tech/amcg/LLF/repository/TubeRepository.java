package tech.amcg.llf.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;
import tech.amcg.llf.domain.neo4j.ShortestPathResult;
import tech.amcg.llf.domain.neo4j.Tube;

import java.io.Serializable;
import java.util.List;

@Repository
public interface TubeRepository extends Neo4jRepository<Tube, Long> {

    @Query("MATCH (n:Station {name: $name})\n" +
            "CALL gds.alpha.shortestPath.deltaStepping.stream({\n" +
            "  nodeProjection: 'Station',\n" +
            "  relationshipProjection: {\n" +
            "    CONNECTS: {\n" +
            "      type: 'CONNECTS',\n" +
            "      properties: ['time','line']\n" +
            "      }\n" +
            "    },\n" +
            "    startNode: n,\n" +
            "    relationshipWeightProperty: 'time',\n" +
            "\tdelta: 3.0\n" +
            "})\n" +
            "YIELD nodeId, distance\n" +
            "RETURN gds.util.asNode(nodeId).name AS destination, distance, gds.util.asNode(nodeId).zone As zone")
    List<SingleSourceShortestPathResult> distanceToAllStations(@Param("name") String name);

    @Query("MATCH (start:Station {name: $firstStation}), (end:Station {name: $secondStation})\n" +
            "CALL gds.alpha.shortestPath.stream({\n" +
            "  nodeProjection: 'Station',\n" +
            "  relationshipProjection: {\n" +
            "    CONNECTS: {\n" +
            "      type: 'CONNECTS',\n" +
            "      properties: ['time','line']\n" +
            "    }\n" +
            "  },\n" +
            "  startNode: start,\n" +
            "  endNode: end,\n" +
            "  relationshipWeightProperty: 'time'\n" +
            "})\n" +
            "YIELD nodeId, cost\n" +
            "RETURN gds.util.asNode(nodeId).name AS name, cost")
    List<ShortestPathResult> detailedJourneyBetween(@Param("firstStation") String firstStation, @Param("secondStation") String secondStation);

}
