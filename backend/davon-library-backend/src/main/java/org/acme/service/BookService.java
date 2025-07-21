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
        return bookRepository.listAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findByIdOptional(id);
    }

    public Book createBook(Book book) {
        bookRepository.persist(book);
        return book;
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}