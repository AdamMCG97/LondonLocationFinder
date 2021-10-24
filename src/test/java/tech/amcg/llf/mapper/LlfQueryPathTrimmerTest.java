package tech.amcg.llf.mapper;

import org.junit.Test;
import tech.amcg.llf.domain.neo4j.LineDataResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LlfQueryPathTrimmerTest {

    QueryPathTrimmer queryPathTrimmer = new QueryPathTrimmer();

    List<LineDataResult> lineData = Arrays.asList(
            LineDataResult.builder()
                    .line(3L)
                    .time(3L)
                    .startNodeName("Paddington")
                    .endNodeName("Edgware Road (C)")
                    .build(),
            LineDataResult.builder()
                    .line(6L)
                    .time(4L)
                    .startNodeName("Paddington")
                    .endNodeName("Edgware Road (C)")
                    .build(),
            LineDataResult.builder()
                    .line(4L)
                    .time(3L)
                    .startNodeName("Paddington")
                    .endNodeName("Edgware Road (C)")
                    .build(),
            LineDataResult.builder()
                    .line(6L)
                    .time(3L)
                    .startNodeName("Edgware Road (C)")
                    .endNodeName("Baker Street")
                    .build(),
            LineDataResult.builder()
                    .line(3L)
                    .time(3L)
                    .startNodeName("Edgware Road (C)")
                    .endNodeName("Baker Street")
                    .build(),
            LineDataResult.builder()
                    .line(8L)
                    .time(6L)
                    .startNodeName("Baker Street")
                    .endNodeName("Finchley Road")
                    .build(),
            LineDataResult.builder()
                    .line(7L)
                    .time(1L)
                    .startNodeName("Finchley Road")
                    .endNodeName("West Hampstead")
                    .build(),
            LineDataResult.builder()
                    .line(7L)
                    .time(2L)
                    .startNodeName("West Hampstead")
                    .endNodeName("Kilburn")
                    .build(),
            LineDataResult.builder()
                    .line(7L)
                    .time(2L)
                    .startNodeName("Kilburn")
                    .endNodeName("Willesden Green")
                    .build(),
            LineDataResult.builder()
                    .line(7L)
                    .time(2L)
                    .startNodeName("Willesden Green")
                    .endNodeName("Dollis Hill")
                    .build()
    );

    @Test
    public void testDuplicateStepsAreTrimmedFromLineData() {
        //given we have a path result with duplicate steps
        SingleSourceShortestPathResult duplicateSteps = SingleSourceShortestPathResult.builder()
                .sourceNodeName("Paddington")
                .targetNodeName("Dollis Hill")
                .zone(3.0)
                .totalCost(19.0)
                .lineData(new ArrayList<>(lineData))
                .nodeNames(new ArrayList<>(Arrays.asList("Paddington", "Edgware Road (C)", "Baker Street", "Finchley Road", "West Hampstead", "Kilburn", "Willesden Green", "Dollis Hill")))
                .costs(Arrays.asList(0.0, 3.0, 6.0, 12.0, 13.0, 15.0, 17.0, 19.0))
                .build();

        assertEquals(10, duplicateSteps.getLineData().size());

        //when we trim the path
        SingleSourceShortestPathResult trimmedResult = queryPathTrimmer.trim(duplicateSteps);

        //no duplicate steps are remaining
        assertEquals(7, trimmedResult.getLineData().size());

        String expected = "SingleSourceShortestPathResult(index=0, sourceNodeName=Paddington, targetNodeName=Dollis Hill, zone=3.0, totalCost=19.0, lineData=[LineDataResult(line=3, time=3, startNodeName=Paddington, endNodeName=Edgware Road (C)), LineDataResult(line=3, time=3, startNodeName=Edgware Road (C), endNodeName=Baker Street), LineDataResult(line=8, time=6, startNodeName=Baker Street, endNodeName=Finchley Road), LineDataResult(line=7, time=1, startNodeName=Finchley Road, endNodeName=West Hampstead), LineDataResult(line=7, time=2, startNodeName=West Hampstead, endNodeName=Kilburn), LineDataResult(line=7, time=2, startNodeName=Kilburn, endNodeName=Willesden Green), LineDataResult(line=7, time=2, startNodeName=Willesden Green, endNodeName=Dollis Hill)], nodeNames=[Paddington, Edgware Road (C), Baker Street, Finchley Road, West Hampstead, Kilburn, Willesden Green], costs=[0.0, 3.0, 6.0, 12.0, 13.0, 15.0, 17.0, 19.0])";

        assertEquals(expected, trimmedResult.toString());
    }


}
