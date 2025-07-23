package org.acme.service;

import org.acme.model.Book;
import org.acme.repository.BookRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookService {

    @Inject
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        System.out.println("DEBUG: BookService.getAllBooks() called");
        List<Book> books = bookRepository.listAll();
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

    public void deleteBook(Long id) {
        System.out.println("DEBUG: BookService.deleteBook() called with ID: " + id);
        if (id == null) {
            System.out.println("DEBUG: ID is null, attempting to delete anyway");
        }

        bookRepository.deleteById(id);
        System.out.println("DEBUG: Delete operation completed for ID: " + id);
    }
}