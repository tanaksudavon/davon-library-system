package org.acme.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Represents a book loan in the library system.
 * Extends BaseEntity to inherit common properties like id and timestamps.
 * 
 * @author Davon Library System
 * @version 1.0
 */
public class Loan extends BaseEntity {
    
    /**
     * The user who borrowed the book
     */
    private User user;
    
    /**
     * The book that was borrowed
     */
    private Book book;
    
    /**
     * The date when the book was borrowed
     */
    private LocalDate loanDate;
    
    /**
     * The due date for returning the book
     */
    private LocalDate dueDate;
    
    /**
     * The actual return date (null if not yet returned)
     */
    private LocalDate returnDate;
    
    /**
     * Current status of the loan
     */
    private LoanStatus status;
    
    /**
     * Number of times the loan has been renewed
     */
    private Integer renewalCount;
    
    /**
     * Maximum number of renewals allowed
     */
    private Integer maxRenewals;
    
    /**
     * Fine amount for overdue books
     */
    private Double fineAmount;
    
    /**
     * Additional notes about the loan
     */
    private String notes;
    
    /**
     * Enum representing the status of a loan
     */
    public enum LoanStatus {
        ACTIVE,
        RETURNED,
        OVERDUE,
        RENEWED,
        LOST
    }
    
    /**
     * Default loan period in days
     */
    public static final int DEFAULT_LOAN_PERIOD = 14;
    
    /**
     * Default maximum renewals allowed
     */
    public static final int DEFAULT_MAX_RENEWALS = 2;
    
    /**
     * Daily fine rate for overdue books
     */
    public static final double DAILY_FINE_RATE = 0.50;
    
    /**
     * Default constructor
     */
    public Loan() {
        super();
        this.loanDate = LocalDate.now();
        this.dueDate = loanDate.plusDays(DEFAULT_LOAN_PERIOD);
        this.status = LoanStatus.ACTIVE;
        this.renewalCount = 0;
        this.maxRenewals = DEFAULT_MAX_RENEWALS;
        this.fineAmount = 0.0;
    }
    
    /**
     * Constructor with user and book
     * 
     * @param user the user borrowing the book
     * @param book the book being borrowed
     */
    public Loan(User user, Book book) {
        this();
        this.user = user;
        this.book = book;
    }
    
    /**
     * Constructor with user, book, and loan period
     * 
     * @param user the user borrowing the book
     * @param book the book being borrowed
     * @param loanPeriodDays the loan period in days
     */
    public Loan(User user, Book book, int loanPeriodDays) {
        this();
        this.user = user;
        this.book = book;
        this.dueDate = loanDate.plusDays(loanPeriodDays);
    }
    
    // Getters and Setters
    
    /**
     * Gets the user
     * 
     * @return the user
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Sets the user
     * 
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
        updateTimestamp();
    }
    
    /**
     * Gets the book
     * 
     * @return the book
     */
    public Book getBook() {
        return book;
    }
    
    /**
     * Sets the book
     * 
     * @param book the book to set
     */
    public void setBook(Book book) {
        this.book = book;
        updateTimestamp();
    }
    
    /**
     * Gets the loan date
     * 
     * @return the loan date
     */
    public LocalDate getLoanDate() {
        return loanDate;
    }
    
