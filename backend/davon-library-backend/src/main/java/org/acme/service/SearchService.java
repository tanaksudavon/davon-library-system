package org.acme.service;

import org.acme.model.Book;
import org.acme.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class SearchService {

    @Inject
    private BookService bookService;

    @Inject
    private UserService userService;

    public List<Book> searchBooks(String searchTerm) {
        String lowerCaseSearchTerm = searchTerm.toLowerCase();
        return bookService.getAllBooks().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerCaseSearchTerm) ||
                        (book.getIsbn() != null && book.getIsbn().toLowerCase().contains(lowerCaseSearchTerm)))
                .collect(Collectors.toList());
    }

    public List<User> searchUsers(String searchTerm) {
        String lowerCaseSearchTerm = searchTerm.toLowerCase();
        return userService.getAllUsers().stream()
                .filter(user -> user.getUsername().toLowerCase().contains(lowerCaseSearchTerm) ||
                        (user.getEmail() != null && user.getEmail().toLowerCase().contains(lowerCaseSearchTerm)))
                .collect(Collectors.toList());
    }
}