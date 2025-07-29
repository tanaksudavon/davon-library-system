package org.acme.resource;

import org.acme.model.Category;
import org.acme.service.CategoryService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/categories")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryResource {

    @Inject
    private CategoryService categoryService;

    @GET
    public List<Category> getAllCategories() {
        System.out.println("DEBUG: CategoryResource.getAllCategories() called");
        return categoryService.getAllCategories();
    }

    @GET
    @Path("/{id}")
    public Response getCategoryById(@PathParam("id") Long id) {
        System.out.println("DEBUG: CategoryResource.getCategoryById() called with id: " + id);
        Category category = categoryService.getCategoryById(id);
        if (category != null) {
            return Response.ok(category).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    public Response createCategory(Category category) {
        System.out.println("DEBUG: CategoryResource.createCategory() called");
        Category createdCategory = categoryService.createCategory(category);
        return Response.status(Response.Status.CREATED).entity(createdCategory).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateCategory(@PathParam("id") Long id, Category category) {
        System.out.println("DEBUG: CategoryResource.updateCategory() called with id: " + id);
        Category updatedCategory = categoryService.updateCategory(id, category);
        if (updatedCategory != null) {
            return Response.ok(updatedCategory).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {
        System.out.println("DEBUG: CategoryResource.deleteCategory() called with id: " + id);
        boolean deleted = categoryService.deleteCategory(id);
        if (deleted) {
            return Response.noContent().build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}