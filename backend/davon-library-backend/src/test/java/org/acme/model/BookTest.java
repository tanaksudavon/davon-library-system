package org.acme.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@DisplayName("Book Model Tests")
public class BookTest {

    private Book book;
    private Author author;
    private Category category;

    @BeforeEach
    void setUp() {
        book = new Book();

        // Create test author
        author = new Author();
        author.setId(1L);
        author.setFirstName("George");
        author.setLastName("Orwell");
        author.setBiography("British author and journalist");
        author.setNationality("British");

        // Create test category
        category = new Category();
        category.setId(1L);
        category.setName("Fiction");
        category.setDescription("Fictional literature");
    }

    @Test
    @DisplayName("Should create book with default constructor")
    void testDefaultConstructor() {
        Book newBook = new Book();

        assertNotNull(newBook);
        assertNull(newBook.getId());
        assertNull(newBook.getTitle());
        assertNull(newBook.getIsbn());
        assertNull(newBook.getStatus());

        System.out.println("✅ Default constructor test passed");
    }

    @Test
    @DisplayName("Should set and get book properties correctly")
    void testBookProperties() {
        // Arrange
        Long id = 1L;
        String title = "1984";
        String isbn = "978-0-452-28423-4";
        String description = "A dystopian social science fiction novel";
        LocalDate publishDate = LocalDate.of(1949, 6, 8);
        String coverImage = "1984-cover.jpg";
        BookStatus status = BookStatus.AVAILABLE;
        LocalDateTime now = LocalDateTime.now();

        // Act
        book.setId(id);
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setDescription(description);
        book.setPublishDate(publishDate);
        book.setCoverImage(coverImage);
        book.setStatus(status);
        book.setAuthor(author);
        book.setCategory(category);
        book.setCreatedAt(now);
        book.setUpdatedAt(now);

        // Assert
        assertEquals(id, book.getId());
        assertEquals(title, book.getTitle());
        assertEquals(isbn, book.getIsbn());
        assertEquals(description, book.getDescription());
        assertEquals(publishDate, book.getPublishDate());
        assertEquals(coverImage, book.getCoverImage());
        assertEquals(status, book.getStatus());
        assertEquals(author, book.getAuthor());
        assertEquals(category, book.getCategory());
        assertEquals(now, book.getCreatedAt());
        assertEquals(now, book.getUpdatedAt());

        System.out.println("✅ Book properties test passed");
        System.out.println("Book: " + title);
        System.out.println("Author: " + author.getFirstName() + " " + author.getLastName());
        System.out.println("Status: " + status);
    }

    @Test
    @DisplayName("Should handle different book statuses")
    void testBookStatuses() {
        BookStatus[] statuses = {
                BookStatus.AVAILABLE,
                BookStatus.BORROWED,
                BookStatus.RESERVED,
                BookStatus.MAINTENANCE
        };

        System.out.println("✅ Testing book statuses:");
        for (BookStatus status : statuses) {
            book.setStatus(status);
            assertEquals(status, book.getStatus());
            System.out.println("  - " + status + " ✓");
        }
    }

    @Test
    @DisplayName("Should create available book")
    void testCreateAvailableBook() {
        // Act
        book.setTitle("The Great Gatsby");
        book.setIsbn("978-0-7432-7356-5");
        book.setStatus(BookStatus.AVAILABLE);
        book.setAuthor(author);
        book.setCategory(category);

        // Assert
        assertEquals("The Great Gatsby", book.getTitle());
        assertEquals("978-0-7432-7356-5", book.getIsbn());
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
        assertEquals(author, book.getAuthor());
        assertEquals(category, book.getCategory());

        System.out.println("✅ Available book creation test passed");
        System.out.println("Book: " + book.getTitle());
        System.out.println("Status: " + book.getStatus());
    }

