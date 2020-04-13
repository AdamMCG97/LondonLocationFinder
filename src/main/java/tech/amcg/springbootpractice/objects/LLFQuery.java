package tech.amcg.springbootpractice.objects;

import java.util.List;

public class LLFQuery {

    private List<LLFPerson> personParamsList;

    private String modeOfTransport;

    private int numberOfBedrooms;

    private int lowerBoundPriceRange;

    private int upperBoundPriceRange;

    private boolean differentCommuteMaximums;

    public LLFQuery(){}

    public LLFQuery(List<LLFPerson> personParamsList, String modeOfTransport, int numberOfBedrooms, int lowerBoundPriceRange, int upperBoundPriceRange, boolean differentCommuteMaximums) {
        this.personParamsList = personParamsList;
        this.modeOfTransport = modeOfTransport;
        this.numberOfBedrooms = numberOfBedrooms;
        this.lowerBoundPriceRange = lowerBoundPriceRange;
        this.upperBoundPriceRange = upperBoundPriceRange;
        this.differentCommuteMaximums = differentCommuteMaximums;
    }

    public List<LLFPerson> getPersonParamsList() {
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
