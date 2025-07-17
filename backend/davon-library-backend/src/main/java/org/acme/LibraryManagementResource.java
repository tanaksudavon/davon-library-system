package org.acme;

import org.acme.model.Book;
import org.acme.model.User;
import org.acme.model.Loan;
import org.acme.model.Author;
import org.acme.model.Book.BookStatus;
import org.acme.model.User.UserType;
import org.acme.model.User.UserStatus;
import org.acme.service.BookService;
import org.acme.service.UserService;
import org.acme.service.LoanService;
import org.acme.service.SearchService;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST resource demonstrating the library management system business logic.
 * Provides endpoints for book checkout/return, user management, inventory management, and search functionality.
 * 
 * @author Davon Library System
 * @version 1.0
 */
@Path("/library")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LibraryManagementResource {
    
    @Inject
    private BookService bookService;
    
    @Inject
    private UserService userService;
    
    @Inject
    private LoanService loanService;
    
    @Inject
    private SearchService searchService;
    
    // ========== BOOK CHECKOUT/RETURN OPERATIONS ==========
    
    /**
     * Checkout a book to a user.
     */
    @POST
    @Path("/checkout")
    public Response checkoutBook(@QueryParam("bookId") Long bookId, 
                               @QueryParam("userId") Long userId) {
        try {
            Loan loan = bookService.checkoutBook(bookId, userId);
            return Response.ok(loan).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Return a book.
     */
    @POST
    @Path("/return")
    public Response returnBook(@QueryParam("loanId") Long loanId) {
        try {
            Loan loan = bookService.returnBook(loanId);
            return Response.ok(loan).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Renew a loan.
     */
    @POST
    @Path("/renew")
    public Response renewLoan(@QueryParam("loanId") Long loanId) {
        try {
            Loan loan = bookService.renewLoan(loanId);
            return Response.ok(loan).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    // ========== INVENTORY MANAGEMENT ==========
    
    /**
     * Add a new book to inventory.
     */
    @POST
    @Path("/books")
    public Response addBook(Book book) {
        try {
            Book savedBook = bookService.addBook(book);
            return Response.status(Response.Status.CREATED).entity(savedBook).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Update a book in inventory.
     */
    @PUT
    @Path("/books/{id}")
    public Response updateBook(@PathParam("id") Long id, Book book) {
        try {
            book.setId(id);
            Book updatedBook = bookService.updateBook(book);
            return Response.ok(updatedBook).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Remove a book from inventory.
     */
    @DELETE
    @Path("/books/{id}")
    public Response removeBook(@PathParam("id") Long id) {
        try {
            bookService.removeBook(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Get all books.
     */
    @GET
    @Path("/books")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }
    
    /**
     * Get available books.
     */
    @GET
    @Path("/books/available")
    public List<Book> getAvailableBooks() {
        return bookService.getAvailableBooks();
    }
    
    /**
     * Get books by status.
     */
    @GET
    @Path("/books/status/{status}")
    public List<Book> getBooksByStatus(@PathParam("status") BookStatus status) {
        return bookService.getBooksByStatus(status);
    }
    
    /**
     * Get inventory statistics.
     */
    @GET
    @Path("/inventory/stats")
    public Map<String, Integer> getInventoryStats() {
        return bookService.getInventoryStats();
    }
    
    // ========== USER MANAGEMENT ==========
    
    /**
     * Create a new user.
     */
    @POST
    @Path("/users")
    public Response createUser(User user) {
        try {
            User savedUser = userService.createUser(user);
            return Response.status(Response.Status.CREATED).entity(savedUser).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Update a user.
     */
    @PUT
    @Path("/users/{id}")
    public Response updateUser(@PathParam("id") Long id, User user) {
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            return Response.ok(updatedUser).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Suspend a user.
     */
    @POST
    @Path("/users/{id}/suspend")
    public Response suspendUser(@PathParam("id") Long id) {
        try {
            userService.suspendUser(id);
            return Response.ok(Map.of("message", "User suspended successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Activate a user.
     */
    @POST
    @Path("/users/{id}/activate")
    public Response activateUser(@PathParam("id") Long id) {
        try {
            userService.activateUser(id);
            return Response.ok(Map.of("message", "User activated successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
    
    /**
     * Get all users.
     */
    @GET
    @Path("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    /**
     * Get users by status.
     */
    @GET
    @Path("/users/status/{status}")
    public List<User> getUsersByStatus(@PathParam("status") UserStatus status) {
        return userService.getUsersByStatus(status);
    }
    
    /**
     * Get users with overdue books.
     */
    @GET
    @Path("/users/overdue")
    public List<User> getUsersWithOverdueBooks() {
        return userService.getUsersWithOverdueBooks();
    }
    
    /**
     * Get user statistics.
     */
    @GET
    @Path("/users/stats")
    public Map<String, Integer> getUserStats() {
        return userService.getUserStats();
    }
    
    // ========== LOAN MANAGEMENT ==========
    
    /**
     * Get all loans.
     */
    @GET
    @Path("/loans")
    public List<Loan> getAllLoans() {
        return loanService.getAllLoans();
    }
    
    /**
     * Get active loans.
     */
    @GET
    @Path("/loans/active")
    public List<Loan> getActiveLoans() {
        return loanService.getActiveLoans();
    }
    
    /**
     * Get overdue loans.
     */
    @GET
    @Path("/loans/overdue")
    public List<Loan> getOverdueLoans() {
        return loanService.getOverdueLoans();
    }
    
    /**
     * Get loans by user.
     */
    @GET
    @Path("/loans/user/{userId}")
    public List<Loan> getLoansByUser(@PathParam("userId") Long userId) {
        return loanService.getLoansByUser(userId);
    }
    
    /**
     * Get loans due today.
     */
    @GET
    @Path("/loans/due-today")
    public List<Loan> getLoansDueToday() {
        return loanService.getLoansDueToday();
    }
    
    /**
     * Get loan statistics.
     */
    @GET
    @Path("/loans/stats")
    public Map<String, Integer> getLoanStats() {
        return loanService.getLoanStats();
    }
    
    // ========== SEARCH FUNCTIONALITY ==========
    
    /**
     * Search books by title.
     */
    @GET
    @Path("/search/books/title")
    public List<Book> searchBooksByTitle(@QueryParam("q") String title) {
        return searchService.searchBooksByTitle(title);
    }
    
    /**
     * Search books by author.
     */
    @GET
    @Path("/search/books/author")
    public List<Book> searchBooksByAuthor(@QueryParam("q") String author) {
        return searchService.searchBooksByAuthor(author);
    }
    
    /**
     * Search books by genre.
     */
    @GET
    @Path("/search/books/genre")
    public List<Book> searchBooksByGenre(@QueryParam("q") String genre) {
        return searchService.searchBooksByGenre(genre);
    }
    
    /**
     * General book search.
     */
    @GET
    @Path("/search/books")
    public List<Book> searchBooks(@QueryParam("q") String searchTerm) {
        return searchService.searchBooks(searchTerm);
    }
    
    /**
     * Search available books only.
     */
    @GET
    @Path("/search/books/available")
    public List<Book> searchAvailableBooks(@QueryParam("q") String searchTerm) {
        return searchService.searchAvailableBooks(searchTerm);
    }
    
    /**
     * Advanced search with multiple criteria.
     */
    @GET
    @Path("/search/books/advanced")
    public List<Book> advancedSearch(@QueryParam("title") String title,
                                   @QueryParam("author") String author,
                                   @QueryParam("genre") String genre,
                                   @QueryParam("publisher") String publisher,
                                   @QueryParam("availableOnly") @DefaultValue("false") boolean availableOnly) {
        return searchService.advancedSearch(title, author, genre, publisher, availableOnly);
    }
    
    /**
     * Search users by name.
     */
    @GET
    @Path("/search/users")
    public List<User> searchUsers(@QueryParam("q") String name) {
        return searchService.searchUsersByName(name);
    }
    
    /**
     * Get all genres.
     */
    @GET
    @Path("/genres")
    public List<String> getAllGenres() {
        return searchService.getAllGenres();
    }
    
    /**
     * Get all publishers.
     */
    @GET
    @Path("/publishers")
    public List<String> getAllPublishers() {
        return searchService.getAllPublishers();
    }
    
    // ========== DEMO DATA INITIALIZATION ==========
    
    /**
     * Initialize demo data for testing.
     */
    @POST
    @Path("/demo/init")
    public Response initializeDemoData() {
        try {
            // Create sample authors
            Author author1 = new Author("J.K.", "Rowling", LocalDate.of(1965, 7, 31), "British");
            author1.setBiography("British author, best known for the Harry Potter series");
            
            Author author2 = new Author("George", "Orwell", LocalDate.of(1903, 6, 25), "British");
            author2.setBiography("English novelist and essayist");
            
            // Create sample books
            Book book1 = new Book("Harry Potter and the Philosopher's Stone", "978-0-7475-3269-9", author1);
            book1.setPublisher("Bloomsbury");
            book1.setPublicationDate(LocalDate.of(1997, 6, 26));
            book1.setGenre("Fantasy");
            book1.setDescription("The first novel in the Harry Potter series");
            book1.setPageCount(223);
            book1.setLocation("A-001");
            
            Book book2 = new Book("1984", "978-0-452-28423-4", author2);
            book2.setPublisher("Secker & Warburg");
            book2.setPublicationDate(LocalDate.of(1949, 6, 8));
            book2.setGenre("Dystopian Fiction");
            book2.setDescription("A dystopian social science fiction novel");
            book2.setPageCount(328);
            book2.setLocation("B-001");
            
            // Add books to inventory
            bookService.addBook(book1);
            bookService.addBook(book2);
            
            // Create sample users
            User user1 = new User("John", "Doe", "john.doe@example.com");
            user1.setPhoneNumber("555-1234");
            user1.setAddress("123 Main St");
            user1.setDateOfBirth(LocalDate.of(1990, 1, 15));
            user1.setUserType(UserType.STUDENT);
            
            User user2 = new User("Jane", "Smith", "jane.smith@example.com");
            user2.setPhoneNumber("555-5678");
            user2.setAddress("456 Oak Ave");
            user2.setDateOfBirth(LocalDate.of(1985, 3, 22));
            user2.setUserType(UserType.FACULTY);
            
            // Create users
            userService.createUser(user1);
            userService.createUser(user2);
            
            return Response.ok(Map.of("message", "Demo data initialized successfully")).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", e.getMessage()))
                    .build();
        }
    }
} 