package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.acme.model.Book;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class BookRepository implements PanacheRepository<Book> {
    public List<Book> listAllWithRelations() {
        return find("SELECT b FROM Book b JOIN FETCH b.author JOIN FETCH b.category ORDER BY b.id").list();
    }
}
