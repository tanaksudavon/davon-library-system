package org.acme.resource;

import org.acme.model.Notification;
import org.acme.service.NotificationService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource {

    @Inject
    NotificationService notificationService;

    // DTO class for simplified notification response
    public static class NotificationDTO {
        public Long id;
        public String type;
        public String title;
        public String message;
        public String bookTitle;
        public boolean isRead;
        public LocalDateTime createdAt;

        public NotificationDTO(Notification notification) {
            this.id = notification.getId();
            this.type = notification.getType() != null ? notification.getType().toString() : "UNKNOWN";
            this.title = notification.getTitle();
            this.message = notification.getMessage();
            this.bookTitle = notification.getBookTitle();
            this.isRead = notification.isRead();
            this.createdAt = notification.getCreatedAt();
        }
    }

    @GET
    @Path("/user/{userId}")
    public Response getUserNotifications(@PathParam("userId") Long userId) {
        try {
            List<Notification> notifications = notificationService.getUserNotifications(userId);
            List<NotificationDTO> notificationDTOs = notifications.stream()
                    .map(NotificationDTO::new)
                    .collect(Collectors.toList());
            return Response.ok(notificationDTOs).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}/unread")
    public Response getUnreadNotifications(@PathParam("userId") Long userId) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotifications(userId);
            List<NotificationDTO> notificationDTOs = notifications.stream()
                    .map(NotificationDTO::new)
                    .collect(Collectors.toList());
            return Response.ok(notificationDTOs).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/user/{userId}/count")
    public Response getUnreadCount(@PathParam("userId") Long userId) {
        try {
            long count = notificationService.getUnreadCount(userId);
            return Response.ok("{\"count\": " + count + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PUT
    @Path("/{id}/read")
    public Response markAsRead(@PathParam("id") Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            return Response.ok("{\"message\": \"Notification marked as read\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @PUT
    @Path("/user/{userId}/read-all")
    public Response markAllAsRead(@PathParam("userId") Long userId) {
        try {
            notificationService.markAllAsRead(userId);
            return Response.ok("{\"message\": \"All notifications marked as read\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @DELETE
    @Path("/user/{userId}/clear-all")
    public Response clearAllNotifications(@PathParam("userId") Long userId) {
        try {
            notificationService.clearAllNotifications(userId);
            return Response.ok("{\"message\": \"All notifications cleared\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}