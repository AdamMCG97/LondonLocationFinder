package tech.amcg.llf.domain.query;

public class Station implements Location {
    private String name;

    private String latitude;

    private String longitude;

    private int walkTime;

    private String naptanCode;

    private String line;

    public Station(){}

    public Station(String name){
        this.name = name;
    }

    public Station(String name, String latitude, String longitude, String naptanCode) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.naptanCode = naptanCode;
    }

    public String getName() {
        return name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getNaptanCode() {
        return naptanCode;
    }

    public String getLine() {
        return line;
    }

    public int getWalkTime() {
        return walkTime;
    }

    public void setWalkTime(int walkTime) {
        this.walkTime = walkTime;
    }
}
