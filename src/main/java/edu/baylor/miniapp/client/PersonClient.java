package edu.baylor.miniapp.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.baylor.miniapp.model.Car;
import edu.baylor.miniapp.model.Person;
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
public class PersonClient {
    private final String url;

    public PersonClient(String url) {
        this.url = url;
    }

    public Person getPerson(final Integer personId) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/app/persons/" + personId);
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
                .readValue(jsonResponse, Person.class);
    }

    public Collection<Person> getAllPersons() throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/app/persons");
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
                .readValue(jsonResponse, new TypeReference<Collection<Person>>() {
                });
    }

    public Person createPerson(final Person person) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/app/persons");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        String jsonResponse =
                Request
                        .Post(uriBuilder.toString())
                        .bodyString(objectMapper.writeValueAsString(person), ContentType.APPLICATION_JSON)
                        .execute()
                        .returnContent().asString();

        if (jsonResponse.isEmpty()) {
            return null;
        }

        return objectMapper.readValue(jsonResponse, Person.class);
    }

    public Person updatePerson(final Person person) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/app/persons/" + person.getId());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        String jsonResponse =
                Request
                        .Put(uriBuilder.toString())
                        .bodyString(objectMapper.writeValueAsString(person), ContentType.APPLICATION_JSON)
                        .execute()
                        .returnContent().asString();

        if (jsonResponse.isEmpty()) {
            return null;
        }

        return objectMapper.readValue(jsonResponse, Person.class);
    }

    public Response deletePerson(final Integer personId) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/app/persons/" + personId);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return Request
                .Delete(uriBuilder.toString())
                .execute();
    }

    public Collection<Car> getCarsForPerson(final Integer personId) throws IOException {
        URIBuilder uriBuilder;
        try {
            uriBuilder = new URIBuilder(url).setPath("/app/persons/" + personId + "/cars");
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
}
