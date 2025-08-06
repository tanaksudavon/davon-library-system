package org.acme.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import org.acme.model.Author;
import org.acme.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Debug/Test class for AuthorService
 * Used for debugging author management functionality
 */
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorService authorService;

    private Author testAuthor1;
    private Author testAuthor2;
    private Author testAuthor3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Debug: Setup test data
        System.out.println("DEBUG: Setting up AuthorServiceTest");

        testAuthor1 = new Author();
        testAuthor1.setId(1L);
        testAuthor1.setFirstName("John");
        testAuthor1.setLastName("Doe");
        testAuthor1.setBiography("Famous author of mystery novels");
        testAuthor1.setBirthDate(LocalDate.of(1970, 5, 15));
        testAuthor1.setNationality("American");
        testAuthor1.setCreatedAt(LocalDateTime.now().minusDays(10));
        testAuthor1.setUpdatedAt(LocalDateTime.now().minusDays(5));

        testAuthor2 = new Author();
        testAuthor2.setId(2L);
        testAuthor2.setFirstName("Jane");
        testAuthor2.setLastName("Smith");
        testAuthor2.setBiography("Renowned science fiction writer");
        testAuthor2.setBirthDate(LocalDate.of(1985, 8, 22));
        testAuthor2.setNationality("British");
        testAuthor2.setCreatedAt(LocalDateTime.now().minusDays(8));
        testAuthor2.setUpdatedAt(LocalDateTime.now().minusDays(3));

        testAuthor3 = new Author();
        testAuthor3.setId(3L);
        testAuthor3.setFirstName("Michael");
        testAuthor3.setLastName("Johnson");
        testAuthor3.setBiography("Contemporary fiction author");
        testAuthor3.setBirthDate(LocalDate.of(1965, 12, 3));
        testAuthor3.setNationality("Canadian");
        testAuthor3.setCreatedAt(LocalDateTime.now().minusDays(15));
        testAuthor3.setUpdatedAt(LocalDateTime.now().minusDays(1));

        System.out.println(
                "DEBUG: Test authors initialized - " + testAuthor1.getFirstName() + " " + testAuthor1.getLastName() +
                        ", " + testAuthor2.getFirstName() + " " + testAuthor2.getLastName() +
                        ", " + testAuthor3.getFirstName() + " " + testAuthor3.getLastName());
    }

    @Test
    @DisplayName("Should get all authors successfully")
    void testGetAllAuthors_Success() {
        System.out.println("DEBUG: Testing getAllAuthors - Success scenario");

        // Given
        List<Author> expectedAuthors = Arrays.asList(testAuthor1, testAuthor2, testAuthor3);
        when(authorRepository.listAll()).thenReturn(expectedAuthors);

        // When
        List<Author> result = authorService.getAllAuthors();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(expectedAuthors, result);

        verify(authorRepository, times(1)).listAll();
        System.out.println("DEBUG: getAllAuthors returned " + result.size() + " authors");
    }

    @Test
    @DisplayName("Should get all authors when repository is empty")
    void testGetAllAuthors_EmptyRepository() {
        System.out.println("DEBUG: Testing getAllAuthors - Empty repository scenario");

        // Given
        when(authorRepository.listAll()).thenReturn(Collections.emptyList());

        // When
        List<Author> result = authorService.getAllAuthors();

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());

        verify(authorRepository, times(1)).listAll();
        System.out.println("DEBUG: getAllAuthors returned empty list as expected");
    }

    @Test
    @DisplayName("Should get author by ID successfully")
    void testGetAuthorById_Success() {
        System.out.println("DEBUG: Testing getAuthorById - Success scenario");

        // Given
        Long authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(testAuthor1);

        // When
        Author result = authorService.getAuthorById(authorId);

        // Then
        assertNotNull(result);
        assertEquals(testAuthor1.getId(), result.getId());
        assertEquals(testAuthor1.getFirstName(), result.getFirstName());
        assertEquals(testAuthor1.getLastName(), result.getLastName());

        verify(authorRepository, times(1)).findById(authorId);
        System.out
                .println("DEBUG: getAuthorById returned author: " + result.getFirstName() + " " + result.getLastName());
    }

    @Test
    @DisplayName("Should return null when author not found by ID")
    void testGetAuthorById_NotFound() {
        System.out.println("DEBUG: Testing getAuthorById - Not found scenario");

        // Given
        Long nonExistentId = 999L;
        when(authorRepository.findById(nonExistentId)).thenReturn(null);

        // When
        Author result = authorService.getAuthorById(nonExistentId);

        // Then
        assertNull(result);

        verify(authorRepository, times(1)).findById(nonExistentId);
        System.out.println("DEBUG: getAuthorById returned null for non-existent ID: " + nonExistentId);
    }

    @Test
    @DisplayName("Should create new author successfully")
    void testCreateAuthor_Success() {
        System.out.println("DEBUG: Testing createAuthor - Success scenario");

        // Given
        Author newAuthor = new Author();
        newAuthor.setFirstName("New");
        newAuthor.setLastName("Author");
        newAuthor.setBiography("New author biography");
        newAuthor.setBirthDate(LocalDate.of(1990, 1, 1));
        newAuthor.setNationality("German");

        // When
        Author result = authorService.createAuthor(newAuthor);

        // Then
        assertNotNull(result);
        assertEquals("New", result.getFirstName());
        assertEquals("Author", result.getLastName());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(authorRepository, times(1)).persist(newAuthor);
        System.out.println("DEBUG: createAuthor successful for: " + result.getFirstName() + " " + result.getLastName());
    }

    @Test
    @DisplayName("Should create author with minimal required fields")
    void testCreateAuthor_MinimalFields() {
        System.out.println("DEBUG: Testing createAuthor - Minimal fields scenario");

        // Given
        Author minimalAuthor = new Author();
        minimalAuthor.setFirstName("Min");
        minimalAuthor.setLastName("Author");

        // When
        Author result = authorService.createAuthor(minimalAuthor);

        // Then
        assertNotNull(result);
        assertEquals("Min", result.getFirstName());
        assertEquals("Author", result.getLastName());
        assertNull(result.getBiography());
        assertNull(result.getBirthDate());
        assertNull(result.getNationality());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(authorRepository, times(1)).persist(minimalAuthor);
        System.out.println("DEBUG: createAuthor with minimal fields successful");
    }

    @Test
    @DisplayName("Should update existing author successfully")
    void testUpdateAuthor_Success() {
        System.out.println("DEBUG: Testing updateAuthor - Success scenario");

        // Given
        Long authorId = 1L;
        Author updatedData = new Author();
        updatedData.setFirstName("Updated");
        updatedData.setLastName("Name");
        updatedData.setBiography("Updated biography");
        updatedData.setBirthDate(LocalDate.of(1975, 6, 20));
        updatedData.setNationality("French");

        when(authorRepository.findById(authorId)).thenReturn(testAuthor1);

        // When
        Author result = authorService.updateAuthor(authorId, updatedData);

        // Then
        assertNotNull(result);
        assertEquals("Updated", result.getFirstName());
        assertEquals("Name", result.getLastName());
        assertEquals("Updated biography", result.getBiography());
        assertEquals(LocalDate.of(1975, 6, 20), result.getBirthDate());
        assertEquals("French", result.getNationality());
        assertNotNull(result.getUpdatedAt());

        verify(authorRepository, times(1)).findById(authorId);
        System.out.println(
                "DEBUG: updateAuthor successful - updated to: " + result.getFirstName() + " " + result.getLastName());
    }

    @Test
    @DisplayName("Should return null when updating non-existent author")
    void testUpdateAuthor_NotFound() {
        System.out.println("DEBUG: Testing updateAuthor - Not found scenario");

        // Given
        Long nonExistentId = 999L;
        Author updatedData = new Author();
        updatedData.setFirstName("Updated");
        updatedData.setLastName("Name");

        when(authorRepository.findById(nonExistentId)).thenReturn(null);

        // When
        Author result = authorService.updateAuthor(nonExistentId, updatedData);

        // Then
        assertNull(result);

        verify(authorRepository, times(1)).findById(nonExistentId);
        verify(authorRepository, never()).persist((Author) any());
        System.out.println("DEBUG: updateAuthor returned null for non-existent ID: " + nonExistentId);
    }

    @Test
    @DisplayName("Should delete existing author successfully")
    void testDeleteAuthor_Success() {
        System.out.println("DEBUG: Testing deleteAuthor - Success scenario");

        // Given
        Long authorId = 1L;
        when(authorRepository.findById(authorId)).thenReturn(testAuthor1);

        // When
        boolean result = authorService.deleteAuthor(authorId);

        // Then
        assertTrue(result);

        verify(authorRepository, times(1)).findById(authorId);
        verify(authorRepository, times(1)).delete(testAuthor1);
        System.out.println("DEBUG: deleteAuthor successful for ID: " + authorId);
    }

    @Test
    @DisplayName("Should return false when deleting non-existent author")
    void testDeleteAuthor_NotFound() {
        System.out.println("DEBUG: Testing deleteAuthor - Not found scenario");

        // Given
        Long nonExistentId = 999L;
        when(authorRepository.findById(nonExistentId)).thenReturn(null);

        // When
        boolean result = authorService.deleteAuthor(nonExistentId);

        // Then
        assertFalse(result);

        verify(authorRepository, times(1)).findById(nonExistentId);
        verify(authorRepository, never()).delete(any());
        System.out.println("DEBUG: deleteAuthor returned false for non-existent ID: " + nonExistentId);
    }

    @Test
    @DisplayName("Should search authors by name successfully")
    void testSearchAuthorsByName_Success() {
        System.out.println("DEBUG: Testing searchAuthorsByName - Success scenario");

        // Given
        String searchName = "John";
        List<Author> expectedResults = Arrays.asList(testAuthor1, testAuthor3); // John Doe and Michael Johnson

        @SuppressWarnings("unchecked")
        PanacheQuery<Author> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(expectedResults);
        when(authorRepository.find(eq("LOWER(firstName) LIKE ?1 OR LOWER(lastName) LIKE ?1"), eq("%john%")))
                .thenReturn(query);

        // When
        List<Author> result = authorService.searchAuthorsByName(searchName);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedResults, result);

        verify(authorRepository, times(1)).find(eq("LOWER(firstName) LIKE ?1 OR LOWER(lastName) LIKE ?1"),
                eq("%john%"));
        System.out.println("DEBUG: searchAuthorsByName found " + result.size() + " authors for search: " + searchName);
    }

    @Test
    @DisplayName("Should return all authors when search name is null")
    void testSearchAuthorsByName_NullName() {
        System.out.println("DEBUG: Testing searchAuthorsByName - Null name scenario");

        // Given
        List<Author> allAuthors = Arrays.asList(testAuthor1, testAuthor2, testAuthor3);
        when(authorRepository.listAll()).thenReturn(allAuthors);

        // When
        List<Author> result = authorService.searchAuthorsByName(null);

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(allAuthors, result);

        verify(authorRepository, times(1)).listAll();
        verify(authorRepository, never()).find(anyString(), (Object[]) any());
        System.out.println("DEBUG: searchAuthorsByName returned all authors for null search");
    }

    @Test
    @DisplayName("Should return all authors when search name is empty")
    void testSearchAuthorsByName_EmptyName() {
        System.out.println("DEBUG: Testing searchAuthorsByName - Empty name scenario");

        // Given
        List<Author> allAuthors = Arrays.asList(testAuthor1, testAuthor2, testAuthor3);
        when(authorRepository.listAll()).thenReturn(allAuthors);

        // When
        List<Author> result = authorService.searchAuthorsByName("   ");

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(allAuthors, result);

        verify(authorRepository, times(1)).listAll();
        verify(authorRepository, never()).find(anyString(), (Object[]) any());
        System.out.println("DEBUG: searchAuthorsByName returned all authors for empty search");
    }

    @Test
    @DisplayName("Should return empty list when no authors match search")
    void testSearchAuthorsByName_NoMatches() {
        System.out.println("DEBUG: Testing searchAuthorsByName - No matches scenario");

        // Given
        String searchName = "NonExistent";

        @SuppressWarnings("unchecked")
        PanacheQuery<Author> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(Collections.emptyList());
        when(authorRepository.find(eq("LOWER(firstName) LIKE ?1 OR LOWER(lastName) LIKE ?1"), eq("%nonexistent%")))
                .thenReturn(query);

        // When
        List<Author> result = authorService.searchAuthorsByName(searchName);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());

        verify(authorRepository, times(1)).find(eq("LOWER(firstName) LIKE ?1 OR LOWER(lastName) LIKE ?1"),
                eq("%nonexistent%"));
        System.out.println("DEBUG: searchAuthorsByName returned empty list for search: " + searchName);
    }

    @Test
    @DisplayName("Should handle case-insensitive search")
    void testSearchAuthorsByName_CaseInsensitive() {
        System.out.println("DEBUG: Testing searchAuthorsByName - Case insensitive scenario");

        // Given
        String searchName = "JANE";
        List<Author> expectedResults = Arrays.asList(testAuthor2);

        @SuppressWarnings("unchecked")
        PanacheQuery<Author> query = mock(PanacheQuery.class);
        when(query.list()).thenReturn(expectedResults);
        when(authorRepository.find(eq("LOWER(firstName) LIKE ?1 OR LOWER(lastName) LIKE ?1"), eq("%jane%")))
                .thenReturn(query);

        // When
        List<Author> result = authorService.searchAuthorsByName(searchName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Jane", result.get(0).getFirstName());

        verify(authorRepository, times(1)).find(eq("LOWER(firstName) LIKE ?1 OR LOWER(lastName) LIKE ?1"),
                eq("%jane%"));
        System.out.println("DEBUG: Case insensitive search successful - found: " + result.get(0).getFirstName());
    }

    @Test
    @DisplayName("Should verify timestamp updates on author creation")
    void testCreateAuthor_TimestampVerification() {
        System.out.println("DEBUG: Testing createAuthor - Timestamp verification");

        // Given
        Author newAuthor = new Author();
        newAuthor.setFirstName("Timestamp");
        newAuthor.setLastName("Test");

        LocalDateTime beforeCreation = LocalDateTime.now();

        // When
        Author result = authorService.createAuthor(newAuthor);

        LocalDateTime afterCreation = LocalDateTime.now();

        // Then
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getCreatedAt().isAfter(beforeCreation.minusSeconds(1)));
        assertTrue(result.getCreatedAt().isBefore(afterCreation.plusSeconds(1)));
        assertTrue(result.getUpdatedAt().isAfter(beforeCreation.minusSeconds(1)));
        assertTrue(result.getUpdatedAt().isBefore(afterCreation.plusSeconds(1)));

        System.out.println("DEBUG: Timestamp verification successful - created: " + result.getCreatedAt() +
                ", updated: " + result.getUpdatedAt());
    }
}
