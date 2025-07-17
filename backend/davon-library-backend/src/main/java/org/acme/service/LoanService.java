package org.acme.service;

import org.acme.model.Loan;
import org.acme.model.User;
import org.acme.model.Book;
import org.acme.model.Loan.LoanStatus;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Service class for managing loan operations.
 * Contains business logic for loan-related operations in the library system.
 * 
 * @author Davon Library System
 * @version 1.0
 */
@ApplicationScoped
public class LoanService {
    
    // In-memory storage for demonstration (replace with database in production)
    private final Map<Long, Loan> loanRepository = new HashMap<>();
    private Long nextLoanId = 1L;
    
    /**
     * Creates a new loan for a user and book.
     * 
     * @param user the user borrowing the book
     * @param book the book being borrowed
     * @return the created loan
     */
    public Loan createLoan(User user, Book book) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        
        Loan loan = new Loan(user, book);
        loan.setId(nextLoanId++);
        
        loanRepository.put(loan.getId(), loan);
        
        return loan;
    }
    
    /**
     * Finds a loan by its ID.
     * 
     * @param loanId the loan ID
     * @return the loan
     * @throws IllegalArgumentException if loan not found
     */
    public Loan findLoanById(Long loanId) {
        Loan loan = loanRepository.get(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found with ID: " + loanId);
        }
        return loan;
    }
    
    /**
     * Gets all loans in the system.
     * 
     * @return list of all loans
     */
    public List<Loan> getAllLoans() {
        return new ArrayList<>(loanRepository.values());
    }
    
    /**
     * Gets all active loans.
     * 
     * @return list of active loans
     */
    public List<Loan> getActiveLoans() {
        return loanRepository.values().stream()
                .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all overdue loans.
     * 
     * @return list of overdue loans
     */
    public List<Loan> getOverdueLoans() {
        return loanRepository.values().stream()
                .filter(Loan::isOverdue)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets loans by user.
     * 
     * @param userId the user ID
     * @return list of loans for the user
     */
    public List<Loan> getLoansByUser(Long userId) {
        return loanRepository.values().stream()
                .filter(loan -> loan.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets loans by book.
     * 
     * @param bookId the book ID
     * @return list of loans for the book
     */
    public List<Loan> getLoansByBook(Long bookId) {
        return loanRepository.values().stream()
                .filter(loan -> loan.getBook().getId().equals(bookId))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets loans by status.
     * 
     * @param status the loan status
     * @return list of loans with the specified status
     */
    public List<Loan> getLoansByStatus(LoanStatus status) {
        return loanRepository.values().stream()
                .filter(loan -> loan.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets loans due within a specified number of days.
     * 
     * @param days number of days to check
     * @return list of loans due within the specified days
     */
    public List<Loan> getLoansDueWithinDays(int days) {
        LocalDate cutoffDate = LocalDate.now().plusDays(days);
        return loanRepository.values().stream()
                .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE)
                .filter(loan -> !loan.getDueDate().isAfter(cutoffDate))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets loan statistics.
     * 
     * @return map containing loan statistics
     */
    public Map<String, Integer> getLoanStats() {
        Map<String, Integer> stats = new HashMap<>();
        
        stats.put("total", loanRepository.size());
        stats.put("active", (int) loanRepository.values().stream()
                .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE).count());
        stats.put("overdue", (int) loanRepository.values().stream()
                .filter(Loan::isOverdue).count());
        stats.put("returned", (int) loanRepository.values().stream()
                .filter(loan -> loan.getStatus() == LoanStatus.RETURNED).count());
        stats.put("renewed", (int) loanRepository.values().stream()
                .filter(loan -> loan.getStatus() == LoanStatus.RENEWED).count());
        stats.put("lost", (int) loanRepository.values().stream()
                .filter(loan -> loan.getStatus() == LoanStatus.LOST).count());
        
        return stats;
    }
    
    /**
     * Calculates total fines for all overdue loans.
     * 
     * @return total fine amount
     */
    public double calculateTotalFines() {
        return loanRepository.values().stream()
                .filter(Loan::isOverdue)
                .mapToDouble(Loan::calculateFine)
                .sum();
    }
    
    /**
     * Gets loans due today.
     * 
     * @return list of loans due today
     */
    public List<Loan> getLoansDueToday() {
        LocalDate today = LocalDate.now();
        return loanRepository.values().stream()
                .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE)
                .filter(loan -> loan.getDueDate().equals(today))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets loans due soon (within 3 days).
     * 
     * @return list of loans due soon
     */
    public List<Loan> getLoansDueSoon() {
        return loanRepository.values().stream()
                .filter(loan -> loan.getStatus() == LoanStatus.ACTIVE)
                .filter(Loan::isDueSoon)
                .collect(Collectors.toList());
    }
    
    /**
     * Removes a loan from the system.
     * 
     * @param loanId the ID of the loan to remove
     */
    public void removeLoan(Long loanId) {
        loanRepository.remove(loanId);
    }
} 