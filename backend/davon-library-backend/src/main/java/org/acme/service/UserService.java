package org.acme.service;

import org.acme.model.User;
import org.acme.repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserService {

    @Inject
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.listAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findByIdOptional(id);
    }

    public User createUser(User user) {
        userRepository.persist(user);
        return user;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}