package org.acme.resource;

import org.acme.model.Loan;
import org.acme.service.LoanService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/api/loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoanResource {

    @Inject
    private LoanService loanService;

    @GET
    public Response getAllLoans() {
        List<Loan> loans = loanService.getAllLoans();
        return Response.ok(loans).build();
    }

    @GET
    @Path("/{id}")
    public Response getLoanById(@PathParam("id") Long id) {
        Optional<Loan> loan = loanService.getLoanById(id);
        if (loan.isPresent()) {
            return Response.ok(loan.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Loan not found\"}")
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}")
    public Response getLoansByUserId(@PathParam("userId") Long userId) {
        List<Loan> loans = loanService.getLoansByUserId(userId);
        return Response.ok(loans).build();
    }

    @POST
    public Response createLoan(Loan loan) {
        try {
            Loan createdLoan = loanService.createLoan(loan);
            return Response.status(Response.Status.CREATED)
                    .entity(createdLoan)
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateLoan(@PathParam("id") Long id, Loan loan) {
        try {
            Optional<Loan> existingLoan = loanService.getLoanById(id);
            if (existingLoan.isPresent()) {
                loan.setId(id);
                Loan updatedLoan = loanService.createLoan(loan); // Service handles both create and update
                return Response.ok(updatedLoan).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Loan not found\"}")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteLoan(@PathParam("id") Long id) {
        try {
            Optional<Loan> existingLoan = loanService.getLoanById(id);
            if (existingLoan.isPresent()) {
                loanService.deleteLoan(id);
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Loan not found\"}")
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}