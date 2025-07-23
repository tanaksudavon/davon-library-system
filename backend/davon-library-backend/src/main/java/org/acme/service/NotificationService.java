package org.acme.service;

import org.acme.model.Loan;
import org.acme.model.Reservation;
import org.acme.model.User;
import org.acme.repository.LoanRepository;
import org.acme.repository.ReservationRepository;
import org.acme.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service for sending notifications to users regarding loans, fines, and
 * reservations.
 */
@ApplicationScoped
public class NotificationService {

    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final FineCalculationService fineCalculationService;
    private final ReservationRepository reservationRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM d, yyyy");

    @Inject
    public NotificationService(UserRepository userRepository, LoanRepository loanRepository,
            FineCalculationService fineCalculationService, ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.fineCalculationService = fineCalculationService;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Sends a notification to a user about an overdue book.
     *
     * @param userId The ID of the user to notify.
     * @param loanId The ID of the overdue loan.
     * @return A status message indicating the outcome.
     */
    public String sendOverdueNotification(Long userId, Long loanId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        Objects.requireNonNull(loanId, "Loan ID cannot be null");

        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        Loan loan = loanRepository.findByIdOptional(loanId)
                .orElseThrow(() -> new NotFoundException("Loan not found with ID: " + loanId));

        String userName = getUserDisplayName(user);
        String dueDate = loan.getDueDate() != null ? loan.getDueDate().format(DATE_FORMATTER) : "N/A";

        String message = String.format(
                "Dear %s,\n\nYour loan for the book (ID: %d) is overdue. The due date was %s.\n" +
                        "Please return the book as soon as possible to avoid further fines.\n\n" +
                        "Thank you,\nDavon Library System",
                userName, loan.getBook().getId(), dueDate);

        return sendEmail(user.getEmail(), "Overdue Book Notification", message);
    }

    /**
     * Sends a notification when a reserved book becomes available.
     *
     * @param reservationId The ID of the reservation that is ready.
     * @return true if the notification was sent successfully, false otherwise.
     */
    public boolean sendReservationReadyNotification(Long reservationId) {
        Objects.requireNonNull(reservationId, "Reservation ID cannot be null");
        Reservation reservation = reservationRepository.findByIdOptional(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found with ID: " + reservationId));

        User user = reservation.getUser();

        String message = String.format(
                "Dear %s,\n\nGood news! The book you reserved (ID: %d) is now available for pickup.\n" +
                        "Please collect it from the library within the next 3 days.\n\n" +
                        "Thank you,\nDavon Library System",
                getUserDisplayName(user), reservation.getBook().getId());

        return sendNotification(user, "Your Reserved Book is Available", message);
    }

    /**
     * Sends a reminder to a user about their outstanding fines.
     *
     * @param userId The ID of the user to remind.
     */
    public void sendFineReminder(Long userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        BigDecimal totalFines = fineCalculationService.getTotalFinesForUser(userId);

        if (totalFines != null && totalFines.compareTo(BigDecimal.ZERO) > 0) {
            String message = String.format(
                    "Dear %s,\n\nThis is a friendly reminder that you have outstanding fines totaling $%.2f.\n" +
                            "Please settle your fines at your earliest convenience to continue using library services.\n\n"
                            +
                            "Thank you,\nDavon Library System",
                    user.getUsername(), totalFines);
            sendNotification(user, "Reminder: Outstanding Library Fines", message);
        }
    }

    /**
     * Sends a bulk notification to a list of users.
     * In a real application, this should use a proper email service and handle
     * pagination.
     *
     * @param subject         The subject of the notification.
     * @param messageTemplate A template for the message, using {name} as a
     *                        placeholder.
     * @return A list of statuses for each notification sent.
     */
    public List<String> sendBulkNotification(String subject, String messageTemplate) {
        // For a real application, use PanacheQuery for pagination to avoid memory
        // issues.
        // Example: userRepository.findAll().page(Page.of(pageIndex, pageSize)).list();
        return userRepository.findAll().stream()
                .map(user -> {
                    String personalizedMessage = messageTemplate.replace("{name}", getUserDisplayName(user));
                    return sendEmail(user.getEmail(), subject, personalizedMessage);
                })
                .collect(Collectors.toList());
    }

    /**
     * Simulates sending an email. In a real application, this would integrate with
     * an email service.
     *
     * @param email   The recipient's email address.
     * @param subject The subject of the email.
     * @param message The body of the email.
     * @return A status message.
     */
    private String sendEmail(String email, String subject, String message) {
        if (email == null || email.isBlank()) {
            return "FAILURE: Email address is null or empty.";
        }
        // Basic email format validation
        if (!email.contains("@") || !email.contains(".")) {
            return "FAILURE: Invalid email format.";
        }

        System.out.printf("--- Email Sent ---\nTo: %s\nSubject: %s\nMessage: %s\n------------------\n", email, subject,
                message);
        return "SUCCESS";
    }

    /**
     * A helper to send a notification and convert the status to a boolean.
     */
    private boolean sendNotification(User user, String subject, String message) {
        if (user == null) {
            return false;
        }
        String result = sendEmail(user.getEmail(), subject, message);
        return "SUCCESS".equals(result);
    }

    /**
     * Helper to get a user's display name, falling back to username if names are
     * not set.
     */
    private String getUserDisplayName(User user) {
        if (user.getFirstName() != null && !user.getFirstName().isBlank()) {
            return user.getFirstName();
        }
        return user.getUsername();
    }
}