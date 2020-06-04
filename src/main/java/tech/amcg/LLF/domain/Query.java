package tech.amcg.llf.domain;

import java.util.List;

public class Query {

    private List<Person> personParamsList;

    private String modeOfTransport;

    private int numberOfBedrooms;

    private int lowerBoundPriceRange;

    private int upperBoundPriceRange;

    private boolean differentCommuteMaximums;

    public Query(){}

    public Query(List<Person> personParamsList, String modeOfTransport, int numberOfBedrooms, int lowerBoundPriceRange, int upperBoundPriceRange, boolean differentCommuteMaximums) {
        this.personParamsList = personParamsList;
        this.modeOfTransport = modeOfTransport;
        this.numberOfBedrooms = numberOfBedrooms;
        this.lowerBoundPriceRange = lowerBoundPriceRange;
        this.upperBoundPriceRange = upperBoundPriceRange;
        this.differentCommuteMaximums = differentCommuteMaximums;
    }

    public List<Person> getPersonParamsList() {
        return personParamsList;
    }

    public String getModeOfTransport() {
        return modeOfTransport;
    }

    public int getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    public int getLowerBoundPriceRange() {
        return lowerBoundPriceRange;
    }

    public int getUpperBoundPriceRange() {
        return upperBoundPriceRange;
    }

    public boolean hasDifferentCommuteMaximums() {
        return differentCommuteMaximums;
    }
}
