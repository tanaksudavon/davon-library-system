package org.acme.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.acme.model.*;
import org.acme.repository.BookRepository;
import org.acme.repository.LoanRepository;
import org.acme.repository.ReservationRepository;
import org.acme.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Debug/Test class for ReservationService
 * Used for debugging reservation management functionality
 */
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReservationService reservationService;

    private User testUser1;
    private User testUser2;
    private Book testBook1;
    private Book testBook2;
    private Author testAuthor;
    private Category testCategory;
    private Reservation testReservation1;
    private Reservation testReservation2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Debug: Setup test data
        System.out.println("DEBUG: Setting up ReservationServiceTest");

        // Create test users
        testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setUsername("testuser1");
        testUser1.setFirstName("Test");
        testUser1.setLastName("User1");
        testUser1.setEmail("test1@example.com");

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("testuser2");
        testUser2.setFirstName("Test");
        testUser2.setLastName("User2");
        testUser2.setEmail("test2@example.com");

        // Create test author and category
        testAuthor = new Author();
        testAuthor.setId(1L);
        testAuthor.setFirstName("Test");
        testAuthor.setLastName("Author");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Fiction");

        // Create test books
        testBook1 = new Book();
        testBook1.setId(1L);
        testBook1.setTitle("Available Book");
        testBook1.setIsbn("1234567890");
        testBook1.setAuthor(testAuthor);
        testBook1.setCategory(testCategory);
        testBook1.setStatus(BookStatus.AVAILABLE);

        testBook2 = new Book();
        testBook2.setId(2L);
        testBook2.setTitle("Borrowed Book");
        testBook2.setIsbn("0987654321");
        testBook2.setAuthor(testAuthor);
        testBook2.setCategory(testCategory);
        testBook2.setStatus(BookStatus.BORROWED);

        // Create test reservations
        testReservation1 = new Reservation();
        testReservation1.setId(1L);
        testReservation1.setUser(testUser1);
        testReservation1.setBook(testBook1);
        testReservation1.setReservationDate(LocalDate.now());
        testReservation1.setExpirationDate(LocalDate.now().plusDays(7));
        testReservation1.setStatus(ReservationStatus.ACTIVE);
        testReservation1.setQueuePosition(1);
        testReservation1.setCreatedAt(LocalDateTime.now());
        testReservation1.setUpdatedAt(LocalDateTime.now());

        testReservation2 = new Reservation();
        testReservation2.setId(2L);
        testReservation2.setUser(testUser2);
        testReservation2.setBook(testBook1);
        testReservation2.setReservationDate(LocalDate.now());
        testReservation2.setExpirationDate(LocalDate.now().plusDays(7));
        testReservation2.setStatus(ReservationStatus.ACTIVE);
        testReservation2.setQueuePosition(2);
        testReservation2.setCreatedAt(LocalDateTime.now());
        testReservation2.setUpdatedAt(LocalDateTime.now());

        System.out.println("DEBUG: Test data initialized - Users: " + testUser1.getUsername() + ", " +
                testUser2.getUsername() + ", Books: " + testBook1.getTitle() + ", " + testBook2.getTitle());
    }

    @Test
    @DisplayName("Should create reservation for available book successfully")
    void testCreateReservation_AvailableBook_Success() {
        System.out.println("DEBUG: Testing createReservation - Available book success scenario");

        // Given
        Long bookId = 1L;
        Long userId = 1L;

        when(bookRepository.findByIdOptional(bookId)).thenReturn(Optional.of(testBook1));
        when(userRepository.findByIdOptional(userId)).thenReturn(Optional.of(testUser1));
        when(reservationRepository.count(eq("user.id = ?1 and status = ?2"), eq(userId), eq(ReservationStatus.ACTIVE)))
                .thenReturn(0L);
        when(reservationRepository.count(eq("user.id = ?1 and book.id = ?2 and status = ?3"),
                eq(userId), eq(bookId), eq(ReservationStatus.ACTIVE))).thenReturn(0L);
        when(reservationRepository.list(anyString(), eq(bookId), eq(ReservationStatus.ACTIVE)))
                .thenReturn(Collections.emptyList());

        // When
        Reservation result = reservationService.createReservation(bookId, userId);

        // Then
        assertNotNull(result);
        assertEquals(testBook1, result.getBook());
        assertEquals(testUser1, result.getUser());
        assertEquals(ReservationStatus.ACTIVE, result.getStatus());
        assertEquals(LocalDate.now(), result.getReservationDate());
        assertEquals(LocalDate.now().plusDays(7), result.getExpirationDate());
        assertEquals(1, result.getQueuePosition());

        verify(bookRepository, times(1)).findByIdOptional(bookId);
        verify(userRepository, times(1)).findByIdOptional(userId);
        verify(reservationRepository, times(1)).persist((Reservation) any());
        verify(bookRepository, times(1)).persist(testBook1);
        verify(notificationService, times(1)).createBookReservedNotification(userId, testBook1.getTitle());

        // Book should be marked as RESERVED
        assertEquals(BookStatus.RESERVED, testBook1.getStatus());

        System.out.println("DEBUG: Reservation created successfully for available book");
        System.out.println("DEBUG: Queue position: " + result.getQueuePosition() + ", Status: " + result.getStatus());
    }

    @Test
    @DisplayName("Should create reservation for borrowed book successfully")
    void testCreateReservation_BorrowedBook_Success() {
        System.out.println("DEBUG: Testing createReservation - Borrowed book success scenario");

        // Given
        Long bookId = 2L;
        Long userId = 1L;

        when(bookRepository.findByIdOptional(bookId)).thenReturn(Optional.of(testBook2));
        when(userRepository.findByIdOptional(userId)).thenReturn(Optional.of(testUser1));
        when(reservationRepository.count(eq("user.id = ?1 and status = ?2"), eq(userId), eq(ReservationStatus.ACTIVE)))
                .thenReturn(0L);
        when(reservationRepository.count(eq("user.id = ?1 and book.id = ?2 and status = ?3"),
                eq(userId), eq(bookId), eq(ReservationStatus.ACTIVE))).thenReturn(0L);
        when(reservationRepository.list(anyString(), eq(bookId), eq(ReservationStatus.ACTIVE)))
                .thenReturn(Collections.emptyList());

        // When
        Reservation result = reservationService.createReservation(bookId, userId);

        // Then
        assertNotNull(result);
        assertEquals(testBook2, result.getBook());
        assertEquals(testUser1, result.getUser());
        assertEquals(ReservationStatus.ACTIVE, result.getStatus());

        verify(reservationRepository, times(1)).persist((Reservation) any());
        verify(notificationService, times(1)).createBookReservedNotification(userId, testBook2.getTitle());

        // Book should remain BORROWED (not changed to RESERVED)
        assertEquals(BookStatus.BORROWED, testBook2.getStatus());
        verify(bookRepository, never()).persist(testBook2);

        System.out.println("DEBUG: Reservation created successfully for borrowed book");
        System.out.println("DEBUG: Book status remained: " + testBook2.getStatus());
    }

    @Test
    @DisplayName("Should throw NotFoundException when book not found")
    void testCreateReservation_BookNotFound() {
        System.out.println("DEBUG: Testing createReservation - Book not found scenario");

        // Given
        Long nonExistentBookId = 999L;
        Long userId = 1L;

        when(bookRepository.findByIdOptional(nonExistentBookId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            reservationService.createReservation(nonExistentBookId, userId);
        });

        assertEquals("Book not found with ID: " + nonExistentBookId, exception.getMessage());
        verify(bookRepository, times(1)).findByIdOptional(nonExistentBookId);
        verify(reservationRepository, never()).persist((Reservation) any());

        System.out.println("DEBUG: NotFoundException thrown for non-existent book ID: " + nonExistentBookId);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when book is not available for reservation")
    void testCreateReservation_BookNotAvailable() {
        System.out.println("DEBUG: Testing createReservation - Book not available scenario");

        // Given
        Long bookId = 1L;
        Long userId = 1L;
        testBook1.setStatus(BookStatus.MAINTENANCE); // Not available for reservation

        when(bookRepository.findByIdOptional(bookId)).thenReturn(Optional.of(testBook1));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            reservationService.createReservation(bookId, userId);
        });

        assertEquals("Book is not available for reservation.", exception.getMessage());
        verify(reservationRepository, never()).persist((Reservation) any());

        System.out.println("DEBUG: IllegalStateException thrown for book in maintenance");
    }

    @Test
    @DisplayName("Should throw BadRequestException when user has too many active reservations")
    void testCreateReservation_TooManyReservations() {
        System.out.println("DEBUG: Testing createReservation - Too many reservations scenario");

        // Given
        Long bookId = 1L;
        Long userId = 1L;

        when(bookRepository.findByIdOptional(bookId)).thenReturn(Optional.of(testBook1));
        when(reservationRepository.count(eq("user.id = ?1 and status = ?2"), eq(userId), eq(ReservationStatus.ACTIVE)))
                .thenReturn(5L); // Maximum allowed

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            reservationService.createReservation(bookId, userId);
        });

        assertTrue(exception.getMessage().contains("maximum number of active reservations"));
        verify(reservationRepository, never()).persist((Reservation) any());

        System.out.println("DEBUG: BadRequestException thrown for too many active reservations");
    }

    @Test
    @DisplayName("Should throw BadRequestException when user already reserved the book")
    void testCreateReservation_AlreadyReserved() {
        System.out.println("DEBUG: Testing createReservation - Already reserved scenario");

        // Given
        Long bookId = 1L;
        Long userId = 1L;

        when(bookRepository.findByIdOptional(bookId)).thenReturn(Optional.of(testBook1));
        when(reservationRepository.count(eq("user.id = ?1 and status = ?2"), eq(userId), eq(ReservationStatus.ACTIVE)))
                .thenReturn(0L);
        when(reservationRepository.count(eq("user.id = ?1 and book.id = ?2 and status = ?3"),
                eq(userId), eq(bookId), eq(ReservationStatus.ACTIVE))).thenReturn(1L);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            reservationService.createReservation(bookId, userId);
        });

        assertTrue(exception.getMessage().contains("already reserved this book"));
        verify(reservationRepository, never()).persist((Reservation) any());

        System.out.println("DEBUG: BadRequestException thrown for already reserved book");
    }

    @Test
    @DisplayName("Should handle null parameters in createReservation")
    void testCreateReservation_NullParameters() {
        System.out.println("DEBUG: Testing createReservation - Null parameters scenario");

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            reservationService.createReservation(null, 1L);
        });

        assertThrows(NullPointerException.class, () -> {
            reservationService.createReservation(1L, null);
        });

        System.out.println("DEBUG: NullPointerException thrown for null parameters - as expected");
    }

    @Test
    @DisplayName("Should cancel reservation successfully when no active loans")
    void testCancelReservation_Success_NoActiveLoans() {
        System.out.println("DEBUG: Testing cancelReservation - Success scenario with no active loans");

        // Given
        Long reservationId = 1L;
        Long userId = 1L;

        when(reservationRepository.findByIdOptional(reservationId)).thenReturn(Optional.of(testReservation1));
        when(loanRepository.list("book.id = ?1 and returnDate is null", testBook1.getId()))
                .thenReturn(Collections.emptyList());

        // When
        Reservation result = reservationService.cancelReservation(reservationId, userId);

        // Then
        assertNotNull(result);
        assertEquals(ReservationStatus.CANCELLED, result.getStatus());
        assertNotNull(result.getUpdatedAt());

        verify(reservationRepository, times(1)).findByIdOptional(reservationId);
        verify(reservationRepository, times(1)).persist(testReservation1);
        verify(loanRepository, times(1)).list("book.id = ?1 and returnDate is null", testBook1.getId());
        verify(bookRepository, times(1)).persist(testBook1);

        // Book should be marked as AVAILABLE when no active loans
        assertEquals(BookStatus.AVAILABLE, testBook1.getStatus());

        System.out.println("DEBUG: Reservation cancelled successfully - ID: " + reservationId);
        System.out.println("DEBUG: Book status changed to: " + testBook1.getStatus());
    }

    @Test
    @DisplayName("Should cancel reservation but keep book BORROWED when active loans exist")
    void testCancelReservation_Success_WithActiveLoans() {
        System.out.println("DEBUG: Testing cancelReservation - Success scenario with active loans");

        // Given
        Long reservationId = 1L;
        Long userId = 1L;

        // Create a mock active loan
        Loan activeLoan = new Loan();
        activeLoan.setId(100L);
        activeLoan.setBook(testBook1);
        activeLoan.setUser(testUser2); // Different user has the book
        activeLoan.setReturnDate(null); // Active loan (no return date)

        // Set the book status to BORROWED initially
        testBook1.setStatus(BookStatus.BORROWED);

        when(reservationRepository.findByIdOptional(reservationId)).thenReturn(Optional.of(testReservation1));
        when(loanRepository.list("book.id = ?1 and returnDate is null", testBook1.getId()))
                .thenReturn(Arrays.asList(activeLoan));

        // When
        Reservation result = reservationService.cancelReservation(reservationId, userId);

        // Then
        assertNotNull(result);
        assertEquals(ReservationStatus.CANCELLED, result.getStatus());
        assertNotNull(result.getUpdatedAt());

        verify(reservationRepository, times(1)).findByIdOptional(reservationId);
        verify(reservationRepository, times(1)).persist(testReservation1);
        verify(loanRepository, times(1)).list("book.id = ?1 and returnDate is null", testBook1.getId());

        // Book repository should NOT be called when there are active loans
        verify(bookRepository, never()).persist(testBook1);

        // Book should remain BORROWED when there are active loans
        assertEquals(BookStatus.BORROWED, testBook1.getStatus());

        System.out.println("DEBUG: Reservation cancelled successfully - ID: " + reservationId);
        System.out.println("DEBUG: Book status remained: " + testBook1.getStatus() + " (correctly stayed BORROWED)");
    }

    @Test
    @DisplayName("Should throw NotFoundException when reservation not found")
    void testCancelReservation_NotFound() {
        System.out.println("DEBUG: Testing cancelReservation - Not found scenario");

        // Given
        Long nonExistentId = 999L;
        Long userId = 1L;

        when(reservationRepository.findByIdOptional(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            reservationService.cancelReservation(nonExistentId, userId);
        });

        assertEquals("Reservation not found with ID: " + nonExistentId, exception.getMessage());
        verify(reservationRepository, times(1)).findByIdOptional(nonExistentId);
        verify(reservationRepository, never()).persist((Reservation) any());

        System.out.println("DEBUG: NotFoundException thrown for non-existent reservation ID: " + nonExistentId);
    }

    @Test
    @DisplayName("Should throw ForbiddenException when user doesn't own reservation")
    void testCancelReservation_Forbidden() {
        System.out.println("DEBUG: Testing cancelReservation - Forbidden scenario");

        // Given
        Long reservationId = 1L;
        Long wrongUserId = 2L; // Different user

        when(reservationRepository.findByIdOptional(reservationId)).thenReturn(Optional.of(testReservation1));

        // When & Then
        ForbiddenException exception = assertThrows(ForbiddenException.class, () -> {
            reservationService.cancelReservation(reservationId, wrongUserId);
        });

        assertEquals("User does not have permission to cancel this reservation.", exception.getMessage());
        verify(reservationRepository, never()).persist((Reservation) any());

        System.out.println("DEBUG: ForbiddenException thrown for wrong user attempting cancellation");
    }

    @Test
    @DisplayName("Should get user active reservations")
    void testGetUserActiveReservations_Success() {
        System.out.println("DEBUG: Testing getUserActiveReservations - Success scenario");

        // Given
        Long userId = 1L;
        List<Reservation> userReservations = Arrays.asList(testReservation1);

        @SuppressWarnings("unchecked")
        PanacheQuery<Reservation> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(userReservations);
        when(reservationRepository.find(eq("user.id = ?1 and status = ?2"), eq(userId), eq(ReservationStatus.ACTIVE)))
                .thenReturn(query);

        // When
        List<Reservation> result = reservationService.getUserActiveReservations(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReservation1, result.get(0));

        verify(reservationRepository, times(1)).find(eq("user.id = ?1 and status = ?2"), eq(userId),
                eq(ReservationStatus.ACTIVE));

        System.out.println("DEBUG: Found " + result.size() + " active reservations for user " + userId);
    }

    @Test
    @DisplayName("Should return empty list for user with no active reservations")
    void testGetUserActiveReservations_NoReservations() {
        System.out.println("DEBUG: Testing getUserActiveReservations - No reservations scenario");

        // Given
        Long userId = 2L;

        @SuppressWarnings("unchecked")
        PanacheQuery<Reservation> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(Collections.emptyList());
        when(reservationRepository.find(eq("user.id = ?1 and status = ?2"), eq(userId), eq(ReservationStatus.ACTIVE)))
                .thenReturn(query);

        // When
        List<Reservation> result = reservationService.getUserActiveReservations(userId);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());

        System.out.println("DEBUG: No active reservations found for user " + userId);
    }

    @Test
    @DisplayName("Should expire old reservations")
    void testExpireOldReservations_Success() {
        System.out.println("DEBUG: Testing expireOldReservations - Success scenario");

        // Given
        Reservation expiredReservation = new Reservation();
        expiredReservation.setId(3L);
        expiredReservation.setBook(testBook1);
        expiredReservation.setUser(testUser1);
        expiredReservation.setStatus(ReservationStatus.ACTIVE);
        expiredReservation.setExpirationDate(LocalDate.now().minusDays(1)); // Expired

        List<Reservation> expiredReservations = Arrays.asList(expiredReservation);

        @SuppressWarnings("unchecked")
        PanacheQuery<Reservation> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(expiredReservations);
        when(reservationRepository.find(eq("status = ?1 and expirationDate < ?2"),
                eq(ReservationStatus.ACTIVE), any(LocalDate.class)))
                .thenReturn(query);

        // When
        reservationService.expireOldReservations();

        // Then
        assertEquals(ReservationStatus.EXPIRED, expiredReservation.getStatus());
        assertEquals(BookStatus.AVAILABLE, testBook1.getStatus());

        verify(reservationRepository, times(1)).persist(expiredReservation);
        verify(bookRepository, times(1)).persist(testBook1);

        System.out.println("DEBUG: Expired " + expiredReservations.size() + " old reservations");
        System.out.println("DEBUG: Reservation status changed to: " + expiredReservation.getStatus());
    }

    @Test
    @DisplayName("Should get book reservation queue")
    void testGetBookReservationQueue_Success() {
        System.out.println("DEBUG: Testing getBookReservationQueue - Success scenario");

        // Given
        Long bookId = 1L;
        List<Reservation> bookReservations = Arrays.asList(testReservation1, testReservation2);

        @SuppressWarnings("unchecked")
        PanacheQuery<Reservation> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(bookReservations);
        when(reservationRepository.find(eq("book.id"), eq(bookId)))
                .thenReturn(query);

        // When
        List<Reservation> result = reservationService.getBookReservationQueue(bookId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testReservation1, result.get(0));
        assertEquals(testReservation2, result.get(1));

        verify(reservationRepository, times(1)).find(eq("book.id"), eq(bookId));

        System.out.println("DEBUG: Found " + result.size() + " reservations in queue for book " + bookId);
        System.out.println("DEBUG: Queue positions: " + result.get(0).getQueuePosition() + ", "
                + result.get(1).getQueuePosition());
    }

    @Test
    @DisplayName("Should handle null parameters in various methods")
    void testNullParameterHandling() {
        System.out.println("DEBUG: Testing null parameter handling");

        // Test getUserActiveReservations with null userId
        assertThrows(NullPointerException.class, () -> {
            reservationService.getUserActiveReservations(null);
        });

        // Test getBookReservationQueue with null bookId
        assertThrows(NullPointerException.class, () -> {
            reservationService.getBookReservationQueue(null);
        });

        // Test cancelReservation with null parameters
        assertThrows(NullPointerException.class, () -> {
            reservationService.cancelReservation(null, 1L);
        });

        assertThrows(NullPointerException.class, () -> {
            reservationService.cancelReservation(1L, null);
        });

        System.out.println("DEBUG: All null parameter scenarios handled correctly");
    }

    @Test
    @DisplayName("Should calculate correct queue position")
    void testQueuePositionCalculation() {
        System.out.println("DEBUG: Testing queue position calculation");

        // Given
        Long bookId = 1L;
        Long userId = 2L;

        // Mock existing reservations (2 active reservations already exist)
        List<Reservation> existingReservations = Arrays.asList(testReservation1, testReservation2);

        when(bookRepository.findByIdOptional(bookId)).thenReturn(Optional.of(testBook1));
        when(userRepository.findByIdOptional(userId)).thenReturn(Optional.of(testUser2));
        when(reservationRepository.count(eq("user.id = ?1 and status = ?2"), eq(userId), eq(ReservationStatus.ACTIVE)))
                .thenReturn(0L);
        when(reservationRepository.count(eq("user.id = ?1 and book.id = ?2 and status = ?3"),
                eq(userId), eq(bookId), eq(ReservationStatus.ACTIVE))).thenReturn(0L);
        when(reservationRepository.list(anyString(), eq(bookId), eq(ReservationStatus.ACTIVE)))
                .thenReturn(existingReservations);

        // When
        Reservation result = reservationService.createReservation(bookId, userId);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getQueuePosition()); // Should be position 3 (after 2 existing)

        System.out.println("DEBUG: Queue position calculated correctly: " + result.getQueuePosition());
        System.out.println("DEBUG: Existing reservations: " + existingReservations.size());
    }
}