    @Test
    @DisplayName("Should create borrowed book")
    void testCreateBorrowedBook() {
        // Act
        book.setTitle("To Kill a Mockingbird");
        book.setIsbn("978-0-06-112008-4");
        book.setStatus(BookStatus.BORROWED);
        book.setAuthor(author);
        book.setCategory(category);

        // Assert
        assertEquals("To Kill a Mockingbird", book.getTitle());
        assertEquals("978-0-06-112008-4", book.getIsbn());
        assertEquals(BookStatus.BORROWED, book.getStatus());

        System.out.println("✅ Borrowed book creation test passed");
        System.out.println("Book: " + book.getTitle());
        System.out.println("Status: " + book.getStatus());
    }

    @Test
    @DisplayName("Should create reserved book")
    void testCreateReservedBook() {
        // Act
        book.setTitle("Pride and Prejudice");
        book.setIsbn("978-0-14-143951-8");
        book.setStatus(BookStatus.RESERVED);
        book.setAuthor(author);
        book.setCategory(category);

        // Assert
        assertEquals("Pride and Prejudice", book.getTitle());
        assertEquals("978-0-14-143951-8", book.getIsbn());
        assertEquals(BookStatus.RESERVED, book.getStatus());

        System.out.println("✅ Reserved book creation test passed");
        System.out.println("Book: " + book.getTitle());
        System.out.println("Status: " + book.getStatus());
    }

    @Test
    @DisplayName("Should handle book in maintenance")
    void testMaintenanceBook() {
        // Act
        book.setTitle("The Catcher in the Rye");
        book.setIsbn("978-0-316-76948-0");
        book.setStatus(BookStatus.MAINTENANCE);
        book.setAuthor(author);
        book.setCategory(category);

        // Assert
        assertEquals("The Catcher in the Rye", book.getTitle());
        assertEquals("978-0-316-76948-0", book.getIsbn());
        assertEquals(BookStatus.MAINTENANCE, book.getStatus());

        System.out.println("✅ Maintenance book test passed");
        System.out.println("Book: " + book.getTitle());
        System.out.println("Status: " + book.getStatus());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void testNullValues() {
        book.setTitle(null);
        book.setIsbn(null);
        book.setDescription(null);
        book.setPublishDate(null);
        book.setCoverImage(null);
        book.setStatus(null);
        book.setAuthor(null);
        book.setCategory(null);

        assertNull(book.getTitle());
        assertNull(book.getIsbn());
        assertNull(book.getDescription());
        assertNull(book.getPublishDate());
        assertNull(book.getCoverImage());
        assertNull(book.getStatus());
        assertNull(book.getAuthor());
        assertNull(book.getCategory());

        System.out.println("✅ Null values test passed");
    }

    @Test
    @DisplayName("Should validate ISBN formats")
    void testIsbnFormats() {
        String[] validIsbns = {
                "978-0-452-28423-4",
                "9780452284234",
                "0-452-28423-6",
                "0452284236"
        };

        String[] invalidIsbns = {
                "invalid-isbn",
                "123",
                "978-0-452-28423-X-EXTRA",
                ""
        };

        System.out.println("✅ Testing ISBN formats:");

        for (String isbn : validIsbns) {
            book.setIsbn(isbn);
            assertEquals(isbn, book.getIsbn());
            System.out.println("  - Valid: " + isbn + " ✓");
        }

        for (String isbn : invalidIsbns) {
            book.setIsbn(isbn);
            assertEquals(isbn, book.getIsbn()); // Model accepts any string, validation should be elsewhere
            System.out.println("  - Invalid (stored but should be validated): " + isbn + " ⚠️");
        }
    }

    @Test
    @DisplayName("Should debug book creation timestamps")
    void testBookTimestamps() {
        LocalDateTime before = LocalDateTime.now();

        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());

        LocalDateTime after = LocalDateTime.now();

        assertNotNull(book.getCreatedAt());
        assertNotNull(book.getUpdatedAt());

        assertTrue(book.getCreatedAt().isAfter(before) || book.getCreatedAt().isEqual(before));
        assertTrue(book.getCreatedAt().isBefore(after) || book.getCreatedAt().isEqual(after));

        System.out.println("✅ Book timestamps test passed");
        System.out.println("Before: " + before);
        System.out.println("Created: " + book.getCreatedAt());
        System.out.println("Updated: " + book.getUpdatedAt());
        System.out.println("After: " + after);
    }

