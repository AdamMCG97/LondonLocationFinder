package tech.amcg.llf.repository;

import org.neo4j.driver.internal.InternalPath;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.data.neo4j.repository.support.SimpleNeo4jRepository;
import org.springframework.stereotype.Repository;
import tech.amcg.llf.domain.neo4j.Path;
import tech.amcg.llf.domain.neo4j.Station;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
// Class instead of interface
// Extend [SimpleNeo4jRepository] instead of [Neo4jRepository]
public class PathRepository extends SimpleNeo4jRepository<Station, Long> {
    private static final String GET_ALL_PATHS_QUERY ="MATCH (start:Station{name: $firstStation}),(end:Station{name: $secondStation}),\n" +
            "p = shortestPath((start)-[*..]-(end)) \n" +
            " RETURN p";
        // Needed to be able to query the database
    private final Session session;
        // Inject in the session
        // No need to create the session yourself, Spring has already created it
    public PathRepository(Session session) {
        super(Station.class, session);
        this.session = session;
    }
    public List<Path> getAllPaths(String start, String end) {
        Map<String, String> parameters = Map.of(
                "firstStation", start,
                "secondStation", end
        );
        // Execute the query and retrieve the result
        Result rows = session.query(GET_ALL_PATHS_QUERY, parameters);
        List<Path> results = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            results.add(convertRow(row));
        }
        return results;
    }
    private Path convertRow(Map<String, Object> row) {
        LinkedHashMap<String, Object> lhm = (LinkedHashMap<String, Object>) row.get("p");

        InternalPath.SelfContainedSegment[] connections =
        (InternalPath.SelfContainedSegment[]) row.get("p");
        List<Station> stations = new ArrayList<>();
        // Iterate through the segments in the path
        for (InternalPath.SelfContainedSegment connection : connections) {
            stations.add(convert(connection));
        }
        double distance = (Double) row.get("distance");
        return new Path(stations);
    }
    private Station convert(InternalPath.SelfContainedSegment connection) {
        // Extract the information about the [City] from the path segment
        // Information about the start node and the relationship could also be accessed
        return new Station(
                connection.end().id(),
                connection.end().get("name").asString(),
                connection.end().get("latitude").asDouble(),
                connection.end().get("longitude").asDouble(),
                connection.end().get("total_lines").asLong(),
                connection.end().get("zone").asDouble(),
                connection.end().get("rail").asLong(),
                connection.end().get("stationID").asLong()
        );
    }
}