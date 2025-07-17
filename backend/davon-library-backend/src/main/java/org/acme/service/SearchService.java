package org.acme.service;

import org.acme.model.Book;
import org.acme.model.Author;
import org.acme.model.User;
import org.acme.model.Loan;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Service class for search functionality.
 * Provides comprehensive search capabilities across books, authors, and users.
 * 
 * @author Davon Library System
 * @version 1.0
 */
@ApplicationScoped
public class SearchService {
    
    @Inject
    private BookService bookService;
    
    @Inject
    private UserService userService;
    
    /**
     * Searches books by title.
     * 
     * @param title the title to search for
     * @return list of books matching the title
     */
    public List<Book> searchBooksByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerTitle = title.toLowerCase();
        return bookService.getAllBooks().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(lowerTitle))
                .collect(Collectors.toList());
    }
    
    /**
     * Searches books by author name.
     * 
     * @param authorName the author name to search for
     * @return list of books by the author
     */
    public List<Book> searchBooksByAuthor(String authorName) {
        if (authorName == null || authorName.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerAuthorName = authorName.toLowerCase();
        return bookService.getAllBooks().stream()
                .filter(book -> book.getAuthor() != null)
                .filter(book -> {
                    Author author = book.getAuthor();
                    String fullName = (author.getFirstName() + " " + author.getLastName()).toLowerCase();
                    return fullName.contains(lowerAuthorName) ||
                           author.getFirstName().toLowerCase().contains(lowerAuthorName) ||
                           author.getLastName().toLowerCase().contains(lowerAuthorName);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Searches books by ISBN.
     * 
     * @param isbn the ISBN to search for
     * @return list of books matching the ISBN
     */
    public List<Book> searchBooksByIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return bookService.findBookByIsbn(isbn)
                .map(book -> List.of(book))
                .orElse(new ArrayList<>());
    }
    
    /**
     * Searches books by genre.
     * 
     * @param genre the genre to search for
     * @return list of books in the genre
     */
    public List<Book> searchBooksByGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerGenre = genre.toLowerCase();
        return bookService.getAllBooks().stream()
                .filter(book -> book.getGenre() != null)
                .filter(book -> book.getGenre().toLowerCase().contains(lowerGenre))
                .collect(Collectors.toList());
    }
    
    /**
     * Searches books by publisher.
     * 
     * @param publisher the publisher to search for
     * @return list of books by the publisher
     */
    public List<Book> searchBooksByPublisher(String publisher) {
        if (publisher == null || publisher.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerPublisher = publisher.toLowerCase();
        return bookService.getAllBooks().stream()
                .filter(book -> book.getPublisher() != null)
                .filter(book -> book.getPublisher().toLowerCase().contains(lowerPublisher))
                .collect(Collectors.toList());
    }
    
    /**
     * Performs a comprehensive search across multiple book fields.
     * 
     * @param searchTerm the search term
     * @return list of books matching the search term
     */
    public List<Book> searchBooks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerSearchTerm = searchTerm.toLowerCase();
        return bookService.getAllBooks().stream()
                .filter(book -> matchesBookSearchTerm(book, lowerSearchTerm))
                .collect(Collectors.toList());
    }
    
    /**
     * Searches for available books only.
     * 
     * @param searchTerm the search term
     * @return list of available books matching the search term
     */
    public List<Book> searchAvailableBooks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return bookService.getAvailableBooks();
        }
        
        String lowerSearchTerm = searchTerm.toLowerCase();
        return bookService.getAvailableBooks().stream()
                .filter(book -> matchesBookSearchTerm(book, lowerSearchTerm))
                .collect(Collectors.toList());
    }
    
    /**
     * Searches users by name.
     * 
     * @param name the name to search for
     * @return list of users matching the name
     */
    public List<User> searchUsersByName(String name) {
        return userService.searchUsersByName(name);
    }
    
    /**
     * Searches users by email.
     * 
     * @param email the email to search for
     * @return list of users matching the email
     */
    public List<User> searchUsersByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerEmail = email.toLowerCase();
        return userService.getAllUsers().stream()
                .filter(user -> user.getEmail().toLowerCase().contains(lowerEmail))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets search suggestions for book titles.
     * 
     * @param partial the partial title
     * @param maxResults maximum number of suggestions
     * @return list of title suggestions
     */
    public List<String> getBookTitleSuggestions(String partial, int maxResults) {
        if (partial == null || partial.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerPartial = partial.toLowerCase();
        return bookService.getAllBooks().stream()
                .map(Book::getTitle)
                .filter(title -> title.toLowerCase().startsWith(lowerPartial))
                .distinct()
                .limit(maxResults)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets search suggestions for author names.
     * 
     * @param partial the partial author name
     * @param maxResults maximum number of suggestions
     * @return list of author name suggestions
     */
    public List<String> getAuthorNameSuggestions(String partial, int maxResults) {
        if (partial == null || partial.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerPartial = partial.toLowerCase();
        return bookService.getAllBooks().stream()
                .filter(book -> book.getAuthor() != null)
                .map(book -> book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName())
                .filter(name -> name.toLowerCase().startsWith(lowerPartial))
                .distinct()
                .limit(maxResults)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all available genres.
     * 
     * @return list of all genres
     */
    public List<String> getAllGenres() {
        return bookService.getAllBooks().stream()
                .map(Book::getGenre)
                .filter(genre -> genre != null && !genre.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Gets all publishers.
     * 
     * @return list of all publishers
     */
    public List<String> getAllPublishers() {
        return bookService.getAllBooks().stream()
                .map(Book::getPublisher)
                .filter(publisher -> publisher != null && !publisher.trim().isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * Performs advanced search with multiple criteria.
     * 
     * @param title the title to search for
     * @param author the author to search for
     * @param genre the genre to search for
     * @param publisher the publisher to search for
     * @param availableOnly whether to search only available books
     * @return list of books matching the criteria
     */
    public List<Book> advancedSearch(String title, String author, String genre, 
                                   String publisher, boolean availableOnly) {
        List<Book> books = availableOnly ? bookService.getAvailableBooks() : bookService.getAllBooks();
        
        return books.stream()
                .filter(book -> {
                    // Title filter
                    if (title != null && !title.trim().isEmpty()) {
                        if (!book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                            return false;
                        }
                    }
                    
                    // Author filter
                    if (author != null && !author.trim().isEmpty()) {
                        if (book.getAuthor() == null) {
                            return false;
                        }
                        String fullName = (book.getAuthor().getFirstName() + " " + 
                                         book.getAuthor().getLastName()).toLowerCase();
                        if (!fullName.contains(author.toLowerCase())) {
                            return false;
                        }
                    }
                    
                    // Genre filter
                    if (genre != null && !genre.trim().isEmpty()) {
                        if (book.getGenre() == null || 
                            !book.getGenre().toLowerCase().contains(genre.toLowerCase())) {
                            return false;
                        }
                    }
                    
                    // Publisher filter
                    if (publisher != null && !publisher.trim().isEmpty()) {
                        if (book.getPublisher() == null || 
                            !book.getPublisher().toLowerCase().contains(publisher.toLowerCase())) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Checks if a book matches the search term across multiple fields.
     * 
     * @param book the book to check
     * @param searchTerm the search term (already lowercased)
     * @return true if the book matches the search term
     */
    private boolean matchesBookSearchTerm(Book book, String searchTerm) {
        // Check title
        if (book.getTitle() != null && book.getTitle().toLowerCase().contains(searchTerm)) {
            return true;
        }
        
        // Check author
        if (book.getAuthor() != null) {
            String authorName = (book.getAuthor().getFirstName() + " " + 
                               book.getAuthor().getLastName()).toLowerCase();
            if (authorName.contains(searchTerm)) {
                return true;
            }
        }
        
        // Check ISBN
        if (book.getIsbn() != null && book.getIsbn().toLowerCase().contains(searchTerm)) {
            return true;
        }
        
        // Check genre
        if (book.getGenre() != null && book.getGenre().toLowerCase().contains(searchTerm)) {
            return true;
        }
        
        // Check publisher
        if (book.getPublisher() != null && book.getPublisher().toLowerCase().contains(searchTerm)) {
            return true;
        }
        
        // Check description
        if (book.getDescription() != null && book.getDescription().toLowerCase().contains(searchTerm)) {
            return true;
        }
        
        return false;
    }
} 