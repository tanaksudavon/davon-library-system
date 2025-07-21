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
    private LocalDate publishDate;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        publishDate = LocalDate.of(2023, 1, 15);
        now = LocalDateTime.now();
        book = new Book();
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
    }

    @Test
    @DisplayName("Test all-args constructor")
    void testAllArgsConstructor() {
        Book newBook = new Book(1L, "Test Title", "978-0123456789", "Test Description",
                publishDate, "cover.jpg", BookStatus.AVAILABLE,
                1L, 1L, now, now);

        assertEquals(1L, newBook.getId());
        assertEquals("Test Title", newBook.getTitle());
        assertEquals("978-0123456789", newBook.getIsbn());
        assertEquals("Test Description", newBook.getDescription());
        assertEquals(publishDate, newBook.getPublishDate());
        assertEquals("cover.jpg", newBook.getCoverImage());
        assertEquals(BookStatus.AVAILABLE, newBook.getStatus());
        assertEquals(1L, newBook.getAuthorId());
        assertEquals(1L, newBook.getCategoryId());
        assertEquals(now, newBook.getCreatedAt());
        assertEquals(now, newBook.getUpdatedAt());
    }

    @Test
    @DisplayName("Test setters and getters")
    void testSettersAndGetters() {
        book.setId(1L);
        book.setTitle("The Great Gatsby");
        book.setIsbn("978-0743273565");
        book.setDescription("A classic American novel");
        book.setPublishDate(publishDate);
        book.setCoverImage("gatsby.jpg");
        book.setStatus(BookStatus.AVAILABLE);
        book.setAuthorId(1L);
        book.setCategoryId(2L);
        book.setCreatedAt(now);
        book.setUpdatedAt(now);

        assertEquals(1L, book.getId());
        assertEquals("The Great Gatsby", book.getTitle());
        assertEquals("978-0743273565", book.getIsbn());
        assertEquals("A classic American novel", book.getDescription());
        assertEquals(publishDate, book.getPublishDate());
        assertEquals("gatsby.jpg", book.getCoverImage());
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
        assertEquals(1L, book.getAuthorId());
        assertEquals(2L, book.getCategoryId());
        assertEquals(now, book.getCreatedAt());
        assertEquals(now, book.getUpdatedAt());
    }

    @Test
    @DisplayName("Test all book statuses")
    void testBookStatuses() {
        book.setStatus(BookStatus.AVAILABLE);
        assertEquals(BookStatus.AVAILABLE, book.getStatus());

        book.setStatus(BookStatus.BORROWED);
        assertEquals(BookStatus.BORROWED, book.getStatus());

        book.setStatus(BookStatus.MAINTENANCE);
        assertEquals(BookStatus.MAINTENANCE, book.getStatus());

        book.setStatus(BookStatus.RESERVED);
        assertEquals(BookStatus.RESERVED, book.getStatus());
    }

    @Test
    @DisplayName("Test equals and hashCode")
    void testEqualsAndHashCode() {
        Book book1 = new Book(1L, "Title", "ISBN", "Description", publishDate, "cover.jpg",
                BookStatus.AVAILABLE, 1L, 1L, now, now);
        Book book2 = new Book(1L, "Title", "ISBN", "Description", publishDate, "cover.jpg",
                BookStatus.AVAILABLE, 1L, 1L, now, now);
        Book book3 = new Book(2L, "Different Title", "Different ISBN", "Different Description",
                publishDate, "cover.jpg", BookStatus.BORROWED, 2L, 2L, now, now);

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
    @DisplayName("Test null values handling")
    void testNullValues() {
        assertDoesNotThrow(() -> {
            book.setTitle(null);
            book.setIsbn(null);
            book.setDescription(null);
            book.setCoverImage(null);
            book.setStatus(null);
            book.setAuthorId(null);
            book.setCategoryId(null);
            book.setPublishDate(null);
            book.setCreatedAt(null);
            book.setUpdatedAt(null);
        });

        assertNull(book.getTitle());
        assertNull(book.getIsbn());
        assertNull(book.getDescription());
        assertNull(book.getCoverImage());
        assertNull(book.getStatus());
        assertNull(book.getAuthorId());
        assertNull(book.getCategoryId());
        assertNull(book.getPublishDate());
        assertNull(book.getCreatedAt());
        assertNull(book.getUpdatedAt());
    }
}