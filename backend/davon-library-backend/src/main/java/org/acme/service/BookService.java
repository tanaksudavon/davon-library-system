package org.acme.service;

import org.acme.model.Book;
import org.acme.repository.BookRepository;
import org.acme.model.BookStatus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@ApplicationScoped
public class BookService {

    @Inject
    private BookRepository bookRepository;

    @Inject
    private EntityManager entityManager;

    public List<Book> getAllBooks() {
        System.out.println("DEBUG: BookService.getAllBooks() called");
        List<Book> books = bookRepository.listAllWithRelations();
        System.out.println("DEBUG: Found " + books.size() + " books in database");
        return books;
    }

    public Optional<Book> getBookById(Long id) {
        System.out.println("DEBUG: BookService.getBookById() called with ID: " + id);
        if (id == null) {
            System.out.println("DEBUG: ID is null, returning empty Optional");
            return bookRepository.findByIdOptional(id);
        }

        Optional<Book> book = bookRepository.findByIdOptional(id);
        if (book.isPresent()) {
            System.out.println("DEBUG: Found book: " + book.get().getTitle());
        } else {
            System.out.println("DEBUG: No book found with ID: " + id);
        }
        return book;
    }

    @Transactional
    public Book createBook(Book book) {
        System.out.println("DEBUG: BookService.createBook() called");
        if (book == null) {
            System.out.println("DEBUG: Book is null, returning null");
            return null;
        }

        System.out.println("DEBUG: Creating book: " + book.getTitle());
        if (book.getAuthor() != null) {
            System.out.println(
                    "DEBUG: Book author: " + book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName());
        }
        if (book.getCategory() != null) {
            System.out.println("DEBUG: Book category: " + book.getCategory().getName());
        }

        bookRepository.persist(book);
        System.out.println("DEBUG: Book persisted successfully");
        return book;
    }

    @Transactional
    public Book updateBook(Book book) {
        System.out.println("DEBUG: BookService.updateBook() called");
        if (book == null) {
            System.out.println("DEBUG: Book is null, returning null");
            return null;
        }

        System.out.println("DEBUG: Updating book: " + book.getTitle());
        if (book.getAuthor() != null) {
            System.out.println(
                    "DEBUG: Book author: " + book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName());
        }
        if (book.getCategory() != null) {
            System.out.println("DEBUG: Book category: " + book.getCategory().getName());
        }

        // Get the existing book to preserve system fields
        Optional<Book> existingBookOpt = bookRepository.findByIdOptional(book.getId());
        if (existingBookOpt.isPresent()) {
            Book existingBook = existingBookOpt.get();

            // Preserve system fields that shouldn't be overwritten
            // Note: Allow status updates from the UI
            book.setCreatedAt(existingBook.getCreatedAt()); // Preserve creation date
            // updatedAt will be set automatically by @PreUpdate if configured
        }

        // Set the updated timestamp
        book.setUpdatedAt(LocalDateTime.now());

        // Use EntityManager merge for updates to handle detached entities
        Book updatedBook = entityManager.merge(book);

        // Force loading of related entities to avoid lazy loading issues during
        // serialization
        if (updatedBook.getAuthor() != null) {
            updatedBook.getAuthor().getFirstName(); // Force load author
            updatedBook.getAuthor().getLastName();
        }
        if (updatedBook.getCategory() != null) {
            updatedBook.getCategory().getName(); // Force load category
        }

        System.out.println("DEBUG: Book updated successfully");
        return updatedBook;
    }

    @Transactional
    public Book updateBookStatus(Long bookId, String status) {
        System.out
                .println("DEBUG: BookService.updateBookStatus() called for book " + bookId + " with status " + status);

        Optional<Book> bookOpt = bookRepository.findByIdOptional(bookId);
        if (bookOpt.isPresent()) {
            Book book = bookOpt.get();

            // Convert string status to BookStatus enum
            try {
                BookStatus bookStatus = BookStatus.valueOf(status.toUpperCase());
                book.setStatus(bookStatus);
            } catch (IllegalArgumentException e) {
                System.out.println("DEBUG: Invalid status value: " + status);
                return null;
            }

            // Force loading of related entities
            if (book.getAuthor() != null) {
                book.getAuthor().getFirstName();
                book.getAuthor().getLastName();
            }
            if (book.getCategory() != null) {
                book.getCategory().getName();
            }

            System.out.println("DEBUG: Book status updated successfully");
            return book;
        }

        System.out.println("DEBUG: Book not found with ID: " + bookId);
        return null;
    }

    @Transactional
    public void deleteBook(Long id) {
        System.out.println("DEBUG: BookService.deleteBook() called with ID: " + id);
        if (id == null) {
            System.out.println("DEBUG: ID is null, attempting to delete anyway");
        }

        bookRepository.deleteById(id);
        System.out.println("DEBUG: Delete operation completed for ID: " + id);
    }
}