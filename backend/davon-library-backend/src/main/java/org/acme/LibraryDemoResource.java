package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.model.*;

import java.time.LocalDate;

/**
 * Demo resource to showcase the library domain model functionality.
 * This demonstrates how the domain classes work together.
 */
@Path("/library-demo")
public class LibraryDemoResource {

    @GET
    @Path("/demo")
    @Produces(MediaType.APPLICATION_JSON)
    public LibraryDemo getDemoData() {
        // Create an author
        Author author = new Author("J.K.", "Rowling");
        author.setId(1L);
        author.setNationality("British");
        author.setDateOfBirth(LocalDate.of(1965, 7, 31));
        author.setBiography("British author, best known for the Harry Potter series");

        // Create a book
        Book book = new Book("Harry Potter and the Philosopher's Stone", "978-0-7475-3269-9", author);
        book.setId(1L);
        book.setPublisher("Bloomsbury");
        book.setPublicationDate(LocalDate.of(1997, 6, 26));
        book.setGenre("Fantasy");
        book.setPageCount(223);
        book.setLocation("A1-001");

        // Add book to author
        author.addBook(book);

        // Create a user
        User user = new User("John", "Doe", "john.doe@example.com");
        user.setId(1L);
        user.setPhoneNumber("+1-555-0123");
        user.setUserType(User.UserType.STUDENT);
        user.setAddress("123 Main St, Anytown, USA");

        // Create a loan
        Loan loan = new Loan(user, book);
        loan.setId(1L);

        // Simulate checkout
        book.checkOut();
        user.addCurrentLoan(loan);

        // Create demo response
        LibraryDemo demo = new LibraryDemo();
        demo.author = author;
        demo.book = book;
        demo.user = user;
        demo.loan = loan;
        demo.systemInfo = new SystemInfo();

        return demo;
    }

    /**
     * Demo response class to showcase the domain model
     */
    public static class LibraryDemo {
        public Author author;
        public Book book;
        public User user;
        public Loan loan;
        public SystemInfo systemInfo;
    }

    /**
     * System information class
     */
    public static class SystemInfo {
        public String message = "Davon Library System - Domain Model Demo";
        public String[] features = {
            "✅ BaseEntity with inheritance",
            "✅ Author with books relationship",
            "✅ Book with status management",
            "✅ User with different types and status",
            "✅ Loan with business logic (overdue, fines, renewals)",
            "✅ Proper encapsulation and OOP principles",
            "✅ Comprehensive Javadoc documentation",
            "✅ Defensive programming practices"
        };
        public String[] businessLogic = {
            "Book checkout/return functionality",
            "User borrowing limits by type",
            "Loan renewal with limits",
            "Overdue fine calculations",
            "Automatic timestamp management",
            "Bidirectional relationships"
        };
    }
} 