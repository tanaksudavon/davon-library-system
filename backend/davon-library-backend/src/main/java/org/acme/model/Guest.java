package org.acme.model;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("GUEST")
public class Guest extends User {

    public Guest() {
        super();
        this.setRole(UserRole.GUEST);
    }

    public Guest(Long id, String username, String password, String email, String firstName, String lastName,
            String phoneNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, username, password, email, firstName, lastName, phoneNumber, UserRole.GUEST, createdAt, updatedAt);
    }
}