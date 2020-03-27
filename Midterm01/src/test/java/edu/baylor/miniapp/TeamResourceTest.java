package edu.baylor.miniapp;


import edu.baylor.miniapp.model.Team;
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
public class TeamResourceTest {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8080";
    }

    @Test
    public void aRetrieveAllTeams() {
        Response response =
                when()
                        .get("/app/teams")
                        .then()
                        .extract().response();

        String jsonAsString = response.asString();
        List<Map<String, ?>> jsonAsList = JsonPath.from(jsonAsString).getList("");

        assertThat(jsonAsList.size()).isEqualTo(5);

        Map<String, ?> record1 = jsonAsList.get(0);
        assertThat(record1.get("id")).isEqualTo(1);
        assertThat(record1.get("name")).isEqualTo("team1");
        assertThat(record1.get("version")).isEqualTo(1);
    }

    @Test
    public void bRetrieveTeam() {
        Response response =
                given()
                        .pathParam("teamId", 1)
                        .when()
                        .get("/app/teams/{teamId}")
                        .then()
                        .extract().response();

        String jsonAsString = response.asString();

        Team team = JsonPath.from(jsonAsString).getObject("", Team.class);

        assertThat(team.getId()).isEqualTo(1);
        assertThat(team.getName()).isEqualTo("team1");
        assertThat(team.getVersion()).isEqualTo(1);
    }

    @Test
    public void cCreateTeam() {
        Team newTeam = new Team();
        newTeam.setName("newTeam");
        newTeam.setSkill("newSkill");

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .body(newTeam)
                        .when()
                        .post("/app/teams");

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(201);
        String locationUrl = response.getHeader("Location");
        Integer teamId = Integer.valueOf(locationUrl.substring(locationUrl.lastIndexOf('/') + 1));

        response =
                when()
                        .get("/app/teams")
                        .then()
                        .extract().response();

        String jsonAsString = response.asString();
        List<Map<String, ?>> jsonAsList = JsonPath.from(jsonAsString).getList("");

        assertThat(jsonAsList.size()).isEqualTo(6);

        response =
                given()
                        .pathParam("teamId", teamId)
                        .when()
                        .get("/app/teams/{teamId}")
                        .then()
                        .extract().response();

        jsonAsString = response.asString();

        Team team = JsonPath.from(jsonAsString).getObject("", Team.class);

        assertThat(team.getId()).isEqualTo(teamId);
        assertThat(team.getName()).isEqualTo("newTeam");
    }

    @Test
    public void eUpdateTeam() {
        // get team with id 1
        Integer teamId = 1;
        Response response =
                given()
                        .pathParam("teamId", teamId)
                        .when()
                        .get("/app/teams/{teamId}")
                        .then()
                        .extract().response();

        Team team = JsonPath.from(response.asString()).getObject("", Team.class);

        // now update the name
        team.setName("newName");

        Response updateResponse =
                given()
                        .contentType(ContentType.JSON)
                        .body(team)
                        .pathParam("teamId", teamId)
                        .when()
                        .put("/app/teams/{teamId}")
                        .then()
                        .extract().response();

        Team updatedTeam = JsonPath.from(updateResponse.asString()).getObject("", Team.class);

        assertThat(updatedTeam.getName()).isEqualTo("newName");
    }

    @Test
    public void gRemoveTeam() {
        // remove team with id 5
        Integer teamId = 5;
        Response deleteResponse =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("teamId", teamId)
                        .when()
                        .delete("/app/teams/{teamId}");

        assertThat(deleteResponse).isNotNull();
        assertThat(deleteResponse.getStatusCode()).isEqualTo(204);

        // try to get team with id 5
        // it should give empty response
        Response response =
                given()
                        .pathParam("teamId", teamId)
                        .when()
                        .get("/app/teams/{teamId}")
                        .then()
                        .extract().response();

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(204);
        assertThat(response.getBody().asString()).isEmpty();
    }

    @Test
    public void hAddLeader() {
        // assign person 1 as leader of team 1
        Integer personId = 1;
        Integer teamId = 1;

        Response addLeaderResponse =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("teamId", teamId)
                        .pathParam("personId", personId)
                        .when()
                        .get("/app/teams/{teamId}/leader/{personId}")
                        .then()
                        .extract().response();
        ;

        Integer leaderId = JsonPath.from(addLeaderResponse.asString()).getObject("leader", Integer.class);
        assertThat(leaderId).isEqualTo(personId);
    }

    public Response addSingleMember(Integer teamId, Integer personId) {
        return given()
                .contentType(ContentType.JSON)
                .pathParam("teamId", teamId)
                .pathParam("personId", personId)
                .when()
                .get("/app/teams/{teamId}/member/{personId}")
                .then()
                .extract().response();
    }

    @Test
    public void iAddMembers() {
        // assign person 2,3,4 as member of team 1
        addSingleMember(1, 2);
        addSingleMember(1, 3);
        Response response = addSingleMember(1, 4);

        List<Integer> members = JsonPath.from(response.asString()).getList("members", Integer.class);
        assertThat(members).hasSize(3);

        // now try to add another, it should fail
        response = addSingleMember(1, 5);
        assertThat(response.getBody().asString()).contains("Team already has 3 members");

        // now try to already assigned person 4 to team 2, it should fail
        response = addSingleMember(2, 4);
        assertThat(response.getBody().asString()).contains("Person is already member of another team");
    }

    @Test
    public void jAddPreferred() {
        Integer teamId = 1;
        Integer preferredTeamId = 2;

        Response response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("teamId", teamId)
                        .pathParam("preferredTeamId", preferredTeamId)
                        .when()
                        .get("/app/teams/{teamId}/preferred/{preferredTeamId}")
                        .then()
                        .extract().response();

        // team 1 and team 2 has different skills, it should fail
        // ref: load.sql
        assertThat(response.getBody().asString()).contains("Skill not matched");

        // now try with team 3
        preferredTeamId = 3;

        response =
                given()
                        .contentType(ContentType.JSON)
                        .pathParam("teamId", teamId)
                        .pathParam("preferredTeamId", preferredTeamId)
                        .when()
                        .get("/app/teams/{teamId}/preferred/{preferredTeamId}")
                        .then()
                        .extract().response();

        // team 1 and team 3 has same skills, it should work now
        List<Integer> preferredTeamIds = JsonPath.from(response.asString()).getList("preferredTeams", Integer.class);
        assertThat(preferredTeamIds).hasSize(1);
        assertThat(preferredTeamIds.get(0)).isEqualTo(preferredTeamId);
    }

}
