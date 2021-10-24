package tech.amcg.llf.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class LlfResult {

    private final String name;

    private final List<IndividualJourney> individualJourneys;

    private final Double averageTravelTime;

    private final Double maximumTravelTime;

    private final Double zone;

}
