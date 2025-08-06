package org.acme.dto;

import org.acme.model.Book;
import org.acme.model.Loan;
import org.acme.model.LoanStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for returning loan information with user details
 * Avoids circular reference issues with @JsonBackReference
 */
public class LoanResponseDTO {
    private Long id;
    private Book book;
    private UserSummaryDTO user;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
    private LoanStatus status;
    private Integer renewalCount;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default constructor
    public LoanResponseDTO() {
    }

    // Static factory method to create from Loan entity
    public static LoanResponseDTO fromLoan(Loan loan) {
        LoanResponseDTO dto = new LoanResponseDTO();
        dto.setId(loan.getId());
        dto.setBook(loan.getBook());
        dto.setBorrowDate(loan.getBorrowDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setDueDate(loan.getDueDate());
        dto.setStatus(loan.getStatus());
        dto.setRenewalCount(loan.getRenewalCount());
        dto.setNotes(loan.getNotes());
        dto.setCreatedAt(loan.getCreatedAt());
        dto.setUpdatedAt(loan.getUpdatedAt());

        // Create user summary if user exists
        if (loan.getUser() != null) {
            dto.setUser(new UserSummaryDTO(loan.getUser()));
        }

        return dto;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public UserSummaryDTO getUser() {
        return user;
    }

    public void setUser(UserSummaryDTO user) {
        this.user = user;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }

    public Integer getRenewalCount() {
        return renewalCount;
    }

    public void setRenewalCount(Integer renewalCount) {
        this.renewalCount = renewalCount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}