    /**
     * Sets the loan date
     * 
     * @param loanDate the loan date to set
     */
    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
        updateTimestamp();
    }
    
    /**
     * Gets the due date
     * 
     * @return the due date
     */
    public LocalDate getDueDate() {
        return dueDate;
    }
    
    /**
     * Sets the due date
     * 
     * @param dueDate the due date to set
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        updateTimestamp();
    }
    
    /**
     * Gets the return date
     * 
     * @return the return date
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    /**
     * Sets the return date
     * 
     * @param returnDate the return date to set
     */
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
        updateTimestamp();
    }
    
    /**
     * Gets the loan status
     * 
     * @return the loan status
     */
    public LoanStatus getStatus() {
        return status;
    }
    
    /**
     * Sets the loan status
     * 
     * @param status the loan status to set
     */
    public void setStatus(LoanStatus status) {
        this.status = status;
        updateTimestamp();
    }
    
    /**
     * Gets the renewal count
     * 
     * @return the renewal count
     */
    public Integer getRenewalCount() {
        return renewalCount;
    }
    
    /**
     * Sets the renewal count
     * 
     * @param renewalCount the renewal count to set
     */
    public void setRenewalCount(Integer renewalCount) {
        this.renewalCount = renewalCount;
        updateTimestamp();
    }
    
    /**
     * Gets the maximum renewals allowed
     * 
     * @return the maximum renewals allowed
     */
    public Integer getMaxRenewals() {
        return maxRenewals;
    }
    
    /**
     * Sets the maximum renewals allowed
     * 
     * @param maxRenewals the maximum renewals allowed to set
     */
    public void setMaxRenewals(Integer maxRenewals) {
        this.maxRenewals = maxRenewals;
        updateTimestamp();
    }
    
    /**
     * Gets the fine amount
     * 
     * @return the fine amount
     */
    public Double getFineAmount() {
        return fineAmount;
    }
    
    /**
     * Sets the fine amount
     * 
     * @param fineAmount the fine amount to set
     */
    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
        updateTimestamp();
    }
    
    /**
     * Gets the notes
     * 
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }
    
    /**
     * Sets the notes
     * 
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
        updateTimestamp();
    }
    
    // Business Logic Methods
    
    /**
     * Checks if the loan is overdue
     * 
     * @return true if the loan is overdue, false otherwise
     */
    public boolean isOverdue() {
        return status == LoanStatus.ACTIVE && 
               LocalDate.now().isAfter(dueDate);
    }
    
    /**
     * Calculates the number of days overdue
     * 
     * @return the number of days overdue (0 if not overdue)
     */
    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }
    
    /**
     * Calculates the current fine amount for overdue loans
     * 
     * @return the calculated fine amount
     */
    public double calculateFine() {
        if (!isOverdue()) {
            return 0.0;
        }
        return getDaysOverdue() * DAILY_FINE_RATE;
    }
    
    /**
     * Checks if the loan can be renewed
     * 
     * @return true if the loan can be renewed, false otherwise
     */
    public boolean canRenew() {
        return status == LoanStatus.ACTIVE && 
               renewalCount < maxRenewals && 
               !isOverdue();
    }
    
    /**
     * Renews the loan by extending the due date
     * 
     * @param additionalDays the number of additional days to extend
     * @return true if renewal was successful, false otherwise
     */
    public boolean renew(int additionalDays) {
        if (!canRenew()) {
            return false;
        }
        
        this.dueDate = dueDate.plusDays(additionalDays);
        this.renewalCount++;
        this.status = LoanStatus.RENEWED;
        updateTimestamp();
        return true;
    }
    
    /**
     * Renews the loan with the default loan period
     * 
     * @return true if renewal was successful, false otherwise
     */
    public boolean renew() {
        return renew(DEFAULT_LOAN_PERIOD);
    }
    
    /**
     * Returns the book and updates the loan status
     */
    public void returnBook() {
        this.returnDate = LocalDate.now();
        this.status = LoanStatus.RETURNED;
        
        // Calculate final fine if overdue
        if (isOverdue()) {
            this.fineAmount = calculateFine();
        }
        
        // Update book status
        if (book != null) {
            book.returnBook();
        }
        
        updateTimestamp();
    }
    
    /**
     * Gets the number of days remaining until due date
     * 
     * @return the number of days remaining (negative if overdue)
     */
    public long getDaysUntilDue() {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
    }
    
    /**
     * Checks if the loan is due soon (within 3 days)
     * 
     * @return true if due soon, false otherwise
     */
    public boolean isDueSoon() {
        long daysUntilDue = getDaysUntilDue();
        return daysUntilDue >= 0 && daysUntilDue <= 3;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Loan loan = (Loan) o;
        return Objects.equals(user, loan.user) && 
               Objects.equals(book, loan.book) && 
               Objects.equals(loanDate, loan.loanDate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), user, book, loanDate);
    }
    
    @Override
    public String toString() {
        return "Loan{" +
                "id=" + getId() +
                ", user=" + (user != null ? user.getFullName() : "Unknown") +
                ", book=" + (book != null ? book.getTitle() : "Unknown") +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", status=" + status +
                ", renewalCount=" + renewalCount +
                '}';
    }
} 