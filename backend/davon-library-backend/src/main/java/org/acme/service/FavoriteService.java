package org.acme.service;

import org.acme.model.Favorite;
import org.acme.model.Book;
import org.acme.model.User;
import org.acme.repository.FavoriteRepository;
import org.acme.repository.BookRepository;
import org.acme.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FavoriteService {

    @Inject
    private FavoriteRepository favoriteRepository;

    @Inject
    private BookRepository bookRepository;

    @Inject
    private UserRepository userRepository;

    /**
     * Get all favorites for a user
     */
    @Transactional
    public List<Favorite> getUserFavorites(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        // Force loading of related entities
        for (Favorite favorite : favorites) {
            if (favorite.getBook() != null) {
                Book book = favorite.getBook();
                book.getTitle();
                if (book.getAuthor() != null) {
                    book.getAuthor().getFirstName();
                    book.getAuthor().getLastName();
                }
                if (book.getCategory() != null) {
                    book.getCategory().getName();
                }
            }
        }

        return favorites;
    }

    /**
     * Add a book to user's favorites
     */
    @Transactional
    public Favorite addFavorite(Long userId, Long bookId) {
        // Check if already favorited
        Optional<Favorite> existing = favoriteRepository.findByUserIdAndBookId(userId, bookId);
        if (existing.isPresent()) {
            return existing.get();
        }

        // Validate user and book exist
        Optional<User> user = userRepository.findByIdOptional(userId);
        Optional<Book> book = bookRepository.findByIdOptional(bookId);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found with id: " + userId);
        }
        if (book.isEmpty()) {
            throw new IllegalArgumentException("Book not found with id: " + bookId);
        }

        // Create new favorite
        Favorite favorite = new Favorite();
        favorite.setUser(user.get());
        favorite.setBook(book.get());

        favoriteRepository.persist(favorite);
        return favorite;
    }

    /**
     * Remove a book from user's favorites
     */
    @Transactional
    public boolean removeFavorite(Long userId, Long bookId) {
        return favoriteRepository.deleteByUserIdAndBookId(userId, bookId);
    }

    /**
     * Check if a book is favorited by a user
     */
    public boolean isFavorited(Long userId, Long bookId) {
        return favoriteRepository.isFavorited(userId, bookId);
    }

    /**
     * Get favorite count for a book
     */
    public long getFavoriteCount(Long bookId) {
        return favoriteRepository.getFavoriteCountForBook(bookId);
    }

    /**
     * Toggle favorite status
     */
    @Transactional
    public boolean toggleFavorite(Long userId, Long bookId) {
        if (isFavorited(userId, bookId)) {
            removeFavorite(userId, bookId);
            return false; // Removed from favorites
        } else {
            addFavorite(userId, bookId);
            return true; // Added to favorites
        }
    }
}