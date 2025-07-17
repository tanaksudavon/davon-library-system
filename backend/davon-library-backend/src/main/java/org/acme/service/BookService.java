package org.acme.service;

import org.acme.model.Book;
import org.acme.model.User;
import org.acme.model.Loan;
import org.acme.model.Book.BookStatus;
import org.acme.model.Loan.LoanStatus;
import org.acme.model.User.UserStatus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing book operations including checkout, return, and inventory management.
 * Contains business logic for book-related operations in the library system.
 * 
 * @author Davon Library System
 * @version 1.0
 */
@ApplicationScoped
public class BookService {
    
    @Inject
    private LoanService loanService;
    
    @Inject
    private UserService userService;
    
    // In-memory storage for demonstration (replace with database in production)
    private final Map<Long, Book> bookRepository = new HashMap<>();
    private Long nextBookId = 1L;
    
    /**
     * Checks out a book to a user.
     * 
     * @param bookId the ID of the book to checkout
     * @param userId the ID of the user checking out the book
     * @return the created loan if successful
     * @throws IllegalStateException if checkout is not possible
     */
    public Loan checkoutBook(Long bookId, Long userId) {
        Book book = findBookById(bookId);
        User user = userService.findUserById(userId);
        
        // Validate checkout conditions
        validateCheckoutConditions(book, user);
        
        // Create and process the loan
        Loan loan = loanService.createLoan(user, book);
        
        // Update book status
        book.checkOut();
        
        // Add loan to user's current loans
        user.getCurrentLoans().add(loan);
        
        return loan;
    }
    
    /**
     * Returns a book from a loan.
     * 
     * @param loanId the ID of the loan to return
     * @return the updated loan
     * @throws IllegalStateException if return is not possible
     */
    public Loan returnBook(Long loanId) {
        Loan loan = loanService.findLoanById(loanId);
        
        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
            throw new IllegalStateException("Cannot return book - loan is not active");
        }
        
        // Process the return
        loan.returnBook();
        
        // Update book status
        loan.getBook().returnBook();
        
        // Remove from user's current loans
        loan.getUser().getCurrentLoans().remove(loan);
        
        // Add to loan history
        loan.getUser().getLoanHistory().add(loan);
        
        return loan;
    }
    
    /**
     * Renews a loan for additional time.
     * 
     * @param loanId the ID of the loan to renew
     * @return the renewed loan
     * @throws IllegalStateException if renewal is not possible
     */
    public Loan renewLoan(Long loanId) {
        Loan loan = loanService.findLoanById(loanId);
        
        if (!loan.canRenew()) {
            throw new IllegalStateException("Cannot renew loan - maximum renewals reached or loan not eligible");
        }
        
        loan.renew();
        return loan;
    }
    
    /**
     * Adds a new book to the inventory.
     * 
     * @param book the book to add
     * @return the saved book with assigned ID
     */
    public Book addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        
        // Validate required fields
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Book title is required");
        }
        
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) {
            throw new IllegalArgumentException("Book ISBN is required");
        }
        
        // Check for duplicate ISBN
        if (findBookByIsbn(book.getIsbn()).isPresent()) {
            throw new IllegalStateException("Book with ISBN " + book.getIsbn() + " already exists");
        }
        
        // Set ID and save
        book.setId(nextBookId++);
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.put(book.getId(), book);
        
        return book;
    }
    
    /**
     * Updates an existing book in the inventory.
     * 
     * @param book the book to update
     * @return the updated book
     */
    public Book updateBook(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Book and book ID cannot be null");
        }
        
        Book existingBook = findBookById(book.getId());
        
        // Update fields
        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPublisher(book.getPublisher());
        existingBook.setPublicationDate(book.getPublicationDate());
        existingBook.setGenre(book.getGenre());
        existingBook.setDescription(book.getDescription());
        existingBook.setPageCount(book.getPageCount());
        existingBook.setLocation(book.getLocation());
        
        return existingBook;
    }
    
    /**
     * Removes a book from the inventory.
     * 
     * @param bookId the ID of the book to remove
     * @throws IllegalStateException if book is currently checked out
     */
    public void removeBook(Long bookId) {
        Book book = findBookById(bookId);
        
        if (book.getStatus() == BookStatus.CHECKED_OUT) {
            throw new IllegalStateException("Cannot remove book - currently checked out");
        }
        
        bookRepository.remove(bookId);
    }
    
    /**
     * Finds a book by its ID.
     * 
     * @param bookId the book ID
     * @return the book
     * @throws IllegalArgumentException if book not found
     */
    public Book findBookById(Long bookId) {
        Book book = bookRepository.get(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found with ID: " + bookId);
        }
        return book;
    }
    
    /**
     * Finds a book by its ISBN.
     * 
     * @param isbn the ISBN to search for
     * @return Optional containing the book if found
     */
    public Optional<Book> findBookByIsbn(String isbn) {
        return bookRepository.values().stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst();
    }
    
    /**
     * Gets all books in the inventory.
     * 
     * @return list of all books
     */
    public List<Book> getAllBooks() {
        return new ArrayList<>(bookRepository.values());
    }
    
    /**
     * Gets all available books.
     * 
     * @return list of available books
     */
    public List<Book> getAvailableBooks() {
        return bookRepository.values().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets books by status.
     * 
     * @param status the book status
     * @return list of books with the specified status
     */
    public List<Book> getBooksByStatus(BookStatus status) {
        return bookRepository.values().stream()
                .filter(book -> book.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets inventory statistics.
     * 
     * @return map containing inventory statistics
     */
    public Map<String, Integer> getInventoryStats() {
        Map<String, Integer> stats = new HashMap<>();
        
        stats.put("total", bookRepository.size());
        stats.put("available", (int) bookRepository.values().stream()
                .filter(Book::isAvailable).count());
        stats.put("checkedOut", (int) bookRepository.values().stream()
                .filter(book -> book.getStatus() == BookStatus.CHECKED_OUT).count());
        stats.put("reserved", (int) bookRepository.values().stream()
                .filter(book -> book.getStatus() == BookStatus.RESERVED).count());
        stats.put("maintenance", (int) bookRepository.values().stream()
                .filter(book -> book.getStatus() == BookStatus.MAINTENANCE).count());
        stats.put("lost", (int) bookRepository.values().stream()
                .filter(book -> book.getStatus() == BookStatus.LOST).count());
        
        return stats;
    }
    
    /**
     * Validates conditions for checking out a book.
     * 
     * @param book the book to checkout
     * @param user the user checking out the book
     * @throws IllegalStateException if checkout conditions are not met
     */
    private void validateCheckoutConditions(Book book, User user) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (!book.isAvailable()) {
            throw new IllegalStateException("Book is not available for checkout");
        }
        
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new IllegalStateException("User account is not active");
        }
        
        if (!user.canBorrowMore()) {
            throw new IllegalStateException("User has reached maximum borrowing limit");
        }
        
        // Check for overdue books
        boolean hasOverdueBooks = user.getCurrentLoans().stream()
                .anyMatch(loan -> loan.isOverdue());
        
        if (hasOverdueBooks) {
            throw new IllegalStateException("User has overdue books and cannot checkout new books");
        }
    }
} 