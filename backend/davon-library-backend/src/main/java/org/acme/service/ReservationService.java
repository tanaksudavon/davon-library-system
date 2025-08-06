package org.acme.service;

import org.acme.model.Book;
import org.acme.model.BookStatus;
import org.acme.model.Reservation;
import org.acme.model.ReservationStatus;
import org.acme.model.Loan;
import org.acme.repository.BookRepository;
import org.acme.repository.ReservationRepository;
import org.acme.repository.UserRepository;
import org.acme.repository.LoanRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.acme.model.User;

/**
 * Service for managing book reservations.
 * This service handles creation, cancellation, and retrieval of book
 * reservations.
 */
@ApplicationScoped
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanRepository loanRepository;
    private final NotificationService notificationService;

    private static final int RESERVATION_EXPIRATION_DAYS = 7;
    private static final int MAX_ACTIVE_RESERVATIONS_PER_USER = 5;

    @Inject
    public ReservationService(ReservationRepository reservationRepository, BookRepository bookRepository,
            UserRepository userRepository, LoanRepository loanRepository, NotificationService notificationService) {
        this.reservationRepository = reservationRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.loanRepository = loanRepository;
        this.notificationService = notificationService;
    }

    /**
     * Creates a new reservation for a book by a user.
     *
     * @param bookId The ID of the book to reserve.
     * @param userId The ID of the user making the reservation.
     * @return The newly created Reservation object.
     * @throws NotFoundException     if the book is not found.
     * @throws IllegalStateException if the book is not available for reservation.
     * @throws BadRequestException   if the user has reached the maximum number of
     *                               active reservations.
     */
    @Transactional
    public Reservation createReservation(Long bookId, Long userId) {
        Objects.requireNonNull(bookId, "Book ID cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        Book book = bookRepository.findByIdOptional(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + bookId));

        if (book.getStatus() != BookStatus.AVAILABLE && book.getStatus() != BookStatus.BORROWED) {
            throw new IllegalStateException("Book is not available for reservation.");
        }

        if (!canUserReserveBook(userId, bookId)) {
            throw new BadRequestException(
                    "User has reached the maximum number of active reservations or has already reserved this book.");
        }

        Reservation reservation = new Reservation();

        // Set the object references instead of IDs
        reservation.setBook(book);
        User user = userRepository.findByIdOptional(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        reservation.setUser(user);

        reservation.setReservationDate(LocalDate.now());
        reservation.setExpirationDate(LocalDate.now().plusDays(RESERVATION_EXPIRATION_DAYS));
        reservation.setStatus(ReservationStatus.ACTIVE);

        // Calculate queue position
        int queuePosition = getNextQueuePosition(bookId);
        reservation.setQueuePosition(queuePosition);

        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());

        // Only change status to RESERVED if book was AVAILABLE
        // BORROWED books should remain BORROWED even when reserved
        if (book.getStatus() == BookStatus.AVAILABLE) {
            book.setStatus(BookStatus.RESERVED);
            bookRepository.persist(book);
        }

        reservationRepository.persist(reservation);

        // Create notification for book reserved
        notificationService.createBookReservedNotification(userId, book.getTitle());

        return reservation;
    }

    /**
     * Cancels an existing reservation.
     *
     * @param reservationId The ID of the reservation to cancel.
     * @param userId        The ID of the user attempting to cancel the reservation.
     * @return The updated Reservation object with a CANCELLED status.
     * @throws NotFoundException  if the reservation is not found.
     * @throws ForbiddenException if the user does not own the reservation.
     */
    @Transactional
    public Reservation cancelReservation(Long reservationId, Long userId) {
        Objects.requireNonNull(reservationId, "Reservation ID cannot be null");
        Objects.requireNonNull(userId, "User ID cannot be null");

        Reservation reservation = reservationRepository.findByIdOptional(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation not found with ID: " + reservationId));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new ForbiddenException("User does not have permission to cancel this reservation.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setUpdatedAt(LocalDateTime.now());

        Book book = reservation.getBook();

        // Check if there are any active loans for this book
        List<Loan> activeLoans = loanRepository.list("book.id = ?1 and returnDate is null", book.getId());

        // Only set book to AVAILABLE if there are no active loans
        if (activeLoans.isEmpty()) {
            book.setStatus(BookStatus.AVAILABLE);
            bookRepository.persist(book);
        }
        // If there are active loans, the book should remain BORROWED (don't change
        // status)

        reservationRepository.persist(reservation);

        // Create notification for cancelled reservation
        notificationService.createReservationCancelledNotification(userId, book.getTitle());

        return reservation;
    }

    /**
     * Retrieves all active reservations for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of active reservations.
     */
    public List<Reservation> getUserActiveReservations(Long userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        return reservationRepository.find("user.id = ?1 and status = ?2", userId, ReservationStatus.ACTIVE).list();
    }

    /**
     * Expires all active reservations that are past their expiration date.
     * This method is typically called by a scheduled job.
     */
    @Transactional
    public void expireOldReservations() {
        List<Reservation> reservationsToExpire = reservationRepository
                .find("status = ?1 and expirationDate < ?2", ReservationStatus.ACTIVE, LocalDate.now())
                .list();

        for (Reservation reservation : reservationsToExpire) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservation.setUpdatedAt(LocalDateTime.now());

            Book book = reservation.getBook();

            // Check if there are any active loans for this book
            List<Loan> activeLoans = loanRepository.list("book.id = ?1 and returnDate is null", book.getId());

            // Only set book to AVAILABLE if there are no active loans
            if (activeLoans.isEmpty()) {
                book.setStatus(BookStatus.AVAILABLE);
                bookRepository.persist(book);
            }
            // If there are active loans, the book should remain BORROWED (don't change
            // status)

            reservationRepository.persist(reservation);
        }
    }

    /**
     * Retrieves the reservation queue for a specific book, sorted by reservation
     * date.
     *
     * @param bookId The ID of the book.
     * @return A sorted list of reservations for the book.
     */
    public List<Reservation> getBookReservationQueue(Long bookId) {
        Objects.requireNonNull(bookId, "Book ID cannot be null");
        return reservationRepository.find("book.id", bookId).list();
    }

    /**
     * Checks if a user is allowed to reserve a specific book.
     *
     * @param userId The ID of the user.
     * @param bookId The ID of the book.
     * @return true if the user can reserve the book, false otherwise.
     */
    private boolean canUserReserveBook(Long userId, Long bookId) {
        long activeReservations = reservationRepository
                .count("user.id = ?1 and status = ?2", userId, ReservationStatus.ACTIVE);

        if (activeReservations >= MAX_ACTIVE_RESERVATIONS_PER_USER) {
            return false;
        }

        long existingReservationForBook = reservationRepository
                .count("user.id = ?1 and book.id = ?2 and status = ?3", userId, bookId, ReservationStatus.ACTIVE);

        return existingReservationForBook == 0;
    }

    /**
     * Calculate the next queue position for a book reservation.
     */
    private int getNextQueuePosition(Long bookId) {
        List<Reservation> activeReservations = reservationRepository.list(
                "book.id = ?1 AND status = ?2 ORDER BY createdAt ASC",
                bookId, ReservationStatus.ACTIVE);
        return activeReservations.size() + 1;
    }

    /**
     * Fix inconsistent reservation states - marks reservations as FULFILLED
     * when user has an active loan for the same book.
     * This is a utility method to fix existing data.
     */
    @Transactional
    public int fixInconsistentReservations() {
        // Find reservations that are ACTIVE but user has an active loan for the same
        // book
        List<Reservation> inconsistentReservations = reservationRepository.find(
                "status = ?1 AND EXISTS (SELECT 1 FROM Loan l WHERE l.book.id = book.id AND l.user.id = user.id AND l.returnDate IS NULL)",
                ReservationStatus.ACTIVE).list();

        int updatedCount = 0;
        for (Reservation reservation : inconsistentReservations) {
            reservation.setStatus(ReservationStatus.FULFILLED);
            reservation.setUpdatedAt(LocalDateTime.now());
            reservationRepository.persist(reservation);
            updatedCount++;
        }

        return updatedCount;
    }
}