package org.acme.service;

import org.acme.model.Loan;
import org.acme.model.LoanStatus;
import org.acme.model.Book;
import org.acme.model.User;
import org.acme.model.BookStatus;
import org.acme.model.Reservation;
import org.acme.model.ReservationStatus;
import org.acme.repository.LoanRepository;
import org.acme.repository.BookRepository;
import org.acme.repository.UserRepository;
import org.acme.repository.ReservationRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LoanService {

    @Inject
    private LoanRepository loanRepository;

    @Inject
    private BookRepository bookRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private FineCalculationService fineCalculationService;

    @Inject
    private NotificationService notificationService;

    @Inject
    private ReservationService reservationService;

    @Inject
    private ReservationRepository reservationRepository;

    @Transactional
    public List<Loan> getAllLoans() {
        List<Loan> loans = loanRepository.listAll();

        // Force loading of related entities to avoid lazy loading issues
        for (Loan loan : loans) {
            if (loan.getUser() != null) {
                loan.getUser().getFirstName();
                loan.getUser().getLastName();
                loan.getUser().getEmail();
            }
            if (loan.getBook() != null) {
                Book book = loan.getBook();
                book.getTitle();
                if (book.getAuthor() != null) {
                    book.getAuthor().getFirstName();
                    book.getAuthor().getLastName();
                }
                if (book.getCategory() != null) {
                    book.getCategory().getName();
                }
            }
        }

        return loans;
    }

    public Optional<Loan> getLoanById(Long id) {
        return loanRepository.findByIdOptional(id);
    }

    @Transactional
    public List<Loan> getLoansByUserId(Long userId) {
        List<Loan> loans = loanRepository.list("user.id", userId);

        // Force loading of related entities to avoid lazy loading issues
        for (Loan loan : loans) {
            if (loan.getUser() != null) {
                loan.getUser().getFirstName();
                loan.getUser().getLastName();
                loan.getUser().getEmail();
            }
            if (loan.getBook() != null) {
                Book book = loan.getBook();
                book.getTitle();
                if (book.getAuthor() != null) {
                    book.getAuthor().getFirstName();
                    book.getAuthor().getLastName();
                }
                if (book.getCategory() != null) {
                    book.getCategory().getName();
                }
            }
        }

        return loans;
    }

    public Loan createLoan(Loan loan) {
        if (loan == null) {
            return null;
        }
        loanRepository.persist(loan);
        return loan;
    }

    public void deleteLoan(Long id) {
        loanRepository.deleteById(id);
    }

    @Transactional
    public Loan borrowBook(Long bookId, Long userId) {
        // Find the book
        Optional<Book> bookOpt = bookRepository.findByIdOptional(bookId);
        if (!bookOpt.isPresent()) {
            throw new RuntimeException("Book not found");
        }
        Book book = bookOpt.get();

        // Ensure related entities are loaded (fix lazy loading)
        if (book.getAuthor() != null) {
            // Force loading of author fields
            book.getAuthor().getFirstName();
            book.getAuthor().getLastName();
        }
        if (book.getCategory() != null) {
            // Force loading of category fields
            book.getCategory().getName();
        }

        // Check if book is available
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new RuntimeException("Book is not available for borrowing");
        }

        // Find the user
        Optional<User> userOpt = userRepository.findByIdOptional(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOpt.get();

        // Force loading of user fields
        user.getFirstName();
        user.getLastName();
        user.getEmail();

        // Check if user has an active reservation for this book
        List<Reservation> userReservations = reservationService.getBookReservationQueue(bookId)
                .stream()
                .filter(r -> r.getStatus() == ReservationStatus.ACTIVE && r.getUser().getId().equals(userId))
                .toList();

        // If user has an active reservation, mark it as fulfilled
        if (!userReservations.isEmpty()) {
            Reservation userReservation = userReservations.get(0);
            userReservation.setStatus(ReservationStatus.FULFILLED);
            userReservation.setUpdatedAt(LocalDateTime.now());
            reservationRepository.persist(userReservation);
        }

        // Create the loan
        Loan loan = new Loan();
        loan.setBook(book);
        loan.setUser(user);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(30)); // 30 days loan period
        loan.setStatus(LoanStatus.BORROWED); // Set loan status
        loan.setCreatedAt(LocalDateTime.now());
        loan.setUpdatedAt(LocalDateTime.now());

        // Update book status to BORROWED
        book.setStatus(BookStatus.BORROWED);
        book.setUpdatedAt(LocalDateTime.now());
        bookRepository.persist(book);

        // Save the loan
        loanRepository.persist(loan);

        // Create notification for book borrowed
        notificationService.createBookBorrowedNotification(userId, book.getTitle());

        return loan;
    }

    @Transactional
    public Loan returnBook(Long loanId) {
        // Find the loan
        Optional<Loan> loanOpt = loanRepository.findByIdOptional(loanId);
        if (!loanOpt.isPresent()) {
            throw new RuntimeException("Loan not found");
        }
        Loan loan = loanOpt.get();

        // Force loading of related entities
        if (loan.getBook() != null) {
            Book book = loan.getBook();
            if (book.getAuthor() != null) {
                book.getAuthor().getFirstName();
                book.getAuthor().getLastName();
            }
            if (book.getCategory() != null) {
                book.getCategory().getName();
            }
        }
        if (loan.getUser() != null) {
            loan.getUser().getFirstName();
            loan.getUser().getLastName();
        }

        // Check if already returned
        if (loan.getReturnDate() != null) {
            throw new RuntimeException("Book has already been returned");
        }

        // Set return date and status
        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);
        loan.setUpdatedAt(LocalDateTime.now());

        Book book = loan.getBook();

        // Check for active reservations
        List<Reservation> activeReservations = reservationService.getBookReservationQueue(book.getId())
                .stream()
                .filter(r -> r.getStatus() == ReservationStatus.ACTIVE)
                .sorted((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt())) // Sort by creation date (FIFO)
                .toList();

        if (!activeReservations.isEmpty()) {
            // Get the first person in the reservation queue
            Reservation nextReservation = activeReservations.get(0);
            User nextUser = nextReservation.getUser();

            // Force loading of next user fields
            nextUser.getFirstName();
            nextUser.getLastName();
            nextUser.getEmail();

            // Create a new loan for the next user
            Loan newLoan = new Loan();
            newLoan.setBook(book);
            newLoan.setUser(nextUser);
            newLoan.setBorrowDate(LocalDate.now());
            newLoan.setDueDate(LocalDate.now().plusDays(30)); // 30 days loan period
            newLoan.setStatus(LoanStatus.BORROWED); // Set loan status
            newLoan.setCreatedAt(LocalDateTime.now());
            newLoan.setUpdatedAt(LocalDateTime.now());
            // IMPORTANT: Do NOT set returnDate - this should be null for active loans
            newLoan.setReturnDate(null);

            // Book remains BORROWED (assigned to next user)
            System.out.println("DEBUG: Setting book " + book.getId() + " status to BORROWED");
            book.setStatus(BookStatus.BORROWED);
            book.setUpdatedAt(LocalDateTime.now());

            // Mark the reservation as FULFILLED
            nextReservation.setStatus(ReservationStatus.FULFILLED);
            nextReservation.setUpdatedAt(LocalDateTime.now());

            // Persist changes
            loanRepository.persist(newLoan);
            System.out.println("DEBUG: Before persisting book " + book.getId() + " with status " + book.getStatus());
            bookRepository.persist(book);
            System.out.println("DEBUG: After persisting book " + book.getId());
            reservationRepository.persist(nextReservation); // Persist the reservation

            // Create notifications
            notificationService.createBookReturnedNotification(loan.getUser().getId(), book.getTitle());
            notificationService.createReservationAvailableNotification(nextUser.getId(), book.getTitle());
            notificationService.createBookBorrowedNotification(nextUser.getId(), book.getTitle());

        } else {
            // No active reservations, book becomes available
            book.setStatus(BookStatus.AVAILABLE);
            book.setUpdatedAt(LocalDateTime.now());
            bookRepository.persist(book);

            // Create notification for book returned
            notificationService.createBookReturnedNotification(loan.getUser().getId(), book.getTitle());
        }

        // Calculate and create fine if overdue
        if (loan.getDueDate() != null && LocalDate.now().isAfter(loan.getDueDate())) {
            try {
                fineCalculationService.calculateOverdueFines();
            } catch (Exception e) {
                System.err.println("Warning: Could not calculate overdue fines: " + e.getMessage());
            }
        }

        // Save the updated loan
        loanRepository.persist(loan);

        return loan;
    }

    public List<Loan> getActiveLoansByBookId(Long bookId) {
        return loanRepository.list("book.id = ?1 and returnDate is null", bookId);
    }

    public List<Loan> getOverdueLoans() {
        return loanRepository.list("returnDate is null and dueDate < ?1", LocalDate.now());
    }
}