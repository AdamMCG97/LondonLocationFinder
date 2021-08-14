package tech.amcg.llf.mapper;

import lombok.NoArgsConstructor;
import tech.amcg.llf.domain.query.Station;

@NoArgsConstructor
public class StationNameMapper {

    private static final String EDGWARE = "Edgware Road";
    private static final String OLYMPIA = "Olympia";
    private static final String KENSINGTON_OLYMPIA = "Kensington (Olympia)";
    private static final String DLR = "DLR";
    private static final String OPENING_BRACKET = "(";
    private static final String CLOSING_BRACKET = ")";

    public void mapTransportApiStationNameToNeo4jStationName(Station station) {
        String name = station.getName();
        station.setName(applyTransformationByStationName(name));
    }

    String applyTransformationByStationName(String name) {
        if(name.contains(OLYMPIA)) {
            return KENSINGTON_OLYMPIA;
        }
        else if(name.contains(EDGWARE)) {
            return reduceLineToInitials(name);
        }
        else if(name.contains(OPENING_BRACKET)) {
            return trimStringFromOccurrence(name, OPENING_BRACKET);
        }
        else if (name.contains(DLR)) {
            return trimStringFromOccurrence(name, DLR);
        }
        else {
            return name;
        }
    }

    String trimStringFromOccurrence(String stringToTrim, String occurrence) {
        return stringToTrim.substring(0, stringToTrim.indexOf(occurrence) - 1);
    }

    String reduceLineToInitials(String name) {
        int firstBracket = name.indexOf(OPENING_BRACKET);
        return name.substring(0, firstBracket + 2) + CLOSING_BRACKET;
    }
}
