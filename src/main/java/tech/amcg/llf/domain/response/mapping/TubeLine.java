package tech.amcg.llf.domain.response.mapping;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@NoArgsConstructor @AllArgsConstructor @Getter @ToString
public enum TubeLine {
    JUBILEE(7d, "Jubilee Line", "949699", null),
    METROPOLITAN(8d, "Metropolitan Line", "91005A", null),
    HAMMERSMITHANDCITY(6d, "Hammersmith & City Line", "F491A8", null),
    NORTHERN(9d, "Northern Line", "0", null),
    BAKERLOO(1d, "Bakerloo Line", "AE6017", null),
    CENTRAL(2d, "Central Line", "F15B2E", null),
    CIRCLE(3d, "Circle Line", "FFE02B", null),
    DISTRICT(4d, "District Line", "00A166", null),
    VICTORIA(11d, "Victoria Line", "0A9CDA", null),
    OVERGROUND(5d, "Overground", "X", null),
    //ELIZABETH()
    PICCADILLY(10d, "Piccadilly Line", "094FA3", null),
    DLR(13d, "Docklands Light Railway", "00A77E", "FFFFFF"),
    WATERLOOANDCITY(12d, "Waterloo & City Line", "88D0C4", null),
    UNKNOWN();

    private Double id;
    private String fullName;
    private String colour;
    private String stripe;

    public static TubeLine getById(Double id) {
        for(TubeLine line : values()) {
            if(line.id.equals(id)) return line;
        }
        return UNKNOWN;
    }
}


