package tech.amcg.llf.mapper;

import org.junit.Test;
import tech.amcg.llf.domain.query.Station;

import static org.junit.Assert.assertEquals;

public class StationNameMapperTest {

    StationNameMapper stationNameMapper = new StationNameMapper();

    @Test
    public void testReductionOfInitials(){
        Station edgwareRoad = new Station("Edgware Road (Bakerloo)");

        stationNameMapper.mapTransportApiStationNameToNeo4jStationName(edgwareRoad);

        assertEquals("Edgware Road (B)", edgwareRoad.getName());
    }

    @Test
    public void testRemovalOfDLR(){
        Station dlrStation = new Station("Canary Wharf DLR Station");

        stationNameMapper.mapTransportApiStationNameToNeo4jStationName(dlrStation);

        assertEquals("Canary Wharf", dlrStation.getName());

    }

}
