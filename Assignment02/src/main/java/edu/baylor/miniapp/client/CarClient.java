package edu.baylor.miniapp.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.baylor.miniapp.model.Car;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

/**
 * Assignment 02
 *
 * @author Dipta Das
 */
public class CarClient {
    private final String url;

    public CarClient(String url) {
        this.url = url;
    }

    public Car getCar(final Integer carId) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/app/cars/" + carId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String jsonResponse =
                Request
                        .Get(uriBuilder.toString())
                        .execute()
                        .returnContent().asString();

        if (jsonResponse.isEmpty()) {
            return null;
        }

        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readValue(jsonResponse, Car.class);
    }

    public Collection<Car> getAllCars() throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/app/cars");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        String jsonResponse =
                Request
                        .Get(uriBuilder.toString())
                        .execute()
                        .returnContent().asString();

        if (jsonResponse.isEmpty()) {
            return null;
        }

        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .readValue(jsonResponse, new TypeReference<Collection<Car>>() {
                });
    }

    public Car createCar(final Car car) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/app/cars");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        String jsonResponse =
                Request
                        .Post(uriBuilder.toString())
                        .bodyString(objectMapper.writeValueAsString(car), ContentType.APPLICATION_JSON)
                        .execute()
                        .returnContent().asString();

        if (jsonResponse.isEmpty()) {
            return null;
        }

        return objectMapper.readValue(jsonResponse, Car.class);
    }

    public Car updateCar(final Car car) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/app/cars/" + car.getId());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        String jsonResponse =
                Request
                        .Put(uriBuilder.toString())
                        .bodyString(objectMapper.writeValueAsString(car), ContentType.APPLICATION_JSON)
                        .execute()
                        .returnContent().asString();

        if (jsonResponse.isEmpty()) {
            return null;
        }

        return objectMapper.readValue(jsonResponse, Car.class);
    }

    public Response deleteCar(final Integer carId) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/app/cars/" + carId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return Request
                .Delete(uriBuilder.toString())
                .execute();
    }
}
