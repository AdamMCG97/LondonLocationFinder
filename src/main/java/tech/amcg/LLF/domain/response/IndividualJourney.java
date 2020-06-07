package tech.amcg.llf.domain.response;

import tech.amcg.llf.domain.response.mapping.JourneyDetails;

public class IndividualJourney {

    private String personID;

    private String workPostcode;

    private Double travelTime;

    private JourneyDetails journeyDetails;

    public IndividualJourney(String personID, String workPostcode, Double travelTime) {
        this.personID = personID;
        this.workPostcode = workPostcode;
        this.travelTime = travelTime;
    }

    public String getPersonID() {
        return personID;
    }

    public String getWorkPostcode() {
        return workPostcode;
    }

    public Double getTravelTime() {
        return travelTime;
    }
}
