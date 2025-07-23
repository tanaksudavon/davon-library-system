package org.acme.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Unit tests for Book entity
 */
@DisplayName("Book Entity Tests")
class BookTest {

    private Book book;
    private Author testAuthor;
    private Category testCategory;
    private LocalDate publishDate;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        publishDate = LocalDate.of(2023, 1, 15);
        now = LocalDateTime.now();
        book = new Book();

        // Create test Author and Category objects
        testAuthor = new Author();
        testAuthor.setId(1L);
        testAuthor.setFirstName("Test");
        testAuthor.setLastName("Author");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Fiction");
    }

    @Test
    @DisplayName("Test no-args constructor")
    void testNoArgsConstructor() {
        Book newBook = new Book();
        assertNotNull(newBook);
        assertNull(newBook.getId());
        assertNull(newBook.getTitle());
        assertNull(newBook.getIsbn());
        assertNull(newBook.getStatus());
        assertNull(newBook.getAuthor());
        assertNull(newBook.getCategory());
    }

    @Test
    @DisplayName("Test all-args constructor")
    void testAllArgsConstructor() {
        Book newBook = new Book(1L, "Test Title", "978-0123456789", "Test Description",
                publishDate, "cover.jpg", BookStatus.AVAILABLE,
                testAuthor, testCategory, now, now);

        assertEquals(1L, newBook.getId());
        assertEquals("Test Title", newBook.getTitle());
        assertEquals("978-0123456789", newBook.getIsbn());
        assertEquals("Test Description", newBook.getDescription());
        assertEquals(publishDate, newBook.getPublishDate());
        assertEquals("cover.jpg", newBook.getCoverImage());
        assertEquals(BookStatus.AVAILABLE, newBook.getStatus());
        assertEquals(testAuthor, newBook.getAuthor());
        assertEquals(testCategory, newBook.getCategory());
        assertEquals(now, newBook.getCreatedAt());
        assertEquals(now, newBook.getUpdatedAt());
    }

    @Test
    @DisplayName("Test setters and getters")
    void testSettersAndGetters() {
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("978-0987654321");
        book.setDescription("A test book description");
        book.setPublishDate(publishDate);
        book.setCoverImage("test-cover.jpg");
        book.setStatus(BookStatus.AVAILABLE);
        book.setAuthor(testAuthor);
        book.setCategory(testCategory);
        book.setCreatedAt(now);
        book.setUpdatedAt(now);

        assertEquals(1L, book.getId());
        assertEquals("Test Book", book.getTitle());
        assertEquals("978-0987654321", book.getIsbn());
        assertEquals("A test book description", book.getDescription());
        assertEquals(publishDate, book.getPublishDate());
        assertEquals("test-cover.jpg", book.getCoverImage());
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
        assertEquals(testAuthor, book.getAuthor());
        assertEquals(testCategory, book.getCategory());
        assertEquals(now, book.getCreatedAt());
        assertEquals(now, book.getUpdatedAt());
    }

    @Test
    @DisplayName("Test book status changes")
    void testBookStatusChanges() {
        book.setStatus(BookStatus.AVAILABLE);
        assertEquals(BookStatus.AVAILABLE, book.getStatus());

        book.setStatus(BookStatus.BORROWED);
        assertEquals(BookStatus.BORROWED, book.getStatus());

        book.setStatus(BookStatus.RESERVED);
        assertEquals(BookStatus.RESERVED, book.getStatus());

        book.setStatus(BookStatus.MAINTENANCE);
        assertEquals(BookStatus.MAINTENANCE, book.getStatus());
    }

    @Test
    @DisplayName("Test equals and hashCode")
    void testEqualsAndHashCode() {
        Book book1 = new Book(1L, "Title", "ISBN", "Description", publishDate, "cover.jpg",
                BookStatus.AVAILABLE, testAuthor, testCategory, now, now);
        Book book2 = new Book(1L, "Title", "ISBN", "Description", publishDate, "cover.jpg",
                BookStatus.AVAILABLE, testAuthor, testCategory, now, now);

        Author differentAuthor = new Author();
        differentAuthor.setId(2L);
        differentAuthor.setFirstName("Different");
        differentAuthor.setLastName("Author");

        Book book3 = new Book(2L, "Different Title", "Different ISBN", "Different Description",
                publishDate, "cover.jpg", BookStatus.BORROWED, differentAuthor, testCategory, now, now);

        assertEquals(book1, book2);
        assertNotEquals(book1, book3);
        assertEquals(book1.hashCode(), book2.hashCode());
        assertNotEquals(book1.hashCode(), book3.hashCode());
    }

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("978-0123456789");

        String toString = book.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Test Book"));
        assertTrue(toString.contains("978-0123456789"));
    }

    @Test
    @DisplayName("Test object relationships")
    void testObjectRelationships() {
        book.setAuthor(testAuthor);
        book.setCategory(testCategory);

        assertNotNull(book.getAuthor());
        assertNotNull(book.getCategory());
        assertEquals("Test Author", book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName());
        assertEquals("Fiction", book.getCategory().getName());
        assertEquals(1L, book.getAuthor().getId());
        assertEquals(1L, book.getCategory().getId());
    }

    @Test
    @DisplayName("Test null values handling")
    void testNullValues() {
        assertDoesNotThrow(() -> {
            book.setAuthor(null);
            book.setCategory(null);
            book.setTitle(null);
            book.setIsbn(null);
            book.setDescription(null);
            book.setPublishDate(null);
            book.setCoverImage(null);
            book.setStatus(null);
            book.setCreatedAt(null);
            book.setUpdatedAt(null);
        });

        assertNull(book.getAuthor());
        assertNull(book.getCategory());
        assertNull(book.getTitle());
        assertNull(book.getIsbn());
        assertNull(book.getDescription());
        assertNull(book.getPublishDate());
        assertNull(book.getCoverImage());
        assertNull(book.getStatus());
        assertNull(book.getCreatedAt());
        assertNull(book.getUpdatedAt());
    }
}