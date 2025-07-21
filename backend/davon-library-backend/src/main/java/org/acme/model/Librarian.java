package org.acme.model;

import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Librarian extends User {

    public Librarian() {
        super();
        this.setRole(UserRole.LIBRARIAN);
    }

    public Librarian(Long id, String username, String password, String email, String firstName, String lastName,
            String phoneNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, username, password, email, firstName, lastName, phoneNumber, UserRole.LIBRARIAN, createdAt,
                updatedAt);
    }
}