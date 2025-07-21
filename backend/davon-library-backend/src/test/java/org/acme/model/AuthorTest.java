package org.acme.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Unit tests for Author entity
 */
@DisplayName("Author Entity Tests")
class AuthorTest {

    private Author author;
    private LocalDate birthDate;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        birthDate = LocalDate.of(1896, 9, 24);
        now = LocalDateTime.now();
        author = new Author();
    }

    @Test
    @DisplayName("Test no-args constructor")
    void testNoArgsConstructor() {
        Author newAuthor = new Author();
        assertNotNull(newAuthor);
        assertNull(newAuthor.getId());
        assertNull(newAuthor.getFirstName());
        assertNull(newAuthor.getLastName());
        assertNull(newAuthor.getBiography());
        assertNull(newAuthor.getBirthDate());
        assertNull(newAuthor.getNationality());
    }

    @Test
    @DisplayName("Test all-args constructor")
    void testAllArgsConstructor() {
        Author newAuthor = new Author(1L, "F. Scott", "Fitzgerald", "American novelist",
                birthDate, "American", now, now);

        assertEquals(1L, newAuthor.getId());
        assertEquals("F. Scott", newAuthor.getFirstName());
        assertEquals("Fitzgerald", newAuthor.getLastName());
        assertEquals("American novelist", newAuthor.getBiography());
        assertEquals(birthDate, newAuthor.getBirthDate());
        assertEquals("American", newAuthor.getNationality());
        assertEquals(now, newAuthor.getCreatedAt());
        assertEquals(now, newAuthor.getUpdatedAt());
    }

    @Test
    @DisplayName("Test setters and getters")
    void testSettersAndGetters() {
        author.setId(1L);
        author.setFirstName("Jane");
        author.setLastName("Austen");
        author.setBiography("English novelist known for her wit and social commentary");
        author.setBirthDate(LocalDate.of(1775, 12, 16));
        author.setNationality("British");
        author.setCreatedAt(now);
        author.setUpdatedAt(now);

        assertEquals(1L, author.getId());
        assertEquals("Jane", author.getFirstName());
        assertEquals("Austen", author.getLastName());
        assertEquals("English novelist known for her wit and social commentary", author.getBiography());
        assertEquals(LocalDate.of(1775, 12, 16), author.getBirthDate());
        assertEquals("British", author.getNationality());
        assertEquals(now, author.getCreatedAt());
        assertEquals(now, author.getUpdatedAt());
    }

    @Test
    @DisplayName("Test equals and hashCode")
    void testEqualsAndHashCode() {
        Author author1 = new Author(1L, "George", "Orwell", "British author",
                LocalDate.of(1903, 6, 25), "British", now, now);
        Author author2 = new Author(1L, "George", "Orwell", "British author",
                LocalDate.of(1903, 6, 25), "British", now, now);
        Author author3 = new Author(2L, "Aldous", "Huxley", "British writer",
                LocalDate.of(1894, 7, 26), "British", now, now);

        assertEquals(author1, author2);
        assertNotEquals(author1, author3);
        assertEquals(author1.hashCode(), author2.hashCode());
        assertNotEquals(author1.hashCode(), author3.hashCode());
    }

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        author.setId(1L);
        author.setFirstName("Ernest");
        author.setLastName("Hemingway");
        author.setNationality("American");

        String toString = author.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Ernest"));
        assertTrue(toString.contains("Hemingway"));
        assertTrue(toString.contains("American"));
    }

    @Test
    @DisplayName("Test null values handling")
    void testNullValues() {
        assertDoesNotThrow(() -> {
            author.setFirstName(null);
            author.setLastName(null);
            author.setBiography(null);
            author.setBirthDate(null);
            author.setNationality(null);
            author.setCreatedAt(null);
            author.setUpdatedAt(null);
        });

        assertNull(author.getFirstName());
        assertNull(author.getLastName());
        assertNull(author.getBiography());
        assertNull(author.getBirthDate());
        assertNull(author.getNationality());
        assertNull(author.getCreatedAt());
        assertNull(author.getUpdatedAt());
    }

    @Test
    @DisplayName("Test birth date validation scenarios")
    void testBirthDateScenarios() {
        // Test with old birth date
        LocalDate oldDate = LocalDate.of(1200, 1, 1);
        author.setBirthDate(oldDate);
        assertEquals(oldDate, author.getBirthDate());

        // Test with recent birth date
        LocalDate recentDate = LocalDate.of(2000, 1, 1);
        author.setBirthDate(recentDate);
        assertEquals(recentDate, author.getBirthDate());

        // Test with future birth date (should be allowed at model level)
        LocalDate futureDate = LocalDate.of(2030, 1, 1);
        author.setBirthDate(futureDate);
        assertEquals(futureDate, author.getBirthDate());
    }

    @Test
    @DisplayName("Test name combinations")
    void testNameCombinations() {
        // Test with both first and last name
        author.setFirstName("Mark");
        author.setLastName("Twain");
        assertEquals("Mark", author.getFirstName());
        assertEquals("Twain", author.getLastName());

        // Test with only first name
        author.setFirstName("Plato");
        author.setLastName(null);
        assertEquals("Plato", author.getFirstName());
        assertNull(author.getLastName());

        // Test with only last name
        author.setFirstName(null);
        author.setLastName("Aristotle");
        assertNull(author.getFirstName());
        assertEquals("Aristotle", author.getLastName());
    }
}