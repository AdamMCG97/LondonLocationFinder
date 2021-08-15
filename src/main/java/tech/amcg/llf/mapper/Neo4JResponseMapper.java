package tech.amcg.llf.mapper;

import lombok.NoArgsConstructor;
import tech.amcg.llf.domain.neo4j.LineDataResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResultDto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@NoArgsConstructor
public class Neo4JResponseMapper {

    public SingleSourceShortestPathResult mapFromDtoToSingleSourceShortestPathResult(SingleSourceShortestPathResultDto dto) {
        return SingleSourceShortestPathResult.builder()
                .index(dto.getIndex())
                .costs(dto.getCosts())
                .lineData(mapLineData(dto.getLineData().iterator()))
                .nodeNames(dto.getNodeNames())
                .sourceNodeName(dto.getSourceNodeName())
                .targetNodeName(dto.getTargetNodeName())
                .totalCost(dto.getTotalCost())
                .zone(dto.getZone())
                .build();
    }

    //very odd behaviour, lineData behaves as an Iterable<Object> but won't load as anything other than an Iterable of a list type
    public List<LineDataResult> mapLineData(Iterator<List<Object>> iterator) {
        Object line, time, startNodeName, endNodeName;
        List<LineDataResult> mappedResults = new ArrayList<>();
        while(iterator.hasNext()) {
            line = iterator.next();
            time = iterator.next();
            startNodeName = iterator.next();
            endNodeName = iterator.next();
            mappedResults.add(
                    LineDataResult.builder()
                            .line((Long) line)
                            .time((Long) time)
                            .startNodeName((String) startNodeName)
                            .endNodeName((String) endNodeName)
                            .build()
            );
        }
        return mappedResults;
    }

}
