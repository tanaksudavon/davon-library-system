package org.acme.resource;

import org.acme.model.Favorite;
import org.acme.service.FavoriteService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/api/favorites")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FavoriteResource {

    @Inject
    private FavoriteService favoriteService;

    /**
     * Get all favorites for a user
     */
    @GET
    @Path("/user/{userId}")
    public Response getUserFavorites(@PathParam("userId") Long userId) {
        try {
            List<Favorite> favorites = favoriteService.getUserFavorites(userId);
            return Response.ok(favorites).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to fetch favorites: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Add a book to user's favorites
     */
    @POST
    @Path("/user/{userId}/book/{bookId}")
    public Response addFavorite(@PathParam("userId") Long userId, @PathParam("bookId") Long bookId) {
        try {
            Favorite favorite = favoriteService.addFavorite(userId, bookId);
            return Response.ok(favorite).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to add favorite: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Remove a book from user's favorites
     */
    @DELETE
    @Path("/user/{userId}/book/{bookId}")
    public Response removeFavorite(@PathParam("userId") Long userId, @PathParam("bookId") Long bookId) {
        try {
            boolean removed = favoriteService.removeFavorite(userId, bookId);
            if (removed) {
                return Response.ok(Map.of("message", "Favorite removed successfully")).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("error", "Favorite not found"))
                        .build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to remove favorite: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Toggle favorite status
     */
    @POST
    @Path("/user/{userId}/book/{bookId}/toggle")
    public Response toggleFavorite(@PathParam("userId") Long userId, @PathParam("bookId") Long bookId) {
        try {
            boolean isNowFavorited = favoriteService.toggleFavorite(userId, bookId);
            return Response.ok(Map.of(
                    "isFavorited", isNowFavorited,
                    "message", isNowFavorited ? "Added to favorites" : "Removed from favorites")).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to toggle favorite: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Check if a book is favorited by a user
     */
    @GET
    @Path("/user/{userId}/book/{bookId}/status")
    public Response getFavoriteStatus(@PathParam("userId") Long userId, @PathParam("bookId") Long bookId) {
        try {
            boolean isFavorited = favoriteService.isFavorited(userId, bookId);
            return Response.ok(Map.of("isFavorited", isFavorited)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to check favorite status: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Get favorite count for a book
     */
    @GET
    @Path("/book/{bookId}/count")
    public Response getFavoriteCount(@PathParam("bookId") Long bookId) {
        try {
            long count = favoriteService.getFavoriteCount(bookId);
            return Response.ok(Map.of("count", count)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to get favorite count: " + e.getMessage()))
                    .build();
        }
    }
}