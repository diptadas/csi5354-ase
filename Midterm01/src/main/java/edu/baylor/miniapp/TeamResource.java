package edu.baylor.miniapp;

import edu.baylor.miniapp.model.Person;
import edu.baylor.miniapp.model.Team;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

/**
 * Midterm 01
 *
 * @author Dipta Das
 */
@Path("/teams")
@ApplicationScoped
public class TeamResource {

    @PersistenceContext(unitName = "MiniAppPU")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Team> all() {
        return em.createNamedQuery("Team.findAll", Team.class)
                .getResultList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(Team team) throws URISyntaxException {
        if (team.getId() != null) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("Unable to create Team, id was already set.")
                    .build();
        }

        try {
            em.persist(team);
        } catch (Exception e) {
            return Response
                    .serverError()
                    .entity(e.getMessage())
                    .build();
        }
        return Response
                .created(new URI(team.getId().toString()))
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{teamId}")
    public Team get(@PathParam("teamId") Integer teamId) {
        return em.find(Team.class, teamId);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{teamId}")
    @Transactional
    public Response remove(@PathParam("teamId") Integer teamId) {
        try {
            Team entity = em.find(Team.class, teamId);
            em.remove(entity);
        } catch (Exception e) {
            return Response
                    .serverError()
                    .entity(e.getMessage())
                    .build();
        }

        return Response
                .noContent()
                .build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{teamId}")
    @Transactional
    public Response update(@PathParam("teamId") Integer teamId, Team team) {
        try {
            Team entity = em.find(Team.class, teamId);

            if (null == entity) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Team with id of " + teamId + " does not exist.")
                        .build();
            }

            em.merge(team);

            return Response
                    .ok(team)
                    .build();
        } catch (Exception e) {
            return Response
                    .serverError()
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Path("/{teamId}/leader/{personId}")
    public Team addLeader(@PathParam("teamId") Integer teamId, @PathParam("personId") Integer personId) throws Exception {
        Team team = em.find(Team.class, teamId);
        Person person = em.find(Person.class, personId);

        if (person.getLeaderOf() != null) {
            throw new Exception("Person is already leader of another team");
        } else if (team.getLeader() != null) {
            throw new Exception("Leader already assigned for team");
        } else {
            team.setLeader(person);
            person.setLeaderOf(team);
            em.merge(team);
        }

        return team;
    }

    @GET
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{teamId}/member/{personId}")
    public Team addMember(@PathParam("teamId") Integer teamId, @PathParam("personId") Integer personId) throws Exception {
        Team team = em.find(Team.class, teamId);
        Person person = em.find(Person.class, personId);

        if (person.getMemberOf() != null) {
            throw new Exception("Person is already member of another team");
        } else if (team.getMembers() != null && team.getMembers().size() == 3) {
            throw new Exception("Team already has 3 members");
        } else if (team.getLeader() != null && team.getLeader().getId() == personId) {
            throw new Exception("Person is already leader of the team");
        } else {
            team.getMembers().add(person);
            person.setMemberOf(team);
            em.merge(team);
        }

        return team;
    }

    @GET
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{teamId}/preferred/{preferredTeamId}")
    public Team addPreferredTeam(@PathParam("teamId") Integer teamId, @PathParam("preferredTeamId") Integer preferredTeamId) throws Exception {
        Team team = em.find(Team.class, teamId);
        Team preferredTeam = em.find(Team.class, preferredTeamId);

        if (teamId.equals(preferredTeamId)) {
            throw new Exception("Can't prefer yourself");
        }

        if (team.getSkill().equals(preferredTeam.getSkill())) {
            team.getPreferredTeams().add(preferredTeam);
            em.merge(team);
        } else {
            throw new Exception("Skill not matched");
        }

        return team;
    }
}
