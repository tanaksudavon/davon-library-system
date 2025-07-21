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

        testBook = new Book(1L, "The Great Gatsby", "978-0743273565",
                "A classic American novel", LocalDate.of(1925, 4, 10),
                "gatsby.jpg", BookStatus.AVAILABLE, 1L, 1L, now, now);

        Book book2 = new Book(2L, "To Kill a Mockingbird", "978-0061120084",
                "A novel about racial injustice", LocalDate.of(1960, 7, 11),
                "mockingbird.jpg", BookStatus.BORROWED, 2L, 2L, now, now);

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
        doNothing().when(bookRepository).persist(nullBook);

        // When
        Book result = bookService.createBook(nullBook);

        // Then
        assertNull(result);
        verify(bookRepository, times(1)).persist(nullBook);
    }

    @Test
    @DisplayName("Test createBook with complete book data")
    void testCreateBookWithCompleteData() {
        // Given
        Book completeBook = new Book();
        completeBook.setTitle("Complete Book");
        completeBook.setIsbn("978-0987654321");
        completeBook.setDescription("A complete book with all details");
        completeBook.setPublishDate(LocalDate.of(2023, 1, 1));
        completeBook.setCoverImage("complete.jpg");
        completeBook.setStatus(BookStatus.AVAILABLE);
        completeBook.setAuthorId(1L);
        completeBook.setCategoryId(1L);
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
}