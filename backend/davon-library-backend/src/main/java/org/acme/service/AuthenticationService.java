package org.acme.service;

import org.acme.model.User;
import org.acme.model.Member;
import org.acme.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class AuthenticationService {

    @Inject
    private UserRepository userRepository;

    public Member registerMember(String username, String password, String email, String firstName, String lastName,
            String phoneNumber) {
        Member newMember = new Member();
        newMember.setUsername(username);
        newMember.setPassword(password);
        newMember.setEmail(email);
        newMember.setFirstName(firstName);
        newMember.setLastName(lastName);
        newMember.setPhoneNumber(phoneNumber);
        newMember.setCreatedAt(LocalDateTime.now());
        newMember.setUpdatedAt(LocalDateTime.now());
        userRepository.persist(newMember);
        return newMember;
    }

    public Optional<User> login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent() && userOptional.get().getPassword().equals(password)) {
            return userOptional;
        }
        return Optional.empty();
    }
}