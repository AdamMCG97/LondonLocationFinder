package tech.amcg.llf.mapper;

import org.junit.Test;
import tech.amcg.llf.domain.query.Station;

import static org.junit.Assert.assertEquals;

public class TubeNameMapperTest {

    TubeNameMapper tubeNameMapper;

    public TubeNameMapperTest() {
        tubeNameMapper = new TubeNameMapper();
    }

    @Test
    public void testReductionOfInitials(){
        String stationName = "Edgware Road (Bakerloo)";

        String afterProcessing = tubeNameMapper.reduceLineToInitials(stationName);

        assertEquals(afterProcessing, "Edgware Road (B)");
    }

    @Test
    public void testRemovalOfDLR(){
        Station dlrStation = new Station("Canary Wharf DLR Station");

        tubeNameMapper.map(dlrStation);

        assertEquals(dlrStation.getName(), "Canary Wharf");

    }

}
