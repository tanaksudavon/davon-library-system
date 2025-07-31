package org.acme.service;

import org.acme.model.User;
import org.acme.repository.UserRepository;

import jakarta.transaction.Transactional;
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

    @Transactional
    public User createUser(User user) {
        if (user == null) {
            System.out.println("Kullanıcı oluşturuluyor: null");
            return null;
        }
        System.out.println("Kullanıcı oluşturuluyor: " + user.getUsername());
        userRepository.persist(user);
        return user;
    }

    @Transactional
    public void deleteUser(Long id) {
        System.out.println("DEBUG: UserService.deleteUser() called with ID: " + id);
        if (id == null) {
            System.out.println("DEBUG: ID is null, attempting to delete anyway");
        }

        userRepository.deleteById(id);
        System.out.println("DEBUG: User delete operation completed for ID: " + id);
    }
}