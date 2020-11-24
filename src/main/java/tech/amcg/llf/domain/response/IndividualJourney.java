package tech.amcg.llf.domain.response;

import tech.amcg.llf.domain.response.mapping.JourneyDetails;

public class IndividualJourney {

    private final String personID;

    private final String workPostcode;

    private final Double travelTime;

    private final JourneyDetails journeyDetails;

    public IndividualJourney(String personID, String workPostcode, Double travelTime, JourneyDetails journeyDetails) {
        this.personID = personID;
        this.workPostcode = workPostcode;
        this.travelTime = travelTime;
        this.journeyDetails = journeyDetails;
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

    public JourneyDetails getJourneyDetails() { return journeyDetails; }
}
