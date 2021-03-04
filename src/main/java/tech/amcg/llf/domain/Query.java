package tech.amcg.llf.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tech.amcg.llf.domain.query.Person;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class Query {

    private List<Person> personParamsList;

    private List<Double> exclusionZones;

    private int numberOfBedrooms;

    private int lowerBoundPriceRange;

    private int upperBoundPriceRange;

    private boolean differentCommuteMaximums;
}
