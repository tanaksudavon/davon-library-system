package org.acme.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.acme.model.Book;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookRepository implements PanacheRepository<Book> {
}