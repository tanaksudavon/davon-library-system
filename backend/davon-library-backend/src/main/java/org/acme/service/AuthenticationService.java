package org.acme.service;

import org.acme.model.User;
import org.acme.model.Member;
import org.acme.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class AuthenticationService {

    @Inject
    private UserRepository userRepository;

    @Transactional
    public Member registerMember(String username, String password, String email, String firstName, String lastName,
            String phoneNumber) {

        // Validate required fields
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }

        // Check if username already exists
        Optional<User> existingUser = userRepository.find("username", username).firstResultOptional();
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        Optional<User> existingEmail = userRepository.find("email", email).firstResultOptional();
        if (existingEmail.isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        Member newMember = new Member();
        newMember.setUsername(username.trim());
        newMember.setPassword(password);
        newMember.setEmail(email.trim());
        newMember.setFirstName(firstName.trim());
        newMember.setLastName(lastName.trim());
        newMember.setPhoneNumber(phoneNumber != null ? phoneNumber.trim() : null);
        newMember.setCreatedAt(LocalDateTime.now());
        newMember.setUpdatedAt(LocalDateTime.now());

        try {
            userRepository.persist(newMember);
            return newMember;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user: " + e.getMessage(), e);
        }
    }

    public Optional<User> login(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }

        Optional<User> userOptional = userRepository.find("username", username).firstResultOptional();
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            return userOptional;
        }
        return Optional.empty();
    }
}