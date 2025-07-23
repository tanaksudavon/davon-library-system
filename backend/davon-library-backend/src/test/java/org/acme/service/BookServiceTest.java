package org.acme.service;

import org.acme.model.Book;
import org.acme.model.BookStatus;
import org.acme.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.acme.model.Author;
import org.acme.model.Category;

/**
 * Unit tests for BookService class
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BookService Tests")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private List<Book> testBooks;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        // Create test Author and Category objects
        Author testAuthor = new Author();
        testAuthor.setId(1L);
        testAuthor.setFirstName("F. Scott");
        testAuthor.setLastName("Fitzgerald");

        Category testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Fiction");

        Author testAuthor2 = new Author();
        testAuthor2.setId(2L);
        testAuthor2.setFirstName("Harper");
        testAuthor2.setLastName("Lee");

        Category testCategory2 = new Category();
        testCategory2.setId(2L);
        testCategory2.setName("Literature");

        testBook = new Book(1L, "The Great Gatsby", "978-0743273565",
                "A classic American novel", LocalDate.of(1925, 4, 10),
                "gatsby.jpg", BookStatus.AVAILABLE, testAuthor, testCategory, now, now);

        Book book2 = new Book(2L, "To Kill a Mockingbird", "978-0061120084",
                "A novel about racial injustice", LocalDate.of(1960, 7, 11),
                "mockingbird.jpg", BookStatus.BORROWED, testAuthor2, testCategory2, now, now);

        testBooks = Arrays.asList(testBook, book2);
    }

    @Test
    @DisplayName("Test getAllBooks returns all books")
    void testGetAllBooks() {
        // Given
        when(bookRepository.listAll()).thenReturn(testBooks);

        // When
        List<Book> result = bookService.getAllBooks();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testBooks, result);
        verify(bookRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Test getAllBooks returns empty list when no books exist")
    void testGetAllBooksWhenEmpty() {
        // Given
        when(bookRepository.listAll()).thenReturn(Collections.emptyList());

        // When
        List<Book> result = bookService.getAllBooks();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Test getBookById returns book when found")
    void testGetBookByIdFound() {
        // Given
        Long bookId = 1L;
        when(bookRepository.findByIdOptional(bookId)).thenReturn(Optional.of(testBook));

        // When
        Optional<Book> result = bookService.getBookById(bookId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testBook, result.get());
        verify(bookRepository, times(1)).findByIdOptional(bookId);
    }

    @Test
    @DisplayName("Test getBookById returns empty when not found")
    void testGetBookByIdNotFound() {
        // Given
        Long bookId = 999L;
        when(bookRepository.findByIdOptional(bookId)).thenReturn(Optional.empty());

        // When
        Optional<Book> result = bookService.getBookById(bookId);

        // Then
        assertFalse(result.isPresent());
        verify(bookRepository, times(1)).findByIdOptional(bookId);
    }

    @Test
    @DisplayName("Test getBookById with null id")
    void testGetBookByIdWithNullId() {
        // Given
        when(bookRepository.findByIdOptional(null)).thenReturn(Optional.empty());

        // When
        Optional<Book> result = bookService.getBookById(null);

        // Then
        assertFalse(result.isPresent());
        verify(bookRepository, times(1)).findByIdOptional(null);
    }

    @Test
    @DisplayName("Test createBook successfully creates and returns book")
    void testCreateBookSuccess() {
        // Given
        Book newBook = new Book();
        newBook.setTitle("New Book");
        newBook.setIsbn("978-0123456789");
        newBook.setStatus(BookStatus.AVAILABLE);

        doNothing().when(bookRepository).persist(newBook);

        // When
        Book result = bookService.createBook(newBook);

        // Then
        assertNotNull(result);
        assertEquals(newBook, result);
        assertEquals("New Book", result.getTitle());
        assertEquals("978-0123456789", result.getIsbn());
        assertEquals(BookStatus.AVAILABLE, result.getStatus());
        verify(bookRepository, times(1)).persist(newBook);
    }

    @Test
    @DisplayName("Test createBook with null book")
    void testCreateBookWithNullBook() {
        // Given
        Book nullBook = null;

        // When
        Book result = bookService.createBook(nullBook);

        // Then
        assertNull(result);
        verify(bookRepository, times(0)).persist(any(Book.class));
    }

    @Test
    @DisplayName("Test createBook with complete book data")
    void testCreateBookWithCompleteData() {
        // Given
        Author completeAuthor = new Author();
        completeAuthor.setId(1L);
        completeAuthor.setFirstName("Complete");
        completeAuthor.setLastName("Author");

        Category completeCategory = new Category();
        completeCategory.setId(1L);
        completeCategory.setName("Complete Category");

        Book completeBook = new Book();
        completeBook.setTitle("Complete Book");
        completeBook.setIsbn("978-0987654321");
        completeBook.setDescription("A complete book with all details");
        completeBook.setPublishDate(LocalDate.of(2023, 1, 1));
        completeBook.setCoverImage("complete.jpg");
        completeBook.setStatus(BookStatus.AVAILABLE);
        completeBook.setAuthor(completeAuthor);
        completeBook.setCategory(completeCategory);
        completeBook.setCreatedAt(LocalDateTime.now());
        completeBook.setUpdatedAt(LocalDateTime.now());

        doNothing().when(bookRepository).persist(completeBook);

        // When
        Book result = bookService.createBook(completeBook);

        // Then
        assertNotNull(result);
        assertEquals("Complete Book", result.getTitle());
        assertEquals("978-0987654321", result.getIsbn());
        assertEquals("A complete book with all details", result.getDescription());
        assertEquals(BookStatus.AVAILABLE, result.getStatus());
        assertNotNull(result.getPublishDate());
        assertNotNull(result.getCreatedAt());
        verify(bookRepository, times(1)).persist(completeBook);
    }

    @Test
    @DisplayName("Test deleteBook successfully deletes existing book")
    void testDeleteBookSuccess() {
        // Given
        Long bookId = 1L;
        when(bookRepository.deleteById(bookId)).thenReturn(true);

        // When
        assertDoesNotThrow(() -> bookService.deleteBook(bookId));

        // Then
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    @DisplayName("Test deleteBook with non-existent book")
    void testDeleteBookNotFound() {
        // Given
        Long bookId = 999L;
        when(bookRepository.deleteById(bookId)).thenReturn(false);

        // When
        assertDoesNotThrow(() -> bookService.deleteBook(bookId));

        // Then
        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    @DisplayName("Test deleteBook with null id")
    void testDeleteBookWithNullId() {
        // Given
        when(bookRepository.deleteById(null)).thenReturn(false);

        // When
        assertDoesNotThrow(() -> bookService.deleteBook(null));

        // Then
        verify(bookRepository, times(1)).deleteById(null);
    }

    @Test
    @DisplayName("Test repository interaction count")
    void testRepositoryInteractionCount() {
        // Given
        when(bookRepository.listAll()).thenReturn(testBooks);
        when(bookRepository.findByIdOptional(1L)).thenReturn(Optional.of(testBook));
        doNothing().when(bookRepository).persist(any(Book.class));

        // When
        bookService.getAllBooks();
        bookService.getBookById(1L);
        bookService.createBook(new Book());
        bookService.deleteBook(1L);

        // Then
        verify(bookRepository, times(1)).listAll();
        verify(bookRepository, times(1)).findByIdOptional(1L);
        verify(bookRepository, times(1)).persist(any(Book.class));
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test service behavior with repository exceptions")
    void testServiceWithRepositoryExceptions() {
        // Given
        when(bookRepository.listAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> bookService.getAllBooks());
        verify(bookRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Test createBook persistence behavior")
    void testCreateBookPersistenceBehavior() {
        // Given
        Book book = new Book();
        book.setTitle("Test Book");

        doAnswer(invocation -> {
            Book persistedBook = invocation.getArgument(0);
            persistedBook.setId(1L); // Simulate ID assignment
            return null;
        }).when(bookRepository).persist(book);

        // When
        Book result = bookService.createBook(book);

        // Then
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        assertEquals(1L, result.getId()); // Verify ID was set
        verify(bookRepository, times(1)).persist(book);
    }

    @Test
    @DisplayName("Test multiple book retrieval scenarios")
    void testMultipleBookRetrievalScenarios() {
        // Test scenario 1: Get existing book
        when(bookRepository.findByIdOptional(1L)).thenReturn(Optional.of(testBook));
        Optional<Book> result1 = bookService.getBookById(1L);
        assertTrue(result1.isPresent());
        assertEquals("The Great Gatsby", result1.get().getTitle());

        // Test scenario 2: Get non-existing book
        when(bookRepository.findByIdOptional(99L)).thenReturn(Optional.empty());
        Optional<Book> result2 = bookService.getBookById(99L);
        assertFalse(result2.isPresent());

        // Test scenario 3: Get another existing book
        Book anotherBook = new Book();
        anotherBook.setId(2L);
        anotherBook.setTitle("Another Book");
        when(bookRepository.findByIdOptional(2L)).thenReturn(Optional.of(anotherBook));
        Optional<Book> result3 = bookService.getBookById(2L);
        assertTrue(result3.isPresent());
        assertEquals("Another Book", result3.get().getTitle());

        // Verify all interactions
        verify(bookRepository, times(1)).findByIdOptional(1L);
        verify(bookRepository, times(1)).findByIdOptional(99L);
        verify(bookRepository, times(1)).findByIdOptional(2L);
    }

    @Test
    @DisplayName("Debug BookService step-by-step")
    void debugBookServiceStepByStep() {
        // BREAKPOINT 1: Set a breakpoint here to start debugging
        System.out.println("=== Starting BookService Debug ===");

        // Create test data
        Author debugAuthor = new Author();
        debugAuthor.setId(999L);
        debugAuthor.setFirstName("Debug");
        debugAuthor.setLastName("Author");

        Category debugCategory = new Category();
        debugCategory.setId(999L);
        debugCategory.setName("Debug Category");

        Book debugBook = new Book();
        debugBook.setTitle("Debug Book");
        debugBook.setIsbn("978-DEBUG-123");
        debugBook.setDescription("A book for debugging purposes");
        debugBook.setStatus(BookStatus.AVAILABLE);
        debugBook.setAuthor(debugAuthor);
        debugBook.setCategory(debugCategory);

        // BREAKPOINT 2: Watch the debugBook object
        System.out.println("Created debug book: " + debugBook.getTitle());

        // Mock repository behavior
        when(bookRepository.listAll()).thenReturn(Arrays.asList(debugBook));
        when(bookRepository.findByIdOptional(999L)).thenReturn(Optional.of(debugBook));
        doNothing().when(bookRepository).persist(debugBook);

        // BREAKPOINT 3: Step INTO getAllBooks() method
        List<Book> allBooks = bookService.getAllBooks();
        System.out.println("Retrieved " + allBooks.size() + " books");

        // BREAKPOINT 4: Step INTO getBookById() method
        Optional<Book> foundBook = bookService.getBookById(999L);
        System.out.println("Found book: " + foundBook.isPresent());

        // BREAKPOINT 5: Step INTO createBook() method
        Book createdBook = bookService.createBook(debugBook);
        System.out.println("Created book: " + (createdBook != null ? createdBook.getTitle() : "null"));

        // BREAKPOINT 6: Step INTO deleteBook() method
        bookService.deleteBook(999L);
        System.out.println("Deleted book with ID: 999");

        // Verify all operations worked
        assertNotNull(allBooks);
        assertEquals(1, allBooks.size());
        assertTrue(foundBook.isPresent());
        assertNotNull(createdBook);

        System.out.println("=== BookService Debug Complete ===");
    }
}