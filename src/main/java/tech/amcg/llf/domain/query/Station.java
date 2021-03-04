package tech.amcg.llf.domain.query;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Station implements Location {
    private String name;

    private String latitude;

    private String longitude;

    private int walkTime;

    private String naptanCode;

    public Station(String name){
        this.name = name;
    }

    public Station(String name, String latitude, String longitude, String naptanCode) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.naptanCode = naptanCode;
    }
}
