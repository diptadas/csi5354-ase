package edu.baylor.miniapp;


import edu.baylor.miniapp.model.Car;
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
public class CarResourceTest {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void aRetrieveAllCars() {
        Response response =
                when()
                        .get("/app/cars")
                        .then()
                        .extract().response();

        String jsonAsString = response.asString();
        List<Map<String, ?>> jsonAsList = JsonPath.from(jsonAsString).getList("");

        assertThat(jsonAsList.size()).isEqualTo(3);

        Map<String, ?> record1 = jsonAsList.get(0);

        assertThat(record1.get("id")).isEqualTo(1);
        assertThat(record1.get("owner")).isNull();
        assertThat(record1.get("brand")).isEqualTo("brand1");
        assertThat(record1.get("type")).isEqualTo("type1");
        assertThat(record1.get("license")).isEqualTo("license1");
        assertThat(record1.get("version")).isEqualTo(1);

        Map<String, ?> record2 = jsonAsList.get(1);

        assertThat(record2.get("id")).isEqualTo(2);
        //assertThat(record2.get("owner")).isEqualTo(1);
        assertThat(record2.get("owner")).isNotNull();
        assertThat(record2.get("brand")).isEqualTo("brand2");
        assertThat(record2.get("type")).isEqualTo("type2");
        assertThat(record2.get("license")).isEqualTo("license2");
        assertThat(record2.get("version")).isEqualTo(1);

        Map<String, ?> record3 = jsonAsList.get(2);

        assertThat(record3.get("id")).isEqualTo(3);
        //assertThat(record3.get("owner")).isEqualTo(1);
        assertThat(record3.get("owner")).isNotNull();
        assertThat(record3.get("brand")).isEqualTo("brand3");
        assertThat(record3.get("type")).isEqualTo("type3");
        assertThat(record3.get("license")).isEqualTo("license3");
        assertThat(record3.get("version")).isEqualTo(1);
    }

    @Test
    public void bRetrieveCar() {
        Response response =
                given()
                        .pathParam("carId", 1)
                        .when()
                        .get("/app/cars/{carId}")
                        .then()
                        .extract().response();

        String jsonAsString = response.asString();

        Car car = JsonPath.from(jsonAsString).getObject("", Car.class);

        assertThat(car.getId()).isEqualTo(1);
        assertThat(car.getOwner()).isNull();
        assertThat(car.getBrand()).isEqualTo("brand1");
        assertThat(car.getType()).isEqualTo("type1");
        assertThat(car.getLicense()).isEqualTo("license1");
        assertThat(car.getVersion()).isEqualTo(1);
    }

    @Test
    public void cCreateCar() {
        Car car4 = new Car(null, "brand4", "type4", "license4", null, 1);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(car4)
                        .when()
                        .post("/app/cars");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(201);
        String locationUrl = response.getHeader("Location");
        Integer carId = Integer.valueOf(locationUrl.substring(locationUrl.lastIndexOf('/') + 1));

        response =
                when()
                        .get("/app/cars")
                        .then()
                        .extract().response();

        String jsonAsString = response.asString();
        List<Map<String, ?>> jsonAsList = JsonPath.from(jsonAsString).getList("");

        assertThat(jsonAsList.size()).isEqualTo(4);

        response =
                given()
                        .pathParam("carId", carId)
                        .when()
                        .get("/app/cars/{carId}")
                        .then()
                        .extract().response();

        jsonAsString = response.asString();

        Car car = JsonPath.from(jsonAsString).getObject("", Car.class);

        assertThat(car.getId()).isEqualTo(carId);
        assertThat(car.getOwner()).isNull();
        assertThat(car.getBrand()).isEqualTo("brand4");
        assertThat(car.getType()).isEqualTo("type4");
        assertThat(car.getLicense()).isEqualTo("license4");
        assertThat(car.getVersion()).isEqualTo(1);
    }

    @Test
    public void dFailToCreateCarFromNullBrand() {
        Car badCar = new Car(null, null, "type5", "license5", null, 1);

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(badCar)
                        .when()
                        .post("/app/cars");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getBody().asString()).contains("Validation failed for classes [edu.baylor.miniapp.model.Car] during persist time for groups [javax.validation.groups.Default, ]\n" +
                "List of constraint violations:[\n" +
                "\tConstraintViolationImpl{interpolatedMessage='must not be null', propertyPath=brand, rootBeanClass=class edu.baylor.miniapp.model.Car, messageTemplate='{javax.validation.constraints.NotNull.message}'}\n" +
                "]");

        response =
                when()
                        .get("/app/cars")
                        .then()
                        .extract().response();

        String jsonAsString = response.asString();
        List<Map<String, ?>> jsonAsList = JsonPath.from(jsonAsString).getList("");

        assertThat(jsonAsList.size()).isEqualTo(4); // 3 initial + 1 created in previous cCreateCar()
    }

    @Test
    public void eUpdateCar() {
        // get car with id 1
        Integer carId = 1;
        Response response =
                given()
                        .pathParam("carId", carId)
                        .when()
                        .get("/app/cars/{carId}")
                        .then()
                        .extract().response();

        Car car = JsonPath.from(response.asString()).getObject("", Car.class);

        // now update the brand
        car.setBrand("newBrand");

        Response updateResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(car)
                        .pathParam("carId", carId)
                        .when()
                        .put("/app/cars/{carId}")
                        .then()
                        .extract().response();

        Car updatedCar = JsonPath.from(updateResponse.asString()).getObject("", Car.class);

        assertThat(updatedCar.getBrand()).isEqualTo("newBrand");
    }

    @Test
    public void fFailToUpdateCarIdNotExists() {
        // try to update car with non existing id 0
        // it should give 404 error response
        Integer carId = 0;
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("carId", carId)
                        .when()
                        .put("/app/cars/{carId}");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(404);
        assertThat(response.getBody().asString()).contains("Car with id of " + carId + " does not exist.");
    }

    @Test
    public void gRemoveCar() {
        // remove car with id 1
        Integer carId = 1;
        Response deleteResponse =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("carId", carId)
                        .when()
                        .delete("/app/cars/{carId}");

        assertThat(deleteResponse).isNotNull();
        assertThat(deleteResponse.getStatusCode()).isEqualTo(204);

        // try to get car with id 1
        // it should give empty response
        Response response =
                given()
                        .pathParam("carId", carId)
                        .when()
                        .get("/app/cars/{carId}")
                        .then()
                        .extract().response();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(204);
        assertThat(response.getBody().asString()).isEmpty();
    }

    @Test
    public void hFailToRemoveCarIdNotExists() {
        // try to remove car with non existing id 0
        // it should give error response
        Integer carId = 0;
        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("carId", carId)
                        .when()
                        .delete("/app/cars/{carId}");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(500);
        assertThat(response.getBody().asString()).contains("attempt to create delete event with null entity");
    }
}
