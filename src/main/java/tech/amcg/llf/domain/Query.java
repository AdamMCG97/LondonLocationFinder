package tech.amcg.llf.domain;

import tech.amcg.llf.domain.query.Person;

import java.util.List;

public class Query {

    private List<Person> personParamsList;

    private List<Double> exclusionZones;

    private int numberOfBedrooms;

    private int lowerBoundPriceRange;

    private int upperBoundPriceRange;

    private boolean differentCommuteMaximums;

    public Query(){}

    public Query(List<Person> personParamsList, int numberOfBedrooms, int lowerBoundPriceRange, int upperBoundPriceRange, boolean differentCommuteMaximums, List<Double> exclusionZones) {
        this.personParamsList = personParamsList;
        this.numberOfBedrooms = numberOfBedrooms;
        this.lowerBoundPriceRange = lowerBoundPriceRange;
        this.upperBoundPriceRange = upperBoundPriceRange;
        this.differentCommuteMaximums = differentCommuteMaximums;
        this.exclusionZones = exclusionZones;
    }

    public List<Person> getPersonParamsList() {
        return personParamsList;
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

    public List<Double> getExclusionZones() {
        return exclusionZones;
    }
}
