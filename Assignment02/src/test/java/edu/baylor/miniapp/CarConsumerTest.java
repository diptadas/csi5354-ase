package edu.baylor.miniapp;

import au.com.dius.pact.consumer.ConsumerPactTestMk2;
import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslResponse;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactSpecVersion;
import au.com.dius.pact.model.RequestResponsePact;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;
import edu.baylor.miniapp.client.CarClient;
import edu.baylor.miniapp.model.Car;
import org.apache.http.client.fluent.Response;
import org.junit.Before;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static edu.baylor.miniapp.Helpers.assertForCars;
import static edu.baylor.miniapp.Helpers.newCar;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Assignment 02
 *
 * @author Dipta Das
 */
public class CarConsumerTest extends ConsumerPactTestMk2 {
    Car car1, car2, car3;
    List<Car> cars;

    @Before
    public void init() {
        car1 = newCar(101);
        car2 = newCar(102, 1);
        car3 = newCar(103, 1);
        cars = Lists.newArrayList(car1, car2, car3);
    }

    @Override
    protected RequestResponsePact createPact(PactDslWithProvider builder) {

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        try {
            // get specific
            PactDslResponse pactDslResponse = builder
                    .uponReceiving("Retrieve a car")
                    .path("/app/cars/" + car1.getId())
                    .method("GET")
                    .willRespondWith()
                    .status(200)
                    .body(mapper.writeValueAsString(car1));

            // get all
            pactDslResponse = pactDslResponse
                    .uponReceiving("Retrieve all cars")
                    .path("/app/cars")
                    .method("GET")
                    .willRespondWith()
                    .status(200)
                    .body(mapper.writeValueAsString(cars)); // write all cars

            // create
            pactDslResponse = pactDslResponse
                    .uponReceiving("Creating a car")
                    .path("/app/cars")
                    .method("POST")
                    .body(mapper.writeValueAsString(car2)) // receive "car2" in body
                    .willRespondWith()
                    .status(200)
                    .body(mapper.writeValueAsString(car2)); // return the same "car2"

            // update
            pactDslResponse = pactDslResponse
                    .uponReceiving("Updating a car")
                    .path("/app/cars/" + car3.getId()) // update "car3"
                    .method("PUT")
                    .body(mapper.writeValueAsString(car3)) // receive "car3" in body
                    .willRespondWith()
                    .status(200)
                    .body(mapper.writeValueAsString(car3)); // return the same "car3"

            // remove
            pactDslResponse = pactDslResponse
                    .uponReceiving("Delete a car")
                    .path("/app/cars/" + car1.getId()) // delete "car1"
                    .method("DELETE")
                    .willRespondWith()
                    .status(204);

            return pactDslResponse.toPact();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected String providerName() {
        return "car_service_provider";
    }

    @Override
    protected String consumerName() {
        return "car_client_consumer";
    }

    @Override
    protected PactSpecVersion getSpecificationVersion() {
        return PactSpecVersion.V3;
    }

    @Override
    protected void runTest(MockServer mockServer) throws IOException {
        CarClient carClient = new CarClient(mockServer.getUrl());

        // get
        Car car = carClient.getCar(car1.getId());
        assertForCars(car, car1);

        // get all
        Collection<Car> allCars = carClient.getAllCars();
        assertThat(allCars).hasSize(cars.size());

        // create
        Car createdCar = carClient.createCar(car2);
        assertForCars(createdCar, car2);

        // update
        Car updateCar = carClient.updateCar(car3);
        assertForCars(updateCar, car3);

        // delete
        Response response = carClient.deleteCar(car1.getId());
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(204);
    }
}
