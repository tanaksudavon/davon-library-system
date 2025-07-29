package org.acme.service;

import org.acme.model.Author;
import org.acme.repository.AuthorRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class AuthorService {

    @Inject
    private AuthorRepository authorRepository;

    public List<Author> getAllAuthors() {
        System.out.println("DEBUG: AuthorService.getAllAuthors() called");
        List<Author> authors = authorRepository.listAll();
        System.out.println("DEBUG: Found " + authors.size() + " authors in database");
        return authors;
    }

    public Author getAuthorById(Long id) {
        System.out.println("DEBUG: AuthorService.getAuthorById() called with id: " + id);
        return authorRepository.findById(id);
    }

    @Transactional
    public Author createAuthor(Author author) {
        System.out.println("DEBUG: AuthorService.createAuthor() called");
        author.setCreatedAt(LocalDateTime.now());
        author.setUpdatedAt(LocalDateTime.now());
        authorRepository.persist(author);
        System.out.println("DEBUG: Author created with id: " + author.getId());
        return author;
    }

    @Transactional
    public Author updateAuthor(Long id, Author updatedAuthor) {
        System.out.println("DEBUG: AuthorService.updateAuthor() called with id: " + id);
        Author existingAuthor = authorRepository.findById(id);
        if (existingAuthor != null) {
            existingAuthor.setFirstName(updatedAuthor.getFirstName());
            existingAuthor.setLastName(updatedAuthor.getLastName());
            existingAuthor.setBiography(updatedAuthor.getBiography());
            existingAuthor.setBirthDate(updatedAuthor.getBirthDate());
            existingAuthor.setNationality(updatedAuthor.getNationality());
            existingAuthor.setUpdatedAt(LocalDateTime.now());
            System.out.println("DEBUG: Author updated with id: " + id);
            return existingAuthor;
        }
        System.out.println("DEBUG: Author not found with id: " + id);
        return null;
    }

    @Transactional
    public boolean deleteAuthor(Long id) {
        System.out.println("DEBUG: AuthorService.deleteAuthor() called with id: " + id);
        Author author = authorRepository.findById(id);
        if (author != null) {
            authorRepository.delete(author);
            System.out.println("DEBUG: Author deleted with id: " + id);
            return true;
        }
        System.out.println("DEBUG: Author not found with id: " + id);
        return false;
    }

    public List<Author> searchAuthorsByName(String name) {
        System.out.println("DEBUG: AuthorService.searchAuthorsByName() called with name: " + name);
        if (name == null || name.trim().isEmpty()) {
            return getAllAuthors();
        }
        String searchPattern = "%" + name.toLowerCase() + "%";
        List<Author> authors = authorRepository.find(
                "LOWER(firstName) LIKE ?1 OR LOWER(lastName) LIKE ?1",
                searchPattern).list();
        System.out.println("DEBUG: Found " + authors.size() + " authors matching: " + name);
        return authors;
    }
}