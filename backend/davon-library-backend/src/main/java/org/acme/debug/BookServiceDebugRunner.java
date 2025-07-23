package org.acme.debug;

import org.acme.model.Book;
import org.acme.model.BookStatus;
import org.acme.model.Author;
import org.acme.model.Category;
import org.acme.service.BookService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Debug runner for BookService
 * Use this class to debug BookService methods step by step
 */
@ApplicationScoped
public class BookServiceDebugRunner {

    @Inject
    private BookService bookService;

    /**
     * Demonstrates debugging BookService operations
     * Set breakpoints on the lines marked with // DEBUG BREAKPOINT
     */
    public void debugBookServiceOperations() {
        System.out.println("=== Starting BookService Debug Session ===");

        // DEBUG BREAKPOINT: Set a breakpoint here to start debugging
        System.out.println("Step 1: Creating test data...");

        // Create test Author
        Author testAuthor = new Author();
        testAuthor.setId(1L);
        testAuthor.setFirstName("J.K.");
        testAuthor.setLastName("Rowling");
        testAuthor.setBiography("British author, best known for Harry Potter series");
        testAuthor.setNationality("British");
        testAuthor.setCreatedAt(LocalDateTime.now());
        testAuthor.setUpdatedAt(LocalDateTime.now());

        // DEBUG BREAKPOINT: Examine the testAuthor object
        System.out.println("Created author: " + testAuthor.getFirstName() + " " + testAuthor.getLastName());

        // Create test Category
        Category testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Fantasy");
        testCategory.setDescription("Fantasy and magical realism books");
        testCategory.setCreatedAt(LocalDateTime.now());
        testCategory.setUpdatedAt(LocalDateTime.now());

        // DEBUG BREAKPOINT: Examine the testCategory object
        System.out.println("Created category: " + testCategory.getName());

        // Create test Book
        Book testBook = new Book();
        testBook.setTitle("Harry Potter and the Philosopher's Stone");
        testBook.setIsbn("978-0747532699");
        testBook.setDescription("The first book in the Harry Potter series");
        testBook.setPublishDate(LocalDate.of(1997, 6, 26));
        testBook.setCoverImage("harry_potter_1.jpg");
        testBook.setStatus(BookStatus.AVAILABLE);
        testBook.setAuthor(testAuthor);
        testBook.setCategory(testCategory);
        testBook.setCreatedAt(LocalDateTime.now());
        testBook.setUpdatedAt(LocalDateTime.now());

        // DEBUG BREAKPOINT: Examine the complete testBook object
        System.out.println("Created book: " + testBook.getTitle());
        System.out.println(
                "Book author: " + testBook.getAuthor().getFirstName() + " " + testBook.getAuthor().getLastName());
        System.out.println("Book category: " + testBook.getCategory().getName());

        System.out.println("\nStep 2: Testing BookService.createBook()...");
        // DEBUG BREAKPOINT: Step INTO createBook method
        Book createdBook = bookService.createBook(testBook);

        if (createdBook != null) {
            System.out.println("✓ Book created successfully: " + createdBook.getTitle());
        } else {
            System.out.println("✗ Book creation failed");
        }

        System.out.println("\nStep 3: Testing BookService.getAllBooks()...");
        // DEBUG BREAKPOINT: Step INTO getAllBooks method
        List<Book> allBooks = bookService.getAllBooks();
        System.out.println("Retrieved " + allBooks.size() + " books from database");

        for (Book book : allBooks) {
            // DEBUG BREAKPOINT: Examine each book in the list
            System.out.println("- " + book.getTitle() + " by " +
                    (book.getAuthor() != null ? book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName()
                            : "Unknown"));
        }

        System.out.println("\nStep 4: Testing BookService.getBookById()...");
        if (!allBooks.isEmpty()) {
            Long firstBookId = allBooks.get(0).getId();
            // DEBUG BREAKPOINT: Step INTO getBookById method
            Optional<Book> foundBook = bookService.getBookById(firstBookId);

            if (foundBook.isPresent()) {
                System.out.println("✓ Found book: " + foundBook.get().getTitle());

                // DEBUG BREAKPOINT: Examine the found book's relationships
                Book book = foundBook.get();
                System.out.println("  - ISBN: " + book.getIsbn());
                System.out.println("  - Status: " + book.getStatus());
                if (book.getAuthor() != null) {
                    System.out.println(
                            "  - Author: " + book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName());
                }
                if (book.getCategory() != null) {
                    System.out.println("  - Category: " + book.getCategory().getName());
                }
            } else {
                System.out.println("✗ Book not found with ID: " + firstBookId);
            }
        }

        System.out.println("\nStep 5: Testing error scenarios...");

        // Test with null book
        // DEBUG BREAKPOINT: Step INTO createBook with null parameter
        Book nullResult = bookService.createBook(null);
        System.out.println("Creating null book result: " + (nullResult == null ? "null (expected)" : "not null"));

        // Test with non-existent ID
        // DEBUG BREAKPOINT: Step INTO getBookById with non-existent ID
        Optional<Book> nonExistentBook = bookService.getBookById(99999L);
        System.out.println("Finding non-existent book: "
                + (nonExistentBook.isPresent() ? "found (unexpected)" : "not found (expected)"));

        System.out.println("\nStep 6: Testing BookService.deleteBook()...");
        if (!allBooks.isEmpty()) {
            Long bookIdToDelete = allBooks.get(0).getId();
            System.out.println("Attempting to delete book with ID: " + bookIdToDelete);

            // DEBUG BREAKPOINT: Step INTO deleteBook method
            bookService.deleteBook(bookIdToDelete);
            System.out.println("✓ Delete operation completed");
        }

        System.out.println("\n=== BookService Debug Session Complete ===");
    }

    /**
     * Demonstrates debugging with different book scenarios
     */
    public void debugDifferentBookScenarios() {
        System.out.println("=== Testing Different Book Scenarios ===");

        // Scenario 1: Book with minimal data
        Book minimalBook = new Book();
        minimalBook.setTitle("Minimal Book");
        minimalBook.setStatus(BookStatus.AVAILABLE);

        // DEBUG BREAKPOINT: Test creating book with minimal data
        Book result1 = bookService.createBook(minimalBook);
        System.out.println("Minimal book result: " + (result1 != null ? result1.getTitle() : "null"));

        // Scenario 2: Book with different status
        Book borrowedBook = new Book();
        borrowedBook.setTitle("Currently Borrowed Book");
        borrowedBook.setStatus(BookStatus.BORROWED);

        // DEBUG BREAKPOINT: Test creating borrowed book
        Book result2 = bookService.createBook(borrowedBook);
        System.out.println("Borrowed book status: " + (result2 != null ? result2.getStatus() : "null"));

        // Scenario 3: Book in maintenance
        Book maintenanceBook = new Book();
        maintenanceBook.setTitle("Book Under Maintenance");
        maintenanceBook.setStatus(BookStatus.MAINTENANCE);

        // DEBUG BREAKPOINT: Test creating maintenance book
        Book result3 = bookService.createBook(maintenanceBook);
        System.out.println("Maintenance book result: " + (result3 != null ? result3.getTitle() : "null"));

        System.out.println("=== Different Scenarios Testing Complete ===");
    }
}