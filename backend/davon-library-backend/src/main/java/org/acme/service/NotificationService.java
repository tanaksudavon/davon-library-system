package org.acme.service;

import org.acme.model.Notification;
import org.acme.model.NotificationType;
import org.acme.model.User;
import org.acme.model.Book;
import org.acme.repository.NotificationRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class NotificationService {

    @Inject
    NotificationRepository notificationRepository;

    @Inject
    UserService userService;

    @Transactional
    public void createBookBorrowedNotification(Long userId, String bookTitle) {
        User user = userService.getUserById(userId).orElse(null);
        if (user != null) {
            String title = "Book Borrowed";
            String message = String.format("You have successfully borrowed '%s'", bookTitle);
            Notification notification = new Notification(user, NotificationType.BOOK_BORROWED, title, message,
                    bookTitle);
            notificationRepository.persist(notification);
        }
    }

    @Transactional
    public void createBookReturnedNotification(Long userId, String bookTitle) {
        User user = userService.getUserById(userId).orElse(null);
        if (user != null) {
            String title = "Book Returned";
            String message = String.format("You have returned '%s'", bookTitle);
            Notification notification = new Notification(user, NotificationType.BOOK_RETURNED, title, message,
                    bookTitle);
            notificationRepository.persist(notification);
        }
    }

    @Transactional
    public void createBookReservedNotification(Long userId, String bookTitle) {
        User user = userService.getUserById(userId).orElse(null);
        if (user != null) {
            String title = "Book Reserved";
            String message = String.format("You have reserved '%s'", bookTitle);
            Notification notification = new Notification(user, NotificationType.BOOK_RESERVED, title, message,
                    bookTitle);
            notificationRepository.persist(notification);
        }
    }

    @Transactional
    public void createReservationAvailableNotification(Long userId, String bookTitle) {
        User user = userService.getUserById(userId).orElse(null);
        if (user != null) {
            String title = "Reservation Available";
            String message = String.format("Your reserved book '%s' is now available for pickup!", bookTitle);
            Notification notification = new Notification(user, NotificationType.RESERVATION_AVAILABLE, title, message,
                    bookTitle);
            notificationRepository.persist(notification);
        }
    }

    @Transactional
    public void createFinePaidNotification(Long userId, double amount) {
        User user = userService.getUserById(userId).orElse(null);
        if (user != null) {
            String title = "Fine Paid";
            String message = String.format("You have paid a fine of $%.2f", amount);
            Notification notification = new Notification(user, NotificationType.FINE_PAID, title, message, null);
            notificationRepository.persist(notification);
        }
    }

    @Transactional
    public void createReservationCancelledNotification(Long userId, String bookTitle) {
        User user = userService.getUserById(userId).orElse(null);
        if (user != null) {
            String title = "Reservation Cancelled";
            String message = String.format("Your reservation for '%s' has been cancelled", bookTitle);
            Notification notification = new Notification(user, NotificationType.RESERVATION_CANCELLED, title, message,
                    bookTitle);
            notificationRepository.persist(notification);
        }
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findUnreadByUserId(userId);
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.markAsRead(notificationId);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadForUser(userId);
    }

    @Transactional
    public void clearAllNotifications(Long userId) {
        notificationRepository.deleteAllForUser(userId);
    }
}