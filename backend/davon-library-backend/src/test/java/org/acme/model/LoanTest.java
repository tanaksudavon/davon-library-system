package org.acme.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Unit tests for Loan entity
 */
@DisplayName("Loan Entity Tests")
class LoanTest {

    private Loan loan;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        borrowDate = LocalDate.of(2023, 1, 15);
        returnDate = LocalDate.of(2023, 1, 30);
        dueDate = LocalDate.of(2023, 2, 15);
        now = LocalDateTime.now();
        loan = new Loan();
    }

    @Test
    @DisplayName("Test no-args constructor")
    void testNoArgsConstructor() {
        Loan newLoan = new Loan();
        assertNotNull(newLoan);
        assertNull(newLoan.getId());
        assertNull(newLoan.getBookId());
        assertNull(newLoan.getUserId());
        assertNull(newLoan.getBorrowDate());
        assertNull(newLoan.getReturnDate());
        assertNull(newLoan.getDueDate());
        assertNull(newLoan.getStatus());
        assertNull(newLoan.getNotes());
        assertNull(newLoan.getCreatedAt());
        assertNull(newLoan.getUpdatedAt());
    }

    @Test
    @DisplayName("Test all-args constructor")
    void testAllArgsConstructor() {
        Loan newLoan = new Loan(1L, 1L, 1L, borrowDate, returnDate, dueDate,
                LoanStatus.RETURNED, "Book returned in good condition", now, now);

        assertEquals(1L, newLoan.getId());
        assertEquals(1L, newLoan.getBookId());
        assertEquals(1L, newLoan.getUserId());
        assertEquals(borrowDate, newLoan.getBorrowDate());
        assertEquals(returnDate, newLoan.getReturnDate());
        assertEquals(dueDate, newLoan.getDueDate());
        assertEquals(LoanStatus.RETURNED, newLoan.getStatus());
        assertEquals("Book returned in good condition", newLoan.getNotes());
        assertEquals(now, newLoan.getCreatedAt());
        assertEquals(now, newLoan.getUpdatedAt());
    }

    @Test
    @DisplayName("Test setters and getters")
    void testSettersAndGetters() {
        loan.setId(1L);
        loan.setBookId(2L);
        loan.setUserId(3L);
        loan.setBorrowDate(borrowDate);
        loan.setReturnDate(returnDate);
        loan.setDueDate(dueDate);
        loan.setStatus(LoanStatus.BORROWED);
        loan.setNotes("Book borrowed by regular member");
        loan.setCreatedAt(now);
        loan.setUpdatedAt(now);

        assertEquals(1L, loan.getId());
        assertEquals(2L, loan.getBookId());
        assertEquals(3L, loan.getUserId());
        assertEquals(borrowDate, loan.getBorrowDate());
        assertEquals(returnDate, loan.getReturnDate());
        assertEquals(dueDate, loan.getDueDate());
        assertEquals(LoanStatus.BORROWED, loan.getStatus());
        assertEquals("Book borrowed by regular member", loan.getNotes());
        assertEquals(now, loan.getCreatedAt());
        assertEquals(now, loan.getUpdatedAt());
    }

    @Test
    @DisplayName("Test all loan statuses")
    void testLoanStatuses() {
        loan.setStatus(LoanStatus.BORROWED);
        assertEquals(LoanStatus.BORROWED, loan.getStatus());

        loan.setStatus(LoanStatus.RETURNED);
        assertEquals(LoanStatus.RETURNED, loan.getStatus());

        loan.setStatus(LoanStatus.OVERDUE);
        assertEquals(LoanStatus.OVERDUE, loan.getStatus());

        loan.setStatus(LoanStatus.LOST);
        assertEquals(LoanStatus.LOST, loan.getStatus());
    }

    @Test
    @DisplayName("Test date relationships")
    void testDateRelationships() {
        // Test normal loan scenario
        LocalDate borrow = LocalDate.of(2023, 1, 1);
        LocalDate due = LocalDate.of(2023, 1, 15);
        LocalDate returned = LocalDate.of(2023, 1, 10);

        loan.setBorrowDate(borrow);
        loan.setDueDate(due);
        loan.setReturnDate(returned);

        assertEquals(borrow, loan.getBorrowDate());
        assertEquals(due, loan.getDueDate());
        assertEquals(returned, loan.getReturnDate());

        // Test overdue scenario
        LocalDate overdueReturn = LocalDate.of(2023, 1, 20);
        loan.setReturnDate(overdueReturn);
        loan.setStatus(LoanStatus.OVERDUE);

        assertEquals(overdueReturn, loan.getReturnDate());
        assertEquals(LoanStatus.OVERDUE, loan.getStatus());
    }

    @Test
    @DisplayName("Test notes scenarios")
    void testNotesScenarios() {
        // Test various note types
        String[] notes = {
                "Book in excellent condition",
                "Minor wear on cover",
                "Pages 45-50 have highlighting",
                "User requested extension",
                "",
                "Very long note that contains multiple sentences and describes the detailed condition of the book including any damage, special circumstances, or other relevant information that might be useful for tracking purposes."
        };

        for (String note : notes) {
            loan.setNotes(note);
            assertEquals(note, loan.getNotes());
        }

        // Test null notes
        loan.setNotes(null);
        assertNull(loan.getNotes());
    }

    @Test
    @DisplayName("Test equals and hashCode")
    void testEqualsAndHashCode() {
        Loan loan1 = new Loan(1L, 1L, 1L, borrowDate, returnDate, dueDate,
                LoanStatus.RETURNED, "Test note", now, now);
        Loan loan2 = new Loan(1L, 1L, 1L, borrowDate, returnDate, dueDate,
                LoanStatus.RETURNED, "Test note", now, now);
        Loan loan3 = new Loan(2L, 2L, 2L, borrowDate, null, dueDate,
                LoanStatus.BORROWED, "Different note", now, now);

        assertEquals(loan1, loan2);
        assertNotEquals(loan1, loan3);
        assertEquals(loan1.hashCode(), loan2.hashCode());
        assertNotEquals(loan1.hashCode(), loan3.hashCode());
    }

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        loan.setId(1L);
        loan.setBookId(2L);
        loan.setUserId(3L);
        loan.setStatus(LoanStatus.BORROWED);

        String toString = loan.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("1"));
        assertTrue(toString.contains("2"));
        assertTrue(toString.contains("3"));
        assertTrue(toString.contains("BORROWED"));
    }

    @Test
    @DisplayName("Test null values handling")
    void testNullValues() {
        assertDoesNotThrow(() -> {
            loan.setBookId(null);
            loan.setUserId(null);
            loan.setBorrowDate(null);
            loan.setReturnDate(null);
            loan.setDueDate(null);
            loan.setStatus(null);
            loan.setNotes(null);
            loan.setCreatedAt(null);
            loan.setUpdatedAt(null);
        });

        assertNull(loan.getBookId());
        assertNull(loan.getUserId());
        assertNull(loan.getBorrowDate());
        assertNull(loan.getReturnDate());
        assertNull(loan.getDueDate());
        assertNull(loan.getStatus());
        assertNull(loan.getNotes());
        assertNull(loan.getCreatedAt());
        assertNull(loan.getUpdatedAt());
    }

    @Test
    @DisplayName("Test loan lifecycle scenarios")
    void testLoanLifecycleScenarios() {
        // Test new loan
        loan.setBookId(1L);
        loan.setUserId(1L);
        loan.setBorrowDate(LocalDate.now());
        loan.setDueDate(LocalDate.now().plusDays(14));
        loan.setStatus(LoanStatus.BORROWED);
        loan.setReturnDate(null);

        assertEquals(LoanStatus.BORROWED, loan.getStatus());
        assertNull(loan.getReturnDate());

        // Test returned loan
        loan.setReturnDate(LocalDate.now().plusDays(10));
        loan.setStatus(LoanStatus.RETURNED);

        assertEquals(LoanStatus.RETURNED, loan.getStatus());
        assertNotNull(loan.getReturnDate());

        // Test overdue loan
        loan.setReturnDate(LocalDate.now().plusDays(20));
        loan.setStatus(LoanStatus.OVERDUE);

        assertEquals(LoanStatus.OVERDUE, loan.getStatus());

        // Test lost loan
        loan.setStatus(LoanStatus.LOST);
        loan.setNotes("Book reported lost by user");

        assertEquals(LoanStatus.LOST, loan.getStatus());
        assertEquals("Book reported lost by user", loan.getNotes());
    }

    @Test
    @DisplayName("Test ID scenarios")
    void testIdScenarios() {
        // Test positive IDs
        loan.setId(1L);
        loan.setBookId(100L);
        loan.setUserId(200L);

        assertEquals(1L, loan.getId());
        assertEquals(100L, loan.getBookId());
        assertEquals(200L, loan.getUserId());

        // Test large IDs
        loan.setId(Long.MAX_VALUE);
        loan.setBookId(Long.MAX_VALUE - 1);
        loan.setUserId(Long.MAX_VALUE - 2);

        assertEquals(Long.MAX_VALUE, loan.getId());
        assertEquals(Long.MAX_VALUE - 1, loan.getBookId());
        assertEquals(Long.MAX_VALUE - 2, loan.getUserId());

        // Test zero IDs
        loan.setId(0L);
        loan.setBookId(0L);
        loan.setUserId(0L);

        assertEquals(0L, loan.getId());
        assertEquals(0L, loan.getBookId());
        assertEquals(0L, loan.getUserId());
    }
}