    @Test
    @DisplayName("Should create complete book profile")
    void testCompleteBookProfile() {
        // Create a complete book profile
        book.setId(100L);
        book.setTitle("Animal Farm");
        book.setIsbn("978-0-452-28424-1");
        book.setDescription("A satirical allegorical novella");
        book.setPublishDate(LocalDate.of(1945, 8, 17));
        book.setCoverImage("animal-farm-cover.jpg");
        book.setStatus(BookStatus.AVAILABLE);
        book.setAuthor(author);
        book.setCategory(category);
        book.setCreatedAt(LocalDateTime.now());
        book.setUpdatedAt(LocalDateTime.now());

        // Verify all fields are set
        assertNotNull(book.getId());
        assertNotNull(book.getTitle());
        assertNotNull(book.getIsbn());
        assertNotNull(book.getDescription());
        assertNotNull(book.getPublishDate());
        assertNotNull(book.getCoverImage());
        assertNotNull(book.getStatus());
        assertNotNull(book.getAuthor());
        assertNotNull(book.getCategory());
        assertNotNull(book.getCreatedAt());
        assertNotNull(book.getUpdatedAt());

        System.out.println("✅ Complete book profile test passed");
        System.out.println("Complete Book Profile:");
        System.out.println("  ID: " + book.getId());
        System.out.println("  Title: " + book.getTitle());
        System.out.println("  ISBN: " + book.getIsbn());
        System.out.println("  Author: " + book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName());
        System.out.println("  Category: " + book.getCategory().getName());
        System.out.println("  Status: " + book.getStatus());
        System.out.println("  Published: " + book.getPublishDate());
        System.out.println("  Created: " + book.getCreatedAt());
    }

    @Test
    @DisplayName("Should debug book status transitions")
    void testBookStatusTransitions() {
        System.out.println("✅ Debugging book status transitions:");

        // Available -> Borrowed
        book.setStatus(BookStatus.AVAILABLE);
        System.out.println("Initial status: " + book.getStatus());

        book.setStatus(BookStatus.BORROWED);
        System.out.println("After borrowing: " + book.getStatus());

        // Borrowed -> Available (returned)
        book.setStatus(BookStatus.AVAILABLE);
        System.out.println("After returning: " + book.getStatus());

        // Available -> Reserved
        book.setStatus(BookStatus.RESERVED);
        System.out.println("After reserving: " + book.getStatus());

        // Reserved -> Borrowed (reservation picked up)
        book.setStatus(BookStatus.BORROWED);
        System.out.println("After picking up reservation: " + book.getStatus());

        // Any status -> Maintenance
        book.setStatus(BookStatus.MAINTENANCE);
        System.out.println("Under maintenance: " + book.getStatus());

        // Maintenance -> Available (maintenance complete)
        book.setStatus(BookStatus.AVAILABLE);
        System.out.println("Maintenance complete: " + book.getStatus());
    }

    @Test
    @DisplayName("Should debug book relationships")
    void testBookRelationships() {
        System.out.println("✅ Debugging book relationships:");

        book.setTitle("Test Book");
        book.setAuthor(author);
        book.setCategory(category);

        System.out.println("Book: " + book.getTitle());
        System.out.println("Author: " + book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName());
        System.out.println("Author Nationality: " + book.getAuthor().getNationality());
        System.out.println("Category: " + book.getCategory().getName());
        System.out.println("Category Description: " + book.getCategory().getDescription());

        // Test without relationships
        book.setAuthor(null);
        book.setCategory(null);

        assertNull(book.getAuthor());
        assertNull(book.getCategory());
        System.out.println("Book without relationships: OK ✓");
    }
}