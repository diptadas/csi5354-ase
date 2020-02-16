package edu.baylor.miniapp;

import edu.baylor.miniapp.model.Car;
import edu.baylor.miniapp.model.Person;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 * Assignment 02
 *
 * @author Dipta Das
 */
@Path("/persons")
@ApplicationScoped
public class PersonResource {

    @PersistenceContext(unitName = "MiniAppPU")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Person> all() {
        return em.createNamedQuery("Person.findAll", Person.class)
                .getResultList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(Person person) throws Exception {
        if (person.getId() != null) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("Unable to create Person, id was already set.")
                    .build();
        }

        try {
            em.persist(person);
        } catch (Exception e) {
            return Response
                    .serverError()
                    .entity(e.getMessage())
                    .build();
        }
        return Response
                .created(new URI(person.getId().toString()))
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{personId}")
    public Person get(@PathParam("personId") Integer personId) {
        return em.find(Person.class, personId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{personId}/cars")
    public List<Car> getCarsForPerson(@PathParam("personId") Integer personId) {
        return em.createNamedQuery("Person.findCars", Car.class).setParameter("id", personId).getResultList();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{personId}")
    @Transactional
    public Response remove(@PathParam("personId") Integer personId) {
        try {
            Person entity = em.find(Person.class, personId);

            // remove owner from associated cars
            List<Car> cars = getCarsForPerson(personId);
            for (Car car : cars) {
                car.setOwner(null);
                em.merge(car);
            }

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
    @Path("/{personId}")
    @Transactional
    public Response update(@PathParam("personId") Integer personId, Person person) {
        try {
            Person entity = em.find(Person.class, personId);

            if (null == entity) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Person with id of " + personId + " does not exist.")
                        .build();
            }

            em.merge(person);

            return Response
                    .ok(person)
                    .build();
        } catch (Exception e) {
            return Response
                    .serverError()
                    .entity(e.getMessage())
                    .build();
        }
    }
}
