package edu.baylor.miniapp;


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
 * Midterm 01
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

        assertThat(jsonAsList.size()).isEqualTo(5);

        Map<String, ?> record1 = jsonAsList.get(0);
        assertThat(record1.get("id")).isEqualTo(1);
        assertThat(record1.get("name")).isEqualTo("person1");
        assertThat(record1.get("version")).isEqualTo(1);
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
        assertThat(person.getVersion()).isEqualTo(1);
    }

    @Test
    public void cCreatePerson() {
        Person newPerson = new Person();
        newPerson.setName("newPerson");
        newPerson.setDob("01/30/1995");

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(newPerson)
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

        assertThat(jsonAsList.size()).isEqualTo(6);

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
        assertThat(person.getName()).isEqualTo("newPerson");
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

        // now update the name
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
    public void hFailToRemoveAssignedPerson() {
        // assign person 2 as leader of team 1
        Integer personId = 2;
        Integer teamId = 1;

        Response addLeaderResponse =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("teamId", teamId)
                        .pathParam("personId", personId)
                        .when()
                        .get("/app/teams/{teamId}/leader/{personId}");

        assertThat(addLeaderResponse).isNotNull();
        assertThat(addLeaderResponse.getStatusCode()).isEqualTo(200);

        // remove person with id 2
        // it should throw error

        Response deleteResponse =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("personId", personId)
                        .when()
                        .delete("/app/persons/{personId}");

        assertThat(deleteResponse).isNotNull();
        assertThat(deleteResponse.getStatusCode()).isEqualTo(500);
        assertThat(deleteResponse.getBody().asString()).contains("Can't remove Person assigned to a team");
    }

}
