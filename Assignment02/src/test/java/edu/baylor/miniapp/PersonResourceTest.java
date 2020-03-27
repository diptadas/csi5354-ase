package edu.baylor.miniapp;


import edu.baylor.miniapp.model.Car;
import edu.baylor.miniapp.model.Person;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.wildfly.swarm.arquillian.DefaultDeployment;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.fest.assertions.Assertions.assertThat;

/**
 * Assignment 02
 *
 * @author Dipta Das
 */
@RunWith(Arquillian.class)
@DefaultDeployment
@RunAsClient
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PersonResourceTest {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void aRetrieveAllPersons() {
        Response response =
                when()
                        .get("/app/persons")
                        .then()
                        .extract().response();

        String jsonAsString = response.asString();
        List<Map<String, ?>> jsonAsList = JsonPath.from(jsonAsString).getList("");

        assertThat(jsonAsList.size()).isEqualTo(2);

        Map<String, ?> record1 = jsonAsList.get(0);

        assertThat(record1.get("id")).isEqualTo(1);
        assertThat(record1.get("name")).isEqualTo("person1");
        assertThat(record1.get("age")).isEqualTo(20);
        assertThat(record1.get("email")).isEqualTo("p1@email.com");
        assertThat(record1.get("version")).isEqualTo(1);

        Map<String, ?> record2 = jsonAsList.get(1);

        assertThat(record2.get("id")).isEqualTo(2);
        assertThat(record2.get("name")).isEqualTo("person2");
        assertThat(record2.get("age")).isEqualTo(21);
        assertThat(record2.get("email")).isEqualTo("p2@email.com");
        assertThat(record2.get("version")).isEqualTo(1);

    }

    @Test
    public void bRetrievePerson() {
        Response response =
                given()
                        .pathParam("personId", 1)
                        .when()
                        .get("/app/persons/{personId}")
                        .then()
                        .extract().response();

        String jsonAsString = response.asString();

        Person person = JsonPath.from(jsonAsString).getObject("", Person.class);

        assertThat(person.getId()).isEqualTo(1);
        assertThat(person.getName()).isEqualTo("person1");
        assertThat(person.getAge()).isEqualTo(20);
        assertThat(person.getEmail()).isEqualTo("p1@email.com");
        assertThat(person.getVersion()).isEqualTo(1);
    }

    @Test
    public void bbRetrievePersonCars() {
        Response response =
                given()
                        .pathParam("personId", 1)
                        .when()
                        .get("/app/persons/{personId}/cars")
                        .then()
                        .extract().response();

        String jsonAsString = response.asString();

        List<Car> cars = JsonPath.from(jsonAsString).getList("", Car.class);

        assertThat(cars).hasSize(2);

        assertThat(cars.get(0).getId()).isEqualTo(2);
        assertThat(cars.get(0).getOwner()).isNotNull();
        assertThat(cars.get(0).getBrand()).isEqualTo("brand2");
        assertThat(cars.get(0).getType()).isEqualTo("type2");
        assertThat(cars.get(0).getLicense()).isEqualTo("license2");
        assertThat(cars.get(0).getVersion()).isEqualTo(1);

        assertThat(cars.get(1).getId()).isEqualTo(3);
        assertThat(cars.get(1).getOwner()).isNotNull();
        assertThat(cars.get(1).getBrand()).isEqualTo("brand3");
        assertThat(cars.get(1).getType()).isEqualTo("type3");
        assertThat(cars.get(1).getLicense()).isEqualTo("license3");
        assertThat(cars.get(1).getVersion()).isEqualTo(1);
    }

    @Test
    public void cCreatePerson() {
        Person person3 = new Person(null, "person3", 22, "p3@email.com", 1);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(person3)
                        .when()
                        .post("/app/persons");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(201);
        String locationUrl = response.getHeader("Location");
        Integer personId = Integer.valueOf(locationUrl.substring(locationUrl.lastIndexOf('/') + 1));

        response =
                when()
                        .get("/app/persons")
                        .then()
                        .extract().response();

        String jsonAsString = response.asString();
        List<Map<String, ?>> jsonAsList = JsonPath.from(jsonAsString).getList("");

        assertThat(jsonAsList.size()).isEqualTo(3);

        response =
                given()
                        .pathParam("personId", personId)
                        .when()
                        .get("/app/persons/{personId}")
                        .then()
                        .extract().response();

        jsonAsString = response.asString();

        Person person = JsonPath.from(jsonAsString).getObject("", Person.class);

        assertThat(person.getId()).isEqualTo(personId);
        assertThat(person.getName()).isEqualTo("person3");
        assertThat(person.getAge()).isEqualTo(22);
        assertThat(person.getEmail()).isEqualTo("p3@email.com");
        assertThat(person.getVersion()).isEqualTo(1);
    }

    @Test
    public void dFailToCreatePersonFromNullName() {
        Person badPerson = new Person(null, null, 24, "p4@email.com", 1);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(badPerson)
                        .when()
                        .post("/app/persons");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getBody().asString()).contains("Validation failed for classes [edu.baylor.miniapp.model.Person] during persist time for groups [javax.validation.groups.Default, ]\n" +
                "List of constraint violations:[\n" +
                "\tConstraintViolationImpl{interpolatedMessage='must not be null', propertyPath=name, rootBeanClass=class edu.baylor.miniapp.model.Person, messageTemplate='{javax.validation.constraints.NotNull.message}'}\n" +
                "]");

        response =
                when()
                        .get("/app/persons")
                        .then()
                        .extract().response();

        String jsonAsString = response.asString();
        List<Map<String, ?>> jsonAsList = JsonPath.from(jsonAsString).getList("");

        assertThat(jsonAsList.size()).isEqualTo(3); // 2 initial + 1 created in previous cCreatePerson()
    }

    @Test
    public void eUpdatePerson() {
        // get person with id 1
        Integer personId = 1;
        Response response =
                given()
                        .pathParam("personId", personId)
                        .when()
                        .get("/app/persons/{personId}")
                        .then()
                        .extract().response();

        Person person = JsonPath.from(response.asString()).getObject("", Person.class);

        // now update the brand
        person.setName("newName");

        Response updateResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(person)
                        .pathParam("personId", personId)
                        .when()
                        .put("/app/persons/{personId}")
                        .then()
                        .extract().response();

        Person updatedPerson = JsonPath.from(updateResponse.asString()).getObject("", Person.class);

        assertThat(updatedPerson.getName()).isEqualTo("newName");
    }

    @Test
    public void fFailToUpdatePersonIdNotExists() {
        // try to update person with non existing id 0
        // it should give 404 error response
        Integer personId = 0;
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("personId", personId)
                        .when()
                        .put("/app/persons/{personId}");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(404);
        assertThat(response.getBody().asString()).contains("Person with id of " + personId + " does not exist.");
    }

    @Test
    public void gRemovePerson() {
        // remove person with id 1
        Integer personId = 1;
        Response deleteResponse =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("personId", personId)
                        .when()
                        .delete("/app/persons/{personId}");

        assertThat(deleteResponse).isNotNull();
        assertThat(deleteResponse.getStatusCode()).isEqualTo(204);

        // try to get person with id 1
        // it should give empty response
        Response response =
                given()
                        .pathParam("personId", personId)
                        .when()
                        .get("/app/persons/{personId}")
                        .then()
                        .extract().response();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(204);
        assertThat(response.getBody().asString()).isEmpty();
    }

    @Test
    public void hFailToRemovePersonIdNotExists() {
        // try to remove person with non existing id 0
        // it should give error response
        Integer personId = 0;
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("personId", personId)
                        .when()
                        .delete("/app/persons/{personId}");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getBody().asString()).contains("attempt to create delete event with null entity");
    }
}
