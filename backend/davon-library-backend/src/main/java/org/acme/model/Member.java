package org.acme.model;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("MEMBER")
public class Member extends User {

    public Member() {
        super();
        this.setRole(UserRole.MEMBER);
    }

    public Member(Long id, String username, String password, String email, String firstName, String lastName,
            String phoneNumber, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, username, password, email, firstName, lastName, phoneNumber, UserRole.MEMBER, createdAt, updatedAt);
    }
}