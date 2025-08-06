package org.acme.service;

import org.acme.model.*;
import org.acme.repository.FineRepository;
import org.acme.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jakarta.ws.rs.NotFoundException;

import java.math.BigDecimal;
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
 * Debug/Test class for FineCalculationService
 * Used for debugging fine calculation functionality
 */
class FineCalculationServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private FineRepository fineRepository;

    @InjectMocks
    private FineCalculationService fineCalculationService;

    private Loan testLoan1;
    private Loan testLoan2;
    private Fine testFine1;
    private Fine testFine2;
    private User testUser;
    private Book testBook;
    private Author testAuthor;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Debug: Setup test data
        System.out.println("DEBUG: Setting up FineCalculationServiceTest");

        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");

        // Create test author and category
        testAuthor = new Author();
        testAuthor.setId(1L);
        testAuthor.setFirstName("Test");
        testAuthor.setLastName("Author");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Fiction");

        // Create test book
        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setIsbn("1234567890");
        testBook.setAuthor(testAuthor);
        testBook.setCategory(testCategory);
        testBook.setStatus(BookStatus.BORROWED);

        // Create overdue loan (5 days overdue)
        testLoan1 = new Loan();
        testLoan1.setId(1L);
        testLoan1.setUser(testUser);
        testLoan1.setBook(testBook);
        testLoan1.setBorrowDate(LocalDate.now().minusDays(35));
        testLoan1.setDueDate(LocalDate.now().minusDays(5)); // 5 days overdue
        testLoan1.setCreatedAt(LocalDateTime.now().minusDays(35));

        // Create very overdue loan (60 days overdue - should hit maximum fine)
        testLoan2 = new Loan();
        testLoan2.setId(2L);
        testLoan2.setUser(testUser);
        testLoan2.setBook(testBook);
        testLoan2.setBorrowDate(LocalDate.now().minusDays(90));
        testLoan2.setDueDate(LocalDate.now().minusDays(60)); // 60 days overdue
        testLoan2.setCreatedAt(LocalDateTime.now().minusDays(90));

        // Create test fines
        testFine1 = new Fine();
        testFine1.setId(1L);
        testFine1.setUser(testUser);
        testFine1.setLoan(testLoan1);
        testFine1.setAmount(new BigDecimal("2.50"));
        testFine1.setReason("Overdue book fine for book: Test Book");
        testFine1.setStatus(FineStatus.PENDING);
        testFine1.setIssuedDate(LocalDate.now());

        testFine2 = new Fine();
        testFine2.setId(2L);
        testFine2.setUser(testUser);
        testFine2.setLoan(testLoan2);
        testFine2.setAmount(new BigDecimal("25.00")); // Maximum fine
        testFine2.setReason("Overdue book fine for book: Test Book");
        testFine2.setStatus(FineStatus.PENDING);
        testFine2.setIssuedDate(LocalDate.now());

        System.out.println("DEBUG: Test data initialized - User: " + testUser.getUsername() +
                ", Book: " + testBook.getTitle() + ", Overdue loans: 2");
    }

    @Test
    @DisplayName("Should calculate overdue fines for overdue loans")
    void testCalculateOverdueFines_Success() {
        System.out.println("DEBUG: Testing calculateOverdueFines - Success scenario");

        // Given
        List<Loan> overdueLoans = Arrays.asList(testLoan1, testLoan2);
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(overdueLoans);
        when(fineRepository.findByLoanId(1L)).thenReturn(Collections.emptyList());
        when(fineRepository.findByLoanId(2L)).thenReturn(Collections.emptyList());

        // When
        List<Fine> result = fineCalculationService.calculateOverdueFines();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // Verify fine amounts
        Fine fine1 = result.get(0);
        Fine fine2 = result.get(1);

        assertEquals(new BigDecimal("2.50"), fine1.getAmount()); // 5 days * $0.50
        assertEquals(new BigDecimal("25.00"), fine2.getAmount()); // Capped at maximum
        assertEquals(FineStatus.PENDING, fine1.getStatus());
        assertEquals(FineStatus.PENDING, fine2.getStatus());

        verify(loanRepository, times(1)).findOverdueLoans(any(LocalDate.class));
        verify(fineRepository, times(2)).persist((Fine) any());

        System.out.println("DEBUG: calculateOverdueFines created " + result.size() + " fines");
        System.out.println("DEBUG: Fine 1 amount: $" + fine1.getAmount() + ", Fine 2 amount: $" + fine2.getAmount());
    }

    @Test
    @DisplayName("Should not create duplicate fines for existing overdue loans")
    void testCalculateOverdueFines_ExistingFines() {
        System.out.println("DEBUG: Testing calculateOverdueFines - Existing fines scenario");

        // Given
        List<Loan> overdueLoans = Arrays.asList(testLoan1);
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(overdueLoans);
        when(fineRepository.findByLoanId(1L)).thenReturn(Arrays.asList(testFine1));

        // When
        List<Fine> result = fineCalculationService.calculateOverdueFines();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size()); // No new fines created

        verify(loanRepository, times(1)).findOverdueLoans(any(LocalDate.class));
        verify(fineRepository, never()).persist((Fine) any());

        System.out.println("DEBUG: No new fines created - existing fine found");
    }

    @Test
    @DisplayName("Should handle empty overdue loans list")
    void testCalculateOverdueFines_NoOverdueLoans() {
        System.out.println("DEBUG: Testing calculateOverdueFines - No overdue loans scenario");

        // Given
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(Collections.emptyList());

        // When
        List<Fine> result = fineCalculationService.calculateOverdueFines();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());

        verify(loanRepository, times(1)).findOverdueLoans(any(LocalDate.class));
        verify(fineRepository, never()).persist((Fine) any());

        System.out.println("DEBUG: No overdue loans found - no fines calculated");
    }

    @Test
    @DisplayName("Should handle null loan data gracefully")
    void testCalculateOverdueFines_NullLoanData() {
        System.out.println("DEBUG: Testing calculateOverdueFines - Null loan data scenario");

        // Given
        Loan nullLoan = null;
        Loan loanWithNullDueDate = new Loan();
        loanWithNullDueDate.setId(3L);
        loanWithNullDueDate.setDueDate(null);

        List<Loan> loansWithNulls = Arrays.asList(nullLoan, loanWithNullDueDate, testLoan1);
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(loansWithNulls);
        when(fineRepository.findByLoanId(1L)).thenReturn(Collections.emptyList());

        // When
        List<Fine> result = fineCalculationService.calculateOverdueFines();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // Only valid loan processed

        verify(fineRepository, times(1)).persist((Fine) any());

        System.out.println("DEBUG: Handled null loan data - processed only valid loans");
    }

    @Test
    @DisplayName("Should get total fines for user")
    void testGetTotalFinesForUser_Success() {
        System.out.println("DEBUG: Testing getTotalFinesForUser - Success scenario");

        // Given
        Long userId = 1L;
        List<Fine> userFines = Arrays.asList(testFine1, testFine2);
        when(fineRepository.findByUserIdAndStatus(userId, FineStatus.PENDING)).thenReturn(userFines);

        // When
        BigDecimal result = fineCalculationService.getTotalFinesForUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("27.50"), result); // $2.50 + $25.00

        verify(fineRepository, times(1)).findByUserIdAndStatus(userId, FineStatus.PENDING);

        System.out.println("DEBUG: Total fines for user " + userId + ": $" + result);
    }

    @Test
    @DisplayName("Should return zero for user with no fines")
    void testGetTotalFinesForUser_NoFines() {
        System.out.println("DEBUG: Testing getTotalFinesForUser - No fines scenario");

        // Given
        Long userId = 2L;
        when(fineRepository.findByUserIdAndStatus(userId, FineStatus.PENDING)).thenReturn(Collections.emptyList());

        // When
        BigDecimal result = fineCalculationService.getTotalFinesForUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);

        verify(fineRepository, times(1)).findByUserIdAndStatus(userId, FineStatus.PENDING);

        System.out.println("DEBUG: No fines found for user " + userId + " - returned $0.00");
    }

    @Test
    @DisplayName("Should handle null user ID in getTotalFinesForUser")
    void testGetTotalFinesForUser_NullUserId() {
        System.out.println("DEBUG: Testing getTotalFinesForUser - Null user ID scenario");

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            fineCalculationService.getTotalFinesForUser(null);
        });

        System.out.println("DEBUG: NullPointerException thrown for null user ID - as expected");
    }

    @Test
    @DisplayName("Should handle fines with null amounts")
    void testGetTotalFinesForUser_NullAmounts() {
        System.out.println("DEBUG: Testing getTotalFinesForUser - Null amounts scenario");

        // Given
        Long userId = 1L;
        Fine fineWithNullAmount = new Fine();
        fineWithNullAmount.setId(3L);
        fineWithNullAmount.setAmount(null);

        List<Fine> userFines = Arrays.asList(testFine1, fineWithNullAmount, testFine2);
        when(fineRepository.findByUserIdAndStatus(userId, FineStatus.PENDING)).thenReturn(userFines);

        // When
        BigDecimal result = fineCalculationService.getTotalFinesForUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("27.50"), result); // Null amount ignored

        System.out.println("DEBUG: Null amounts filtered out - total: $" + result);
    }

    @Test
    @DisplayName("Should pay fine successfully")
    void testPayFine_Success() {
        System.out.println("DEBUG: Testing payFine - Success scenario");

        // Given
        Long fineId = 1L;
        when(fineRepository.findByIdOptional(fineId)).thenReturn(Optional.of(testFine1));

        // When
        Fine result = fineCalculationService.payFine(fineId);

        // Then
        assertNotNull(result);
        assertEquals(FineStatus.PAID, result.getStatus());
        assertEquals(LocalDate.now(), result.getPaidDate());

        verify(fineRepository, times(1)).findByIdOptional(fineId);
        verify(fineRepository, times(1)).persist(testFine1);

        System.out.println("DEBUG: Fine " + fineId + " paid successfully - status: " + result.getStatus());
    }

    @Test
    @DisplayName("Should throw NotFoundException when fine not found")
    void testPayFine_NotFound() {
        System.out.println("DEBUG: Testing payFine - Not found scenario");

        // Given
        Long nonExistentId = 999L;
        when(fineRepository.findByIdOptional(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            fineCalculationService.payFine(nonExistentId);
        });

        assertEquals("Fine not found with ID: " + nonExistentId, exception.getMessage());
        verify(fineRepository, times(1)).findByIdOptional(nonExistentId);
        verify(fineRepository, never()).persist((Fine) any());

        System.out.println("DEBUG: NotFoundException thrown for non-existent fine ID: " + nonExistentId);
    }

    @Test
    @DisplayName("Should handle null fine ID in payFine")
    void testPayFine_NullFineId() {
        System.out.println("DEBUG: Testing payFine - Null fine ID scenario");

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            fineCalculationService.payFine(null);
        });

        System.out.println("DEBUG: NullPointerException thrown for null fine ID - as expected");
    }

    @Test
    @DisplayName("Should handle fine with complex entity relationships")
    void testPayFine_ComplexRelationships() {
        System.out.println("DEBUG: Testing payFine - Complex relationships scenario");

        // Given
        Long fineId = 1L;
        // Set up the fine with all related entities
        testFine1.setLoan(testLoan1);
        testFine1.setUser(testUser);

        when(fineRepository.findByIdOptional(fineId)).thenReturn(Optional.of(testFine1));

        // When
        Fine result = fineCalculationService.payFine(fineId);

        // Then
        assertNotNull(result);
        assertEquals(FineStatus.PAID, result.getStatus());
        assertNotNull(result.getPaidDate());

        verify(fineRepository, times(1)).persist(testFine1);

        System.out.println("DEBUG: Fine with complex relationships paid successfully");
        System.out.println("DEBUG: Related user: " + result.getUser().getUsername());
        System.out.println("DEBUG: Related book: " + result.getLoan().getBook().getTitle());
    }

    @Test
    @DisplayName("Should verify fine calculation constants")
    void testFineCalculationConstants() {
        System.out.println("DEBUG: Testing fine calculation constants");

        // Test that the constants are as expected (indirectly through calculation)
        // 1 day overdue should be $0.50
        // 50+ days should be capped at $25.00

        // Create loans with specific overdue periods
        Loan oneDayOverdue = new Loan();
        oneDayOverdue.setId(10L);
        oneDayOverdue.setUser(testUser);
        oneDayOverdue.setBook(testBook);
        oneDayOverdue.setDueDate(LocalDate.now().minusDays(1));

        Loan fiftyDaysOverdue = new Loan();
        fiftyDaysOverdue.setId(11L);
        fiftyDaysOverdue.setUser(testUser);
        fiftyDaysOverdue.setBook(testBook);
        fiftyDaysOverdue.setDueDate(LocalDate.now().minusDays(50));

        List<Loan> testLoans = Arrays.asList(oneDayOverdue, fiftyDaysOverdue);
        when(loanRepository.findOverdueLoans(any(LocalDate.class))).thenReturn(testLoans);
        when(fineRepository.findByLoanId(10L)).thenReturn(Collections.emptyList());
        when(fineRepository.findByLoanId(11L)).thenReturn(Collections.emptyList());

        // When
        List<Fine> result = fineCalculationService.calculateOverdueFines();

        // Then
        assertEquals(2, result.size());
        assertEquals(new BigDecimal("0.50"), result.get(0).getAmount()); // 1 day * $0.50
        assertEquals(new BigDecimal("25.00"), result.get(1).getAmount()); // Capped at maximum

        System.out.println("DEBUG: Fine calculation constants verified");
        System.out.println("DEBUG: Daily rate: $0.50, Maximum fine: $25.00");
    }
}