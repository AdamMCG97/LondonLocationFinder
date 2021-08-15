package tech.amcg.llf.process;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.neo4j.SingleSourceShortestPathResult;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.response.LLFResult;
import tech.amcg.llf.mapper.ResultsMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@NoArgsConstructor
@Slf4j
public class ResultsProcessor {

    @Autowired
    ResultsMapper resultsMapper;

    public List<LLFResult> generateResults(Query query) {
        List<String> matchingStationsList = findMatchingLocations(query.getPersonParamsList());
        return generateResults(query.getPersonParamsList(), matchingStationsList);
    }

    private List<LLFResult> generateResults(List<Person> personList, List<String> stationsAcceptableToAll) {
        List<LLFResult> resultList = new ArrayList<>();

        for(String station : stationsAcceptableToAll) {
            resultList.add(resultsMapper.mapPathForAll(personList, station));
        }

        return resultList;
    }

    private List<String> findMatchingLocations(List<Person> personList) {
        List<String> matchingList = new ArrayList<>();

        List<String> firstPersonStationsList = personList.get(0).getAcceptablePaths()
                .stream().map(SingleSourceShortestPathResult::getTargetNodeName).collect(Collectors.toList());

        for(String station : firstPersonStationsList) {
            var ref = new Object() {
                boolean optionForAllPeople = true;
            };

            personList.forEach(person -> {
                SingleSourceShortestPathResult journey = findElementInListByString(person.getAcceptablePaths(), station);
                ref.optionForAllPeople = ref.optionForAllPeople && null != journey;
            });

            if(ref.optionForAllPeople) {
                matchingList.add(station);
            }
        }

        return matchingList;
    }

    public static SingleSourceShortestPathResult findElementInListByString(List<SingleSourceShortestPathResult> list, String itemToFind) {
        return IterableUtils.find(list,
                singleSourceShortestPathResult -> itemToFind.equals(singleSourceShortestPathResult.getTargetNodeName())
        );
    }

}
