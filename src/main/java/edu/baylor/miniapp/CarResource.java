package edu.baylor.miniapp;

import edu.baylor.miniapp.model.Car;

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
 * Assignment 02
 *
 * @author Dipta Das
 */
@Path("/cars")
@ApplicationScoped
public class CarResource {

    @PersistenceContext(unitName = "MiniAppPU")
    private EntityManager em;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Car> all() {
        return em.createNamedQuery("Car.findAll", Car.class)
                .getResultList();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(Car car) throws URISyntaxException {
        if (car.getId() != null) {
            return Response
                    .status(Response.Status.CONFLICT)
                    .entity("Unable to create Car, id was already set.")
                    .build();
        }

        try {
            em.persist(car);
        } catch (Exception e) {
            return Response
                    .serverError()
                    .entity(e.getMessage())
                    .build();
        }
        return Response
                .created(new URI(car.getId().toString()))
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{carId}")
    public Car get(@PathParam("carId") Integer carId) {
        return em.find(Car.class, carId);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{carId}")
    @Transactional
    public Response remove(@PathParam("carId") Integer carId) {
        try {
            Car entity = em.find(Car.class, carId);
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
    @Path("/{carId}")
    @Transactional
    public Response update(@PathParam("carId") Integer carId, Car car) {
        try {
            Car entity = em.find(Car.class, carId);

            if (null == entity) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Car with id of " + carId + " does not exist.")
                        .build();
            }

            em.merge(car);

            return Response
                    .ok(car)
                    .build();
        } catch (Exception e) {
            return Response
                    .serverError()
                    .entity(e.getMessage())
                    .build();
        }
    }
}
