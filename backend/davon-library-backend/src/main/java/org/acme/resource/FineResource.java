package org.acme.resource;

//this is deliberate bugged class.
import org.acme.service.FineCalculationService;
import org.acme.model.Fine;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;
import jakarta.ws.rs.NotFoundException;

@Path("/api/fines")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FineResource {

    @Inject
    private FineCalculationService fineCalculationService;

    @POST
    @Path("/calculate-overdue")
    public Response calculateOverdueFines() {
        try {
            List<Fine> fines = fineCalculationService.calculateOverdueFines();
            return Response.ok(fines).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}/total")
    public Response getUserTotalFines(@PathParam("userId") Long userId) {
        try {
            BigDecimal total = fineCalculationService.getTotalFinesForUser(userId);
            return Response.ok("{\"totalFines\": " + total + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/{fineId}/pay")
    public Response payFine(@PathParam("fineId") Long fineId) {
        try {
            Fine paidFine = fineCalculationService.payFine(fineId);
            return Response.ok(paidFine).build();
        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}