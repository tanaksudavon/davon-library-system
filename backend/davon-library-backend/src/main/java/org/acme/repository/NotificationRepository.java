package org.acme.repository;

import org.acme.model.Notification;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class NotificationRepository implements PanacheRepository<Notification> {

    public List<Notification> findByUserId(Long userId) {
        return list("user.id = ?1 ORDER BY createdAt DESC", userId);
    }

    public List<Notification> findUnreadByUserId(Long userId) {
        return list("user.id = ?1 AND isRead = false ORDER BY createdAt DESC", userId);
    }

    public long countUnreadByUserId(Long userId) {
        return count("user.id = ?1 AND isRead = false", userId);
    }

    public void markAsRead(Long notificationId) {
        update("isRead = true WHERE id = ?1", notificationId);
    }

    public void markAllAsReadForUser(Long userId) {
        update("isRead = true WHERE user.id = ?1", userId);
    }

    public void deleteAllForUser(Long userId) {
        delete("user.id = ?1", userId);
    }
}