package org.acme.service;

import org.acme.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Debug/Test class for SearchService
 * Used for debugging search functionality
 */
class SearchServiceTest {

    @Mock
    private BookService bookService;

    @Mock
    private UserService userService;

    @InjectMocks
    private SearchService searchService;

    private Book testBook1;
    private Book testBook2;
    private Book testBook3;
    private User testUser1;
    private User testUser2;
    private User testUser3;
    private Author testAuthor1;
    private Author testAuthor2;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Debug: Setup test data
        System.out.println("DEBUG: Setting up SearchServiceTest");

        // Create test authors
        testAuthor1 = new Author();
        testAuthor1.setId(1L);
        testAuthor1.setFirstName("John");
        testAuthor1.setLastName("Smith");

        testAuthor2 = new Author();
        testAuthor2.setId(2L);
        testAuthor2.setFirstName("Jane");
        testAuthor2.setLastName("Doe");

        // Create test category
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Fiction");

        // Create test books
        testBook1 = new Book();
        testBook1.setId(1L);
        testBook1.setTitle("Java Programming Guide");
        testBook1.setIsbn("978-0123456789");
        testBook1.setAuthor(testAuthor1);
        testBook1.setCategory(testCategory);
        testBook1.setStatus(BookStatus.AVAILABLE);

        testBook2 = new Book();
        testBook2.setId(2L);
        testBook2.setTitle("Advanced Java Concepts");
        testBook2.setIsbn("978-9876543210");
        testBook2.setAuthor(testAuthor2);
        testBook2.setCategory(testCategory);
        testBook2.setStatus(BookStatus.BORROWED);

        testBook3 = new Book();
        testBook3.setId(3L);
        testBook3.setTitle("Python for Beginners");
        testBook3.setIsbn("978-1111111111");
        testBook3.setAuthor(testAuthor1);
        testBook3.setCategory(testCategory);
        testBook3.setStatus(BookStatus.AVAILABLE);

        // Create test users
        testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setUsername("john.smith");
        testUser1.setFirstName("John");
        testUser1.setLastName("Smith");
        testUser1.setEmail("john.smith@example.com");
        testUser1.setCreatedAt(LocalDateTime.now());

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("jane.doe");
        testUser2.setFirstName("Jane");
        testUser2.setLastName("Doe");
        testUser2.setEmail("jane.doe@example.com");
        testUser2.setCreatedAt(LocalDateTime.now());

        testUser3 = new User();
        testUser3.setId(3L);
        testUser3.setUsername("admin");
        testUser3.setFirstName("Admin");
        testUser3.setLastName("User");
        testUser3.setEmail("admin@library.com");
        testUser3.setCreatedAt(LocalDateTime.now());

        System.out.println("DEBUG: Test data initialized - Books: " + testBook1.getTitle() + ", " +
                testBook2.getTitle() + ", " + testBook3.getTitle());
        System.out.println("DEBUG: Users: " + testUser1.getUsername() + ", " + testUser2.getUsername() +
                ", " + testUser3.getUsername());
    }

    @Test
    @DisplayName("Should search books by title successfully")
    void testSearchBooks_ByTitle_Success() {
        System.out.println("DEBUG: Testing searchBooks - By title success scenario");

        // Given
        String searchTerm = "Java";
        List<Book> allBooks = Arrays.asList(testBook1, testBook2, testBook3);
        when(bookService.getAllBooks()).thenReturn(allBooks);

        // When
        List<Book> result = searchService.searchBooks(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testBook1));
        assertTrue(result.contains(testBook2));
        assertFalse(result.contains(testBook3));

        verify(bookService, times(1)).getAllBooks();

        System.out.println("DEBUG: Found " + result.size() + " books matching title search: " + searchTerm);
        result.forEach(book -> System.out.println("DEBUG: - " + book.getTitle()));
    }

    @Test
    @DisplayName("Should search books by ISBN successfully")
    void testSearchBooks_ByISBN_Success() {
        System.out.println("DEBUG: Testing searchBooks - By ISBN success scenario");

        // Given
        String searchTerm = "978-0123456789";
        List<Book> allBooks = Arrays.asList(testBook1, testBook2, testBook3);
        when(bookService.getAllBooks()).thenReturn(allBooks);

        // When
        List<Book> result = searchService.searchBooks(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBook1, result.get(0));

        verify(bookService, times(1)).getAllBooks();

        System.out.println("DEBUG: Found " + result.size() + " book matching ISBN search: " + searchTerm);
        System.out.println("DEBUG: - " + result.get(0).getTitle() + " (ISBN: " + result.get(0).getIsbn() + ")");
    }

    @Test
    @DisplayName("Should search books case-insensitively")
    void testSearchBooks_CaseInsensitive() {
        System.out.println("DEBUG: Testing searchBooks - Case insensitive scenario");

        // Given
        String searchTerm = "JAVA"; // Uppercase
        List<Book> allBooks = Arrays.asList(testBook1, testBook2, testBook3);
        when(bookService.getAllBooks()).thenReturn(allBooks);

        // When
        List<Book> result = searchService.searchBooks(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testBook1));
        assertTrue(result.contains(testBook2));

        System.out.println("DEBUG: Case insensitive search successful - found " + result.size() + " books");
        System.out.println("DEBUG: Search term: " + searchTerm + " (uppercase)");
    }

    @Test
    @DisplayName("Should return empty list when no books match search")
    void testSearchBooks_NoMatches() {
        System.out.println("DEBUG: Testing searchBooks - No matches scenario");

        // Given
        String searchTerm = "NonExistentBook";
        List<Book> allBooks = Arrays.asList(testBook1, testBook2, testBook3);
        when(bookService.getAllBooks()).thenReturn(allBooks);

        // When
        List<Book> result = searchService.searchBooks(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());

        verify(bookService, times(1)).getAllBooks();

        System.out.println("DEBUG: No books found matching search: " + searchTerm);
    }

    @Test
    @DisplayName("Should handle empty book list")
    void testSearchBooks_EmptyBookList() {
        System.out.println("DEBUG: Testing searchBooks - Empty book list scenario");

        // Given
        String searchTerm = "Java";
        when(bookService.getAllBooks()).thenReturn(Collections.emptyList());

        // When
        List<Book> result = searchService.searchBooks(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());

        verify(bookService, times(1)).getAllBooks();

        System.out.println("DEBUG: Empty book list handled correctly");
    }

    @Test
    @DisplayName("Should handle partial title matches")
    void testSearchBooks_PartialMatches() {
        System.out.println("DEBUG: Testing searchBooks - Partial matches scenario");

        // Given
        String searchTerm = "Program"; // Partial match for "Programming"
        List<Book> allBooks = Arrays.asList(testBook1, testBook2, testBook3);
        when(bookService.getAllBooks()).thenReturn(allBooks);

        // When
        List<Book> result = searchService.searchBooks(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBook1, result.get(0));

        System.out.println("DEBUG: Partial match successful - found: " + result.get(0).getTitle());
        System.out.println(
                "DEBUG: Search term: '" + searchTerm + "' matched in title: '" + result.get(0).getTitle() + "'");
    }

    @Test
    @DisplayName("Should handle books with null ISBN")
    void testSearchBooks_NullISBN() {
        System.out.println("DEBUG: Testing searchBooks - Null ISBN scenario");

        // Given
        Book bookWithNullISBN = new Book();
        bookWithNullISBN.setId(4L);
        bookWithNullISBN.setTitle("Book Without ISBN");
        bookWithNullISBN.setIsbn(null);
        bookWithNullISBN.setAuthor(testAuthor1);
        bookWithNullISBN.setCategory(testCategory);

        String searchTerm = "Without";
        List<Book> allBooks = Arrays.asList(testBook1, testBook2, bookWithNullISBN);
        when(bookService.getAllBooks()).thenReturn(allBooks);

        // When
        List<Book> result = searchService.searchBooks(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookWithNullISBN, result.get(0));

        System.out.println("DEBUG: Book with null ISBN handled correctly - found: " + result.get(0).getTitle());
    }

    @Test
    @DisplayName("Should search users by username successfully")
    void testSearchUsers_ByUsername_Success() {
        System.out.println("DEBUG: Testing searchUsers - By username success scenario");

        // Given
        String searchTerm = "john";
        List<User> allUsers = Arrays.asList(testUser1, testUser2, testUser3);
        when(userService.getAllUsers()).thenReturn(allUsers);

        // When
        List<User> result = searchService.searchUsers(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser1, result.get(0));

        verify(userService, times(1)).getAllUsers();

        System.out.println("DEBUG: Found " + result.size() + " user matching username search: " + searchTerm);
        System.out.println("DEBUG: - " + result.get(0).getUsername() + " (" + result.get(0).getEmail() + ")");
    }

    @Test
    @DisplayName("Should search users by email successfully")
    void testSearchUsers_ByEmail_Success() {
        System.out.println("DEBUG: Testing searchUsers - By email success scenario");

        // Given
        String searchTerm = "library.com";
        List<User> allUsers = Arrays.asList(testUser1, testUser2, testUser3);
        when(userService.getAllUsers()).thenReturn(allUsers);

        // When
        List<User> result = searchService.searchUsers(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser3, result.get(0));

        verify(userService, times(1)).getAllUsers();

        System.out.println("DEBUG: Found " + result.size() + " user matching email search: " + searchTerm);
        System.out.println("DEBUG: - " + result.get(0).getUsername() + " (" + result.get(0).getEmail() + ")");
    }

    @Test
    @DisplayName("Should search users case-insensitively")
    void testSearchUsers_CaseInsensitive() {
        System.out.println("DEBUG: Testing searchUsers - Case insensitive scenario");

        // Given
        String searchTerm = "ADMIN"; // Uppercase
        List<User> allUsers = Arrays.asList(testUser1, testUser2, testUser3);
        when(userService.getAllUsers()).thenReturn(allUsers);

        // When
        List<User> result = searchService.searchUsers(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser3, result.get(0));

        System.out.println("DEBUG: Case insensitive user search successful");
        System.out.println("DEBUG: Search term: " + searchTerm + " (uppercase) found: " + result.get(0).getUsername());
    }

    @Test
    @DisplayName("Should return empty list when no users match search")
    void testSearchUsers_NoMatches() {
        System.out.println("DEBUG: Testing searchUsers - No matches scenario");

        // Given
        String searchTerm = "nonexistent";
        List<User> allUsers = Arrays.asList(testUser1, testUser2, testUser3);
        when(userService.getAllUsers()).thenReturn(allUsers);

        // When
        List<User> result = searchService.searchUsers(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());

        verify(userService, times(1)).getAllUsers();

        System.out.println("DEBUG: No users found matching search: " + searchTerm);
    }

    @Test
    @DisplayName("Should handle empty user list")
    void testSearchUsers_EmptyUserList() {
        System.out.println("DEBUG: Testing searchUsers - Empty user list scenario");

        // Given
        String searchTerm = "admin";
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        // When
        List<User> result = searchService.searchUsers(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(0, result.size());
        assertTrue(result.isEmpty());

        verify(userService, times(1)).getAllUsers();

        System.out.println("DEBUG: Empty user list handled correctly");
    }

    @Test
    @DisplayName("Should handle partial username matches")
    void testSearchUsers_PartialMatches() {
        System.out.println("DEBUG: Testing searchUsers - Partial matches scenario");

        // Given
        String searchTerm = "doe"; // Partial match for "jane.doe"
        List<User> allUsers = Arrays.asList(testUser1, testUser2, testUser3);
        when(userService.getAllUsers()).thenReturn(allUsers);

        // When
        List<User> result = searchService.searchUsers(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser2, result.get(0));

        System.out.println("DEBUG: Partial username match successful - found: " + result.get(0).getUsername());
        System.out.println(
                "DEBUG: Search term: '" + searchTerm + "' matched in username: '" + result.get(0).getUsername() + "'");
    }

    @Test
    @DisplayName("Should handle users with null email")
    void testSearchUsers_NullEmail() {
        System.out.println("DEBUG: Testing searchUsers - Null email scenario");

        // Given
        User userWithNullEmail = new User();
        userWithNullEmail.setId(4L);
        userWithNullEmail.setUsername("nullemail");
        userWithNullEmail.setFirstName("Null");
        userWithNullEmail.setLastName("Email");
        userWithNullEmail.setEmail(null);

        String searchTerm = "nullemail";
        List<User> allUsers = Arrays.asList(testUser1, testUser2, userWithNullEmail);
        when(userService.getAllUsers()).thenReturn(allUsers);

        // When
        List<User> result = searchService.searchUsers(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userWithNullEmail, result.get(0));

        System.out.println("DEBUG: User with null email handled correctly - found: " + result.get(0).getUsername());
    }

    @Test
    @DisplayName("Should find multiple matches across different fields")
    void testSearchUsers_MultipleMatches() {
        System.out.println("DEBUG: Testing searchUsers - Multiple matches scenario");

        // Given
        String searchTerm = "example.com"; // Should match multiple email addresses
        List<User> allUsers = Arrays.asList(testUser1, testUser2, testUser3);
        when(userService.getAllUsers()).thenReturn(allUsers);

        // When
        List<User> result = searchService.searchUsers(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // testUser1 and testUser2 have example.com emails
        assertTrue(result.contains(testUser1));
        assertTrue(result.contains(testUser2));
        assertFalse(result.contains(testUser3)); // has library.com email

        System.out.println("DEBUG: Multiple matches found - " + result.size() + " users");
        result.forEach(user -> System.out.println("DEBUG: - " + user.getUsername() + " (" + user.getEmail() + ")"));
    }

    @Test
    @DisplayName("Should handle search with special characters")
    void testSearchBooks_SpecialCharacters() {
        System.out.println("DEBUG: Testing searchBooks - Special characters scenario");

        // Given
        String searchTerm = "978-0123"; // Contains hyphens
        List<Book> allBooks = Arrays.asList(testBook1, testBook2, testBook3);
        when(bookService.getAllBooks()).thenReturn(allBooks);

        // When
        List<Book> result = searchService.searchBooks(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testBook1, result.get(0));

        System.out.println("DEBUG: Special characters in search handled correctly");
        System.out.println("DEBUG: Search term with hyphens: " + searchTerm);
    }

    @Test
    @DisplayName("Should verify search performance with large datasets")
    void testSearchPerformance() {
        System.out.println("DEBUG: Testing search performance");

        // Given
        String searchTerm = "test";
        List<Book> manyBooks = Collections.nCopies(1000, testBook1); // Simulate 1000 books
        List<User> manyUsers = Collections.nCopies(1000, testUser1); // Simulate 1000 users

        when(bookService.getAllBooks()).thenReturn(manyBooks);
        when(userService.getAllUsers()).thenReturn(manyUsers);

        // When
        long startTime = System.currentTimeMillis();
        List<Book> bookResults = searchService.searchBooks(searchTerm);
        long bookSearchTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        List<User> userResults = searchService.searchUsers(searchTerm);
        long userSearchTime = System.currentTimeMillis() - startTime;

        // Then
        assertNotNull(bookResults);
        assertNotNull(userResults);

        System.out.println("DEBUG: Performance test completed");
        System.out.println("DEBUG: Book search time for 1000 items: " + bookSearchTime + "ms");
        System.out.println("DEBUG: User search time for 1000 items: " + userSearchTime + "ms");
        System.out.println("DEBUG: Book results: " + bookResults.size() + ", User results: " + userResults.size());
    }
}