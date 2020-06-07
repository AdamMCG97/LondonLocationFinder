package tech.amcg.llf.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.amcg.llf.domain.mapping.LocationCandidate;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.Query;
import tech.amcg.llf.domain.Response;
import tech.amcg.llf.domain.query.Station;
import tech.amcg.llf.domain.neo4j.AllStationsResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LocationFindingService {

    @Autowired
    Neo4JRepositoryService neo4JRepositoryService;

    LocationFindingService(){}

    public Response findLocations(Query query) {
        query.getPersonParamsList().forEach(this::findLocationsForPerson);

        return new Response(findMatchingLocations(query.getPersonParamsList()));
    }

    private void findLocationsForPerson(Person person){
        Map<String, Double> acceptableStations = new HashMap<>();
        Station nearestStation = person.getNearestStations().get(0);

        List<AllStationsResult> results = neo4JRepositoryService.distanceToAllStations(nearestStation.getName());

        results.removeIf(result -> result.getDistance() + nearestStation.getWalkTime() > person.getMaximumCommuteTime() );

        results.forEach(result -> acceptableStations.put(result.getDestination(), result.getDistance()));

        person.setSolutionCandidates(acceptableStations);
    }

    private List<LocationCandidate> findMatchingLocations(List<Person> personList) {
        List<LocationCandidate> matchingList = new ArrayList<>();

        for(String station : personList.get(0).getSolutionCandidates().keySet()) {
            List<Double> travelTimeList = new ArrayList<>();
            var ref = new Object() {
                boolean optionForAllPeople = true;
            };
            personList.forEach(person -> {
                ref.optionForAllPeople = ref.optionForAllPeople && person.getSolutionCandidates().containsKey(station);
                if(ref.optionForAllPeople) {
                    travelTimeList.add(person.getSolutionCandidates().get(station));
                }
            });

            if(ref.optionForAllPeople) {
                matchingList.add(new LocationCandidate(travelTimeList ,station));
            }
        }

        return matchingList;
    }
}
