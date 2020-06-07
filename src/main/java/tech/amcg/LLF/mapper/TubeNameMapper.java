package tech.amcg.llf.mapper;

import tech.amcg.llf.domain.query.Station;

public class TubeNameMapper {

    private static final String EDGWARE = "Edgware Road";
    private static final String OLYMPIA = "Olympia";
    private static final String KENSINGTON_OLYMPIA = "Kensington (Olympia)";
    private static final String DLR = "DLR";
    private static final String OPENING_BRACKET = "(";
    private static final String CLOSING_BRACKET = ")";

    public TubeNameMapper() {}

    public void map(Station station) {
        String name = station.getName();

        if(name.contains(OLYMPIA)) {
            station.setName(KENSINGTON_OLYMPIA);
            return;
        }

        if(name.contains(OPENING_BRACKET)) {
            if(name.contains(EDGWARE)) {
                station.setName(reduceLineToInitials(name));
            }
            else {
                station.setName(name.substring(0, name.indexOf("(") - 1));
            }
        }
        else if (name.contains(DLR)) {
            station.setName(name.substring(0, name.indexOf(DLR) - 1));
        }
    }

    String reduceLineToInitials(String name) {
        int firstBracket = name.indexOf(OPENING_BRACKET);
        return name.substring(0, firstBracket + 2) + CLOSING_BRACKET;
    }
}
