package tech.amcg.llf.mappers;

public class RequestMapper {
    private static final String hereApiKey = "nclHFYxJ5vQjVVBLnq1iOyUHQvVJ39ZGyq2MpyZ4z9U";
    private static final String transportApiKey = "8a5dcfce3f5a3d38873f95eca9432ab9";

    public RequestMapper(){}

    public String getHereApiRequestUrl(String startLat, String startLong, String endLat, String endLong){
        return String.format("https://router.hereapi.com/v8/routes?transportMode=pedestrian&origin=%s,%s&destination=%s,%s&return=summary&apiKey=%s",
                startLat, startLong, endLat, endLong, hereApiKey);
    }

    public String getTransportApiRequestUrl(String lat, String lon){
        return String.format("http://transportapi.com/v3/uk/tube/stations/near.json?lat=%s&lon=%s&page=1&rpp=5&app_id=bf215419&app_key=%s",
                lat, lon, transportApiKey);
    }
}
