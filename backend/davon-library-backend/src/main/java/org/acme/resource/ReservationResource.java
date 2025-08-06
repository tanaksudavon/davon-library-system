package org.acme.resource;

import org.acme.service.ReservationService;
import org.acme.model.Reservation;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.ForbiddenException;

@Path("/api/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReservationResource {

    @Inject
    private ReservationService reservationService;

    @POST
    @Path("/book/{bookId}/user/{userId}")
    public Response createReservation(@PathParam("bookId") Long bookId,
            @PathParam("userId") Long userId) {
        try {
            Reservation reservation = reservationService.createReservation(bookId, userId);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Reservation created successfully\", \"reservationId\": "
                            + reservation.getId() + "}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{reservationId}/user/{userId}")
    public Response cancelReservation(@PathParam("reservationId") Long reservationId,
            @PathParam("userId") Long userId) {
        try {
            reservationService.cancelReservation(reservationId, userId);
            return Response.ok("{\"message\": \"Reservation cancelled successfully\"}").build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (ForbiddenException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}")
    public Response getUserReservations(@PathParam("userId") Long userId) {
        try {
            List<Reservation> reservations = reservationService.getUserActiveReservations(userId);
            return Response.ok(reservations).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/book/{bookId}/queue")
    public Response getBookReservationQueue(@PathParam("bookId") Long bookId) {
        try {
            List<Reservation> queue = reservationService.getBookReservationQueue(bookId);
            return Response.ok(queue).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/expire-old")
    public Response expireOldReservations() {
        try {
            reservationService.expireOldReservations();
            return Response.ok("{\"message\": \"Old reservations expired\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/fix-inconsistent")
    public Response fixInconsistentReservations() {
        try {
            int updatedCount = reservationService.fixInconsistentReservations();
            return Response.ok("{\"message\": \"Fixed " + updatedCount + " inconsistent reservations\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}