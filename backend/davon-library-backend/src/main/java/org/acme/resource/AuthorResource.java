package org.acme.resource;

import org.acme.model.Author;
import org.acme.service.AuthorService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {

    @Inject
    private AuthorService authorService;

    @GET
    public List<Author> getAllAuthors() {
        System.out.println("DEBUG: AuthorResource.getAllAuthors() called");
        return authorService.getAllAuthors();
    }

    @GET
    @Path("/{id}")
    public Response getAuthorById(@PathParam("id") Long id) {
        System.out.println("DEBUG: AuthorResource.getAuthorById() called with id: " + id);
        Author author = authorService.getAuthorById(id);
        if (author != null) {
            return Response.ok(author).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response createAuthor(Author author) {
        System.out.println("DEBUG: AuthorResource.createAuthor() called");
        Author createdAuthor = authorService.createAuthor(author);
        return Response.status(Response.Status.CREATED).entity(createdAuthor).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateAuthor(@PathParam("id") Long id, Author author) {
        System.out.println("DEBUG: AuthorResource.updateAuthor() called with id: " + id);
        Author updatedAuthor = authorService.updateAuthor(id, author);
        if (updatedAuthor != null) {
            return Response.ok(updatedAuthor).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") Long id) {
        System.out.println("DEBUG: AuthorResource.deleteAuthor() called with id: " + id);
        boolean deleted = authorService.deleteAuthor(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/search")
    public List<Author> searchAuthors(@QueryParam("name") String name) {
        System.out.println("DEBUG: AuthorResource.searchAuthors() called with name: " + name);
        return authorService.searchAuthorsByName(name);
    }
}