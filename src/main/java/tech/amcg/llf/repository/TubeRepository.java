package tech.amcg.llf.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.amcg.llf.domain.neo4j.ShortestPathResult;
import tech.amcg.llf.domain.neo4j.LegacySingleSourceShortestPathResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResultDto;
import tech.amcg.llf.domain.neo4j.Tube;

import java.util.List;

@Repository
public interface TubeRepository extends Neo4jRepository<Tube, Long> {

    @Deprecated
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
    List<LegacySingleSourceShortestPathResult> distanceToAllStations(@Param("name") String name);

    @Query("MATCH (source:Station {name: $name})\n" +
            "CALL gds.beta.allShortestPaths.dijkstra.stream('tubeGraph', {\n" +
            "    sourceNode: id(source),\n" +
            "    relationshipWeightProperty: 'time'\n" +
            "})\n" +
            "YIELD index, sourceNode, targetNode, totalCost, nodeIds, costs\n" +
            "WHERE NOT apoc.coll.contains($zones, gds.util.asNode(targetNode).zone) AND totalCost < $maxTime\n" +
            "CALL {\n" +
            "WITH nodeIds\n" +
            "UNWIND apoc.coll.zip(apoc.coll.remove(nodeIds, apoc.coll.indexOf(nodeIds, last(nodeIds)), apoc.coll.indexOf(nodeIds, last(nodeIds))), tail(nodeIds)) as zippedIds\n" +
            "UNWIND gds.util.asNode(zippedIds[0]) as startNode\n" +
            "UNWIND gds.util.asNode(zippedIds[1]) as endNode\n" +
            "MATCH (startNode)-[r]->(endNode)\n" +
            "RETURN collect([r.line, r.time, startNode.name, endNode.name]) as lineData\n" +
            "}\n" +
            "RETURN\n" +
            "    index,\n" +
            "    gds.util.asNode(sourceNode).name AS sourceNodeName,\n" +
            "    gds.util.asNode(targetNode).name AS targetNodeName,\n" +
            "    gds.util.asNode(targetNode).zone as zone,\n" +
            "    totalCost,\n" +
            "    lineData,\n" +
            "    [nodeId IN nodeIds | gds.util.asNode(nodeId).name] AS nodeNames,\n" +
            "    costs\n" +
            "ORDER BY index;")
    List<SingleSourceShortestPathResultDto> dijkstraDistanceToAllStations(@Param("name") String name, @Param("zones") List<Double> zones, @Param("maxTime") Integer maxTime);

    @Query("MATCH (source:Station {name: $name})\n" +
            "CALL gds.beta.allShortestPaths.dijkstra.stream('tubeGraph', {\n" +
            "    sourceNode: id(source),\n" +
            "    relationshipWeightProperty: 'time'\n" +
            "})\n" +
            "YIELD index, sourceNode, targetNode, totalCost, nodeIds, costs\n" +
            "CALL {\n" +
            "WITH nodeIds\n" +
            "UNWIND apoc.coll.zip(apoc.coll.remove(nodeIds, apoc.coll.indexOf(nodeIds, last(nodeIds)), apoc.coll.indexOf(nodeIds, last(nodeIds))), tail(nodeIds)) as zippedIds\n" +
            "UNWIND gds.util.asNode(zippedIds[0]) as startNode\n" +
            "UNWIND gds.util.asNode(zippedIds[1]) as endNode\n" +
            "MATCH (startNode)-[r]->(endNode)\n" +
            "RETURN collect([r.line, r.time, startNode.name, endNode.name]) as lineData\n" +
            "}\n" +
            "RETURN\n" +
            "    index,\n" +
            "    gds.util.asNode(sourceNode).name AS sourceNodeName,\n" +
            "    gds.util.asNode(targetNode).name AS targetNodeName,\n" +
            "    gds.util.asNode(targetNode).zone as zone,\n" +
            "    totalCost,\n" +
            "    lineData,\n" +
            "    [nodeId IN nodeIds | gds.util.asNode(nodeId).name] AS nodeNames,\n" +
            "    costs\n" +
            "ORDER BY index;")
    List<SingleSourceShortestPathResultDto> dijkstraDistanceToAllStations(@Param("name") String name);

    @Query("MATCH (source:Station {name: $firstStation}), (target:Station {name: $secondStation})\n" +
            "CALL gds.beta.shortestPath.dijkstra.stream('tubeGraph', { \n" +
            "    sourceNode: id(source),\n" +
            "    targetNode: id(target),\n" +
            "    relationshipWeightProperty: 'time'\n" +
            "})\n" +
            "YIELD index, sourceNode, targetNode, totalCost, nodeIds, costs\n" +
            "RETURN\n" +
            "    index,\n" +
            "    gds.util.asNode(sourceNode).name AS sourceNodeName,\n" +
            "    gds.util.asNode(targetNode).name AS targetNodeName,\n" +
            "    totalCost,\n" +
            "    [nodeId IN nodeIds | gds.util.asNode(nodeId).name] AS nodeNames,\n" +
            "    costs\n" +
            "ORDER BY index")
    List<ShortestPathResult> dijkstraDetailedJourneyBetween(@Param("firstStation") String firstStation, @Param("secondStation") String secondStation);

    @Query("MATCH (source:Station {name: $firstStation}), (target:Station {name: $secondStation})\n" +
            "CALL gds.beta.shortestPath.dijkstra.stream('tubeGraph', { \n" +
            "    sourceNode: id(source),\n" +
            "    targetNode: id(target),\n" +
            "    relationshipWeightProperty: 'line'\n" +
            "})\n" +
            "YIELD index, sourceNode, targetNode, totalCost, nodeIds, costs\n" +
            "RETURN\n" +
            "    index,\n" +
            "    gds.util.asNode(sourceNode).name AS sourceNodeName,\n" +
            "    gds.util.asNode(targetNode).name AS targetNodeName,\n" +
            "    totalCost,\n" +
            "    [nodeId IN nodeIds | gds.util.asNode(nodeId).name] AS nodeNames,\n" +
            "    costs\n" +
            "ORDER BY index")
    List<ShortestPathResult> dijkstraDetailedJourneyByTubeLine(@Param("firstStation") String firstStation, @Param("secondStation") String secondStation);

}
