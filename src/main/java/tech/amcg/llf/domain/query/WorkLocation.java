package tech.amcg.llf.domain.query;

public class WorkLocation {
    private String postcode;

    private String latitude;

    private String longitude;

    public WorkLocation(){}

    public WorkLocation(String postcode){
        this.postcode = postcode;
    }

    public WorkLocation(String postcode, String latitude, String longitude) {
        this.postcode = postcode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
