package tech.amcg.springbootpractice.service;

import com.postcode.io.initializers.PostcodeLookup;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import org.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tech.amcg.springbootpractice.objects.LLFLocation;
import tech.amcg.springbootpractice.objects.LLFPerson;
import tech.amcg.springbootpractice.objects.LLFQuery;
import tech.amcg.springbootpractice.objects.LLFResultset;

import java.util.Collections;
import java.util.List;

@Service
public class LLFService {

    public LLFService(){}

    public LLFResultset calculateResults(LLFQuery query) throws Exception{
        LLFLocation postcode = query.getPersonParamsList().get(0).getWorkLocation();
        enrichLocationData(query.getPersonParamsList());
        nearestStationNames(query.getPersonParamsList());
        //query.getPersonParamsList().forEach(person -> System.out.println(person.getWorkLatitude() + "," + person.getWorkLongitude()));
        return new LLFResultset("placeholder");
    }

    private void nearestStationNames(List<LLFPerson> personList){
        personList.forEach(person -> {
            Try.of(() -> findNearestStations(person.getWorkLocation().getLatitude(), person.getWorkLocation().getLongitude()))
                    .onSuccess(person::setNearestStations);
        });
    }

    private List<LLFLocation> findNearestStations(String latitude, String longitude){
        //find nearest station
        WebClient webClient = WebClient.create();

        WebClient.UriSpec<WebClient.RequestBodySpec> request = webClient.method(HttpMethod.GET);

        WebClient.RequestBodySpec uri = webClient.method(HttpMethod.GET).uri("http://transportapi.com/v3/uk/tube/stations/near.json?lat=" + latitude + "&lon=" + longitude + "&page=1&rpp=10&app_id=bf215419&app_key=8a5dcfce3f5a3d38873f95eca9432ab9" );

        String response = uri
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println(response);

        return Collections.singletonList(new LLFLocation());
    }

    private void enrichLocationData(List<LLFPerson> personList) throws Exception{
        personList.forEach(person -> {
            Try.of(() -> getLatAndLong(person.getWorkLocation()))
                    .onSuccess(latAndLong -> person.getWorkLocation().setLatitude(latAndLong._1))
                    .onSuccess(latAndLong -> person.getWorkLocation().setLongitude(latAndLong._2));
        });
    }

    private Tuple2<String, String> getLatAndLong(LLFLocation location) throws Exception{
        JSONObject lookupResult;
        lookupResult = (JSONObject) PostcodeLookup.postcode(location.getPostcode()).asJson().get("result");
        return Tuple.of(lookupResult.get("latitude").toString(), lookupResult.get("longitude").toString());
        //System.out.println(lookupResult);
        //System.out.println(String.format("Latitude: {%s}, Longitude: {%s}", lookupResult.get("latitude"), lookupResult.get("longitude")));
    }
}
