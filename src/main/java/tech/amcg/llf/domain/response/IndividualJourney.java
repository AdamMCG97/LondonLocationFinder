package tech.amcg.llf.domain.response;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;

import tech.amcg.llf.domain.response.mapping.JourneyDetails;

@Builder
@Getter
@AllArgsConstructor
public class IndividualJourney {

    private final String personID;

    private final String workPostcode;

    private final Double travelTime;

    private final JourneyDetails journeyDetails;

}
