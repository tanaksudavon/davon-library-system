package org.acme.service;

import org.acme.model.Loan;
import org.acme.model.LoanStatus;
import org.acme.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.acme.model.Book;
import org.acme.model.User;

/**
 * Unit tests for LoanService class
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LoanService Tests")
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private Loan testLoan;
    private List<Loan> testLoans;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        // Create test Book and User objects
        Book testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setIsbn("978-0123456789");

        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        Book testBook2 = new Book();
        testBook2.setId(2L);
        testBook2.setTitle("Test Book 2");
        testBook2.setIsbn("978-0987654321");

        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("testuser2");
        testUser2.setEmail("test2@example.com");

        testLoan = new Loan(1L, testBook, testUser, today, null, today.plusDays(14),
                LoanStatus.BORROWED, 0, "Regular loan", now, now);

        Loan loan2 = new Loan(2L, testBook2, testUser2, today.minusDays(10), today.minusDays(5),
                today.minusDays(3), LoanStatus.RETURNED, 0, "Returned early", now, now);

        testLoans = Arrays.asList(testLoan, loan2);
    }

    @Test
    @DisplayName("Test getAllLoans returns all loans")
    void testGetAllLoans() {
        // Given
        when(loanRepository.listAll()).thenReturn(testLoans);

        // When
        List<Loan> result = loanService.getAllLoans();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testLoans, result);
        verify(loanRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Test getAllLoans returns empty list when no loans exist")
    void testGetAllLoansWhenEmpty() {
        // Given
        when(loanRepository.listAll()).thenReturn(Collections.emptyList());

        // When
        List<Loan> result = loanService.getAllLoans();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(loanRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Test getLoanById returns loan when found")
    void testGetLoanByIdFound() {
        // Given
        Long loanId = 1L;
        when(loanRepository.findByIdOptional(loanId)).thenReturn(Optional.of(testLoan));

        // When
        Optional<Loan> result = loanService.getLoanById(loanId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testLoan, result.get());
        assertEquals(1L, result.get().getBook().getId());
        assertEquals(1L, result.get().getUser().getId());
        assertEquals(LoanStatus.BORROWED, result.get().getStatus());
        verify(loanRepository, times(1)).findByIdOptional(loanId);
    }

    @Test
    @DisplayName("Test getLoanById returns empty when not found")
    void testGetLoanByIdNotFound() {
        // Given
        Long loanId = 999L;
        when(loanRepository.findByIdOptional(loanId)).thenReturn(Optional.empty());

        // When
        Optional<Loan> result = loanService.getLoanById(loanId);

        // Then
        assertFalse(result.isPresent());
        verify(loanRepository, times(1)).findByIdOptional(loanId);
    }

    @Test
    @DisplayName("Test getLoanById with null id")
    void testGetLoanByIdWithNullId() {
        // Given
        when(loanRepository.findByIdOptional(null)).thenReturn(Optional.empty());

        // When
        Optional<Loan> result = loanService.getLoanById(null);

        // Then
        assertFalse(result.isPresent());
        verify(loanRepository, times(1)).findByIdOptional(null);
    }

    @Test
    @DisplayName("Test createLoan successfully creates and returns loan")
    void testCreateLoanSuccess() {
        // Given
        Book testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("New Test Book");

        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("newtestuser");

        Loan newLoan = new Loan();
        newLoan.setBook(testBook);
        newLoan.setUser(testUser);
        newLoan.setBorrowDate(LocalDate.now());
        newLoan.setDueDate(LocalDate.now().plusDays(14));
        newLoan.setStatus(LoanStatus.BORROWED);

        doNothing().when(loanRepository).persist(newLoan);

        // When
        Loan result = loanService.createLoan(newLoan);

        // Then
        assertNotNull(result);
        assertEquals(newLoan, result);
        assertEquals(1L, result.getBook().getId());
        assertEquals(1L, result.getUser().getId());
        assertEquals(LoanStatus.BORROWED, result.getStatus());
        verify(loanRepository, times(1)).persist(newLoan);
    }

    @Test
    @DisplayName("Test createLoan with null loan")
    void testCreateLoanWithNullLoan() {
        // Given
        Loan nullLoan = null;

        // When
        Loan result = loanService.createLoan(nullLoan);

        // Then
        assertNull(result);
        verify(loanRepository, times(0)).persist(any(Loan.class));
    }

    @Test
    @DisplayName("Test createLoan with complete loan data")
    void testCreateLoanWithCompleteData() {
        // Given
        LocalDate borrowDate = LocalDate.now();
        LocalDate dueDate = LocalDate.now().plusDays(14);

        Book completeBook = new Book();
        completeBook.setId(1L);
        completeBook.setTitle("Complete Test Book");

        User completeUser = new User();
        completeUser.setId(1L);
        completeUser.setUsername("completeuser");

        Loan completeLoan = new Loan();
        completeLoan.setBook(completeBook);
        completeLoan.setUser(completeUser);
        completeLoan.setBorrowDate(borrowDate);
        completeLoan.setDueDate(dueDate);
        completeLoan.setStatus(LoanStatus.BORROWED);
        completeLoan.setNotes("Complete loan with all details");
        completeLoan.setCreatedAt(LocalDateTime.now());
        completeLoan.setUpdatedAt(LocalDateTime.now());

        doNothing().when(loanRepository).persist(completeLoan);

        // When
        Loan result = loanService.createLoan(completeLoan);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getBook().getId());
        assertEquals(1L, result.getUser().getId());
        assertEquals(borrowDate, result.getBorrowDate());
        assertEquals(dueDate, result.getDueDate());
        assertEquals(LoanStatus.BORROWED, result.getStatus());
        assertEquals("Complete loan with all details", result.getNotes());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(loanRepository, times(1)).persist(completeLoan);
    }

    @Test
    @DisplayName("Test deleteLoan successfully deletes existing loan")
    void testDeleteLoanSuccess() {
        // Given
        Long loanId = 1L;
        when(loanRepository.deleteById(loanId)).thenReturn(true);

        // When
        assertDoesNotThrow(() -> loanService.deleteLoan(loanId));

        // Then
        verify(loanRepository, times(1)).deleteById(loanId);
    }

    @Test
    @DisplayName("Test deleteLoan with non-existent loan")
    void testDeleteLoanNotFound() {
        // Given
        Long loanId = 999L;
        when(loanRepository.deleteById(loanId)).thenReturn(false);

        // When
        assertDoesNotThrow(() -> loanService.deleteLoan(loanId));

        // Then
        verify(loanRepository, times(1)).deleteById(loanId);
    }

    @Test
    @DisplayName("Test deleteLoan with null id")
    void testDeleteLoanWithNullId() {
        // Given
        when(loanRepository.deleteById(null)).thenReturn(false);

        // When
        assertDoesNotThrow(() -> loanService.deleteLoan(null));

        // Then
        verify(loanRepository, times(1)).deleteById(null);
    }

    @Test
    @DisplayName("Test service behavior with repository exceptions")
    void testServiceWithRepositoryExceptions() {
        // Given
        when(loanRepository.listAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> loanService.getAllLoans());
        verify(loanRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Test createLoan persistence behavior")
    void testCreateLoanPersistenceBehavior() {
        // Given
        Book persistenceBook = new Book();
        persistenceBook.setId(1L);
        persistenceBook.setTitle("Persistence Test Book");

        User persistenceUser = new User();
        persistenceUser.setId(1L);
        persistenceUser.setUsername("persistenceuser");

        Loan loan = new Loan();
        loan.setBook(persistenceBook);
        loan.setUser(persistenceUser);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setStatus(LoanStatus.BORROWED);

        doAnswer(invocation -> {
            Loan persistedLoan = invocation.getArgument(0);
            persistedLoan.setId(1L); // Simulate ID assignment
            persistedLoan.setCreatedAt(LocalDateTime.now());
            persistedLoan.setUpdatedAt(LocalDateTime.now());
            return null;
        }).when(loanRepository).persist(loan);

        // When
        Loan result = loanService.createLoan(loan);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getBook().getId());
        assertEquals(1L, result.getUser().getId());
        assertEquals(1L, result.getId()); // Verify ID was set
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(loanRepository, times(1)).persist(loan);
    }

    @Test
    @DisplayName("Test loan status scenarios")
    void testLoanStatusScenarios() {
        // Test different loan statuses
        Loan borrowedLoan = new Loan();
        borrowedLoan.setStatus(LoanStatus.BORROWED);
        assertEquals(LoanStatus.BORROWED, borrowedLoan.getStatus());

        Loan returnedLoan = new Loan();
        returnedLoan.setStatus(LoanStatus.RETURNED);
        returnedLoan.setReturnDate(LocalDate.now());
        assertEquals(LoanStatus.RETURNED, returnedLoan.getStatus());
        assertNotNull(returnedLoan.getReturnDate());

        Loan overdueLoan = new Loan();
        overdueLoan.setStatus(LoanStatus.OVERDUE);
        assertEquals(LoanStatus.OVERDUE, overdueLoan.getStatus());

        Loan lostLoan = new Loan();
        lostLoan.setStatus(LoanStatus.LOST);
        lostLoan.setNotes("Book reported lost");
        assertEquals(LoanStatus.LOST, lostLoan.getStatus());
        assertEquals("Book reported lost", lostLoan.getNotes());
    }
}