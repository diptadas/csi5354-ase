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
import edu.baylor.miniapp.client.PersonClient;
import edu.baylor.miniapp.model.Car;
import edu.baylor.miniapp.model.Person;
import org.apache.http.client.fluent.Response;
import org.junit.Before;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static edu.baylor.miniapp.Helpers.*;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Assignment 02
 *
 * @author Dipta Das
 */
public class PersonConsumerTest extends ConsumerPactTestMk2 {
    Person person1, person2, person3;
    List<Person> persons;
    List<Car> person1Cars;

    @Before
    public void init() {
        person1 = newPerson(101);
        person2 = newPerson(102);
        person3 = newPerson(103);
        persons = Lists.newArrayList(person1, person2, person3);
        person1Cars = Lists.newArrayList(newCar(1), newCar(2));
    }

    @Override
    protected RequestResponsePact createPact(PactDslWithProvider builder) {

        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());

        try {
            // get specific
            PactDslResponse pactDslResponse = builder
                    .uponReceiving("Retrieve a person")
                    .path("/app/persons/" + person1.getId())
                    .method("GET")
                    .willRespondWith()
                    .status(200)
                    .body(mapper.writeValueAsString(person1));

            // get all
            pactDslResponse = pactDslResponse
                    .uponReceiving("Retrieve all persons")
                    .path("/app/persons")
                    .method("GET")
                    .willRespondWith()
                    .status(200)
                    .body(mapper.writeValueAsString(persons)); // write all persons

            // create
            pactDslResponse = pactDslResponse
                    .uponReceiving("Creating a person")
                    .path("/app/persons")
                    .method("POST")
                    .body(mapper.writeValueAsString(person2)) // receive "person2" in body
                    .willRespondWith()
                    .status(200)
                    .body(mapper.writeValueAsString(person2)); // return the same "person2"

            // update
            pactDslResponse = pactDslResponse
                    .uponReceiving("Updating a person")
                    .path("/app/persons/" + person3.getId()) // update "person3"
                    .method("PUT")
                    .body(mapper.writeValueAsString(person3)) // receive "person3" in body
                    .willRespondWith()
                    .status(200)
                    .body(mapper.writeValueAsString(person3)); // return the same "person3"

            // remove
            pactDslResponse = pactDslResponse
                    .uponReceiving("Delete a person")
                    .path("/app/persons/" + person1.getId()) // delete "person1"
                    .method("DELETE")
                    .willRespondWith()
                    .status(204);

            // get cars for person
            pactDslResponse = pactDslResponse
                    .uponReceiving("Get cars for person")
                    .path("/app/persons/" + person1.getId() + "/cars") // get cars for "person1"
                    .method("GET")
                    .willRespondWith()
                    .status(200)
                    .body(mapper.writeValueAsString(person1Cars)); // write persons1Cars

            return pactDslResponse.toPact();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected String providerName() {
        return "person_service_provider";
    }

    @Override
    protected String consumerName() {
        return "person_client_consumer";
    }

    @Override
    protected PactSpecVersion getSpecificationVersion() {
        return PactSpecVersion.V3;
    }

    @Override
    protected void runTest(MockServer mockServer) throws IOException {
        PersonClient personClient = new PersonClient(mockServer.getUrl());

        // get
        Person person = personClient.getPerson(person1.getId());
        assertForPersons(person, person1);

        // get all
        Collection<Person> allPersons = personClient.getAllPersons();
        assertThat(allPersons).hasSize(persons.size());

        // create
        Person createdPerson = personClient.createPerson(person2);
        assertForPersons(createdPerson, person2);

        // update
        Person updatePerson = personClient.updatePerson(person3);
        assertForPersons(updatePerson, person3);

        // delete
        Response response = personClient.deletePerson(person1.getId());
        assertThat(response.returnResponse().getStatusLine().getStatusCode()).isEqualTo(204);

        // get cars for person
        Collection<Car> cars = personClient.getCarsForPerson(person1.getId());
        assertThat(cars).hasSize(2);
    }
}
