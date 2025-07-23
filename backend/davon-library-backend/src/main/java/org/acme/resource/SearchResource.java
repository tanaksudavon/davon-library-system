package org.acme.resource;

import org.acme.model.Book;
import org.acme.model.User;
import org.acme.service.SearchService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SearchResource {

    @Inject
    private SearchService searchService;

    @GET
    @Path("/books")
    public Response searchBooks(@QueryParam("q") String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Search term is required\"}")
                        .build();
            }

            List<Book> books = searchService.searchBooks(searchTerm);
            return Response.ok(books).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/users")
    public Response searchUsers(@QueryParam("q") String searchTerm) {
        try {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Search term is required\"}")
                        .build();
            }

            List<User> users = searchService.searchUsers(searchTerm);
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}