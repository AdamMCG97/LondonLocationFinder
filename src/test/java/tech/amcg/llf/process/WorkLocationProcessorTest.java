package tech.amcg.llf.process;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import tech.amcg.llf.Application;
import tech.amcg.llf.config.LlfSpringTest;
import tech.amcg.llf.domain.exception.LlfException;
import tech.amcg.llf.domain.query.Person;
import tech.amcg.llf.domain.query.Point;
import tech.amcg.llf.domain.query.WorkLocation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class WorkLocationProcessorTest extends LlfSpringTest {

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    WorkLocationProcessor workLocationProcessor;

    @Value("classpath:PaddingtonTransportApiSampleResponse.json")
    private Resource paddingtonTransportResponseResource;

    @Value("classpath:CanaryWharfTransportApiSampleResponse.json")
    private Resource canaryWharfTransportResponseResource;

    @Before
    public void setUp() {
        wireMockServer.start();
    }

    @Test
    public void getNearestStationNamesWhenTransportApiIsUnresponsive() {

        //given the transport api is unavailable
        String testPerson1TransportApiRequest = "/uk/tube/stations/near.json?lat=51.518885&lon=-0.180416&page=1&rpp=1&app_id=bf215419&app_key=abcde";
        wireMockServerWillRespondWith(testPerson1TransportApiRequest, "", 503);

        List<Person> personList = Collections.singletonList(
                Person.builder()
                        .personID("Test-Person-1")
                        .workLocation(WorkLocation.builder()
                                .postcode("W2 6TT")
                                .point(new Point("51.518885", "-0.180416"))
                                .build())
                        .maximumCommuteTime(40)
                        .maximumWalkTime(15)
                        .build()
        );

        //when we try to find the nearest stations
        Exception exception = assertThrows(LlfException.class, () -> workLocationProcessor.getNearestStationNames(personList));

        //then we wrap the exception
        assertThat(exception.getMessage(), CoreMatchers.startsWith("No Stations found for person: Person(personID=Test-Person-1"));
        //TODO: Add more specific errors for reason stations could not be found
    }

    @Test
    public void getsNearestStationNamesFromLatAndLong() throws LlfException {

        //given transport API will respond to requests with sample data
        String paddingtonTransportResponse = resourceToString(paddingtonTransportResponseResource);
        String canaryWharfTransportResponse = resourceToString(canaryWharfTransportResponseResource);

        String testPerson1TransportApiRequest = "/uk/tube/stations/near.json?lat=51.518885&lon=-0.180416&page=1&rpp=1&app_id=bf215419&app_key=abcde";
        String testPerson2TransportApiRequest = "/uk/tube/stations/near.json?lat=51.504454&lon=-0.017428&page=1&rpp=1&app_id=bf215419&app_key=abcde";

        wireMockServerWillRespondWith(testPerson1TransportApiRequest, paddingtonTransportResponse);
        wireMockServerWillRespondWith(testPerson2TransportApiRequest, canaryWharfTransportResponse);

        List<Person> personList = Arrays.asList(
                        Person.builder()
                                .personID("Test-Person-1")
                                .workLocation(WorkLocation.builder()
                                        .postcode("W2 6TT")
                                        .point(new Point("51.518885", "-0.180416"))
                                        .build())
                                .maximumCommuteTime(40)
                                .maximumWalkTime(15)
                                .build(),
                        Person.builder()
                                .personID("Test-Person-2")
                                .workLocation(WorkLocation.builder()
                                        .postcode("E14 5AH")
                                        .point(new Point("51.504454", "-0.017428"))
                                        .build())
                                .maximumCommuteTime(30)
                                .maximumWalkTime(15)
                                .build()
        );

        //when we query for the nearest stations
        workLocationProcessor.getNearestStationNames(personList);

        //then our model is enriched with the nearest stations for each person
        assertEquals(
                "[Station(name=Paddington (H & C line), point=Point(latitude=51.51847, longitude=-0.17858), walkTime=0), " +
                        "Station(name=Paddington (Circle), point=Point(latitude=51.51819, longitude=-0.1783), walkTime=0), " +
                        "Station(name=Paddington (Bakerloo), point=Point(latitude=51.51659, longitude=-0.17569), walkTime=0), " +
                        "Station(name=Royal Oak, point=Point(latitude=51.51908, longitude=-0.18809), walkTime=0), " +
                        "Station(name=Warwick Avenue, point=Point(latitude=51.52323, longitude=-0.18371), walkTime=0), " +
                        "Station(name=Edgware Road (Bakerloo), point=Point(latitude=51.52058, longitude=-0.16911), walkTime=0), " +
                        "Station(name=Lancaster Gate, point=Point(latitude=51.51173, longitude=-0.17549), walkTime=0), " +
                        "Station(name=Edgware Road (Cir), point=Point(latitude=51.51986, longitude=-0.16783), walkTime=0), " +
                        "Station(name=Bayswater, point=Point(latitude=51.51229, longitude=-0.18794), walkTime=0), " +
                        "Station(name=Queensway, point=Point(latitude=51.51032, longitude=-0.18715), walkTime=0)]",
                personList.get(0).getNearestStations().toString()
        );

        assertEquals(
                "[Station(name=Canary Wharf, point=Point(latitude=51.50329, longitude=-0.01604), walkTime=0), " +
                        "Station(name=Canary Wharf DLR Station, point=Point(latitude=51.50484, longitude=-0.02099), walkTime=0), " +
                        "Station(name=Heron Quays DLR Station, point=Point(latitude=51.50338, longitude=-0.02141), walkTime=0), " +
                        "Station(name=West India Quay DLR Station, point=Point(latitude=51.50703, longitude=-0.0203), walkTime=0), " +
                        "Station(name=Poplar DLR Station, point=Point(latitude=51.50766, longitude=-0.01741), walkTime=0), " +
                        "Station(name=South Quay DLR Station, point=Point(latitude=51.50056, longitude=-0.01903), walkTime=0), " +
                        "Station(name=All Saints DLR Station, point=Point(latitude=51.51104, longitude=-0.01292), walkTime=0), " +
                        "Station(name=Blackwall DLR Station, point=Point(latitude=51.50803, longitude=-0.00723), walkTime=0), " +
                        "Station(name=Westferry DLR Station, point=Point(latitude=51.50974, longitude=-0.0269), walkTime=0), " +
                        "Station(name=Crossharbour & London Arena DLR Station, point=Point(latitude=51.49573, longitude=-0.0146), walkTime=0)]",
                personList.get(1).getNearestStations().toString()
        );
    }

    @After
    public void cleanUp() {
        wireMockServer.stop();
    }

    private void wireMockServerWillRespondWith(String request, String response, int responseCode) {
        wireMockServer.stubFor(
            WireMock.get((request))
                .willReturn(
                        new ResponseDefinitionBuilder()
                                .withStatus(responseCode)
                                .withHeader("Content-Type", "application/json")
                                .withBody(response)
                )
        );
    }

    private void wireMockServerWillRespondWith(String request, String response) {
        wireMockServerWillRespondWith(request, response, 200);
    }
}
