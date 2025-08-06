package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.acme.model.Favorite;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FavoriteRepository implements PanacheRepository<Favorite> {

    /**
     * Find all favorites for a specific user
     */
    public List<Favorite> findByUserId(Long userId) {
        return list("user.id", userId);
    }

    /**
     * Find a specific favorite by user and book
     */
    public Optional<Favorite> findByUserIdAndBookId(Long userId, Long bookId) {
        return find("user.id = ?1 and book.id = ?2", userId, bookId).firstResultOptional();
    }

    /**
     * Check if a book is favorited by a user
     */
    public boolean isFavorited(Long userId, Long bookId) {
        return findByUserIdAndBookId(userId, bookId).isPresent();
    }

    /**
     * Delete favorite by user and book
     */
    public boolean deleteByUserIdAndBookId(Long userId, Long bookId) {
        return delete("user.id = ?1 and book.id = ?2", userId, bookId) > 0;
    }

    /**
     * Get favorite count for a book
     */
    public long getFavoriteCountForBook(Long bookId) {
        return count("book.id", bookId);
    }
}