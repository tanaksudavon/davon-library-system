package org.acme.service;

import org.acme.model.User;
import org.acme.model.Loan;
import org.acme.model.User.UserType;
import org.acme.model.User.UserStatus;

import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing user operations.
 * Contains business logic for user-related operations in the library system.
 * 
 * @author Davon Library System
 * @version 1.0
 */
@ApplicationScoped
public class UserService {
    
    // In-memory storage for demonstration (replace with database in production)
    private final Map<Long, User> userRepository = new HashMap<>();
    private Long nextUserId = 1L;
    
    /**
     * Creates a new user in the system.
     * 
     * @param user the user to create
     * @return the created user with assigned ID
     */
    public User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        // Validate required fields
        validateUserFields(user);
        
        // Check for duplicate email
        if (findUserByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException("User with email " + user.getEmail() + " already exists");
        }
        
        // Set ID and save
        user.setId(nextUserId++);
        user.setStatus(UserStatus.ACTIVE);
        user.setMembershipDate(LocalDate.now());
        
        userRepository.put(user.getId(), user);
        
        return user;
    }
    
    /**
     * Updates an existing user.
     * 
     * @param user the user to update
     * @return the updated user
     */
    public User updateUser(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User and user ID cannot be null");
        }
        
        User existingUser = findUserById(user.getId());
        
        // Validate fields
        validateUserFields(user);
        
        // Check for duplicate email (excluding current user)
        Optional<User> userWithEmail = findUserByEmail(user.getEmail());
        if (userWithEmail.isPresent() && !userWithEmail.get().getId().equals(user.getId())) {
            throw new IllegalStateException("User with email " + user.getEmail() + " already exists");
        }
        
        // Update fields
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhoneNumber(user.getPhoneNumber());
        existingUser.setAddress(user.getAddress());
        existingUser.setDateOfBirth(user.getDateOfBirth());
        existingUser.setUserType(user.getUserType());
        
        return existingUser;
    }
    
    /**
     * Suspends a user account.
     * 
     * @param userId the ID of the user to suspend
     */
    public void suspendUser(Long userId) {
        User user = findUserById(userId);
        user.setStatus(UserStatus.SUSPENDED);
    }
    
    /**
     * Activates a user account.
     * 
     * @param userId the ID of the user to activate
     */
    public void activateUser(Long userId) {
        User user = findUserById(userId);
        user.setStatus(UserStatus.ACTIVE);
    }
    
    /**
     * Blocks a user account.
     * 
     * @param userId the ID of the user to block
     */
    public void blockUser(Long userId) {
        User user = findUserById(userId);
        user.setStatus(UserStatus.BLOCKED);
    }
    
    /**
     * Expires a user account.
     * 
     * @param userId the ID of the user to expire
     */
    public void expireUser(Long userId) {
        User user = findUserById(userId);
        user.setStatus(UserStatus.EXPIRED);
    }
    
    /**
     * Removes a user from the system.
     * 
     * @param userId the ID of the user to remove
     * @throws IllegalStateException if user has active loans
     */
    public void removeUser(Long userId) {
        User user = findUserById(userId);
        
        if (!user.getCurrentLoans().isEmpty()) {
            throw new IllegalStateException("Cannot remove user - has active loans");
        }
        
        userRepository.remove(userId);
    }
    
    /**
     * Finds a user by their ID.
     * 
     * @param userId the user ID
     * @return the user
     * @throws IllegalArgumentException if user not found
     */
    public User findUserById(Long userId) {
        User user = userRepository.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        return user;
    }
    
    /**
     * Finds a user by their email address.
     * 
     * @param email the email address
     * @return Optional containing the user if found
     */
    public Optional<User> findUserByEmail(String email) {
        return userRepository.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
    
    /**
     * Gets all users in the system.
     * 
     * @return list of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(userRepository.values());
    }
    
    /**
     * Gets users by status.
     * 
     * @param status the user status
     * @return list of users with the specified status
     */
    public List<User> getUsersByStatus(UserStatus status) {
        return userRepository.values().stream()
                .filter(user -> user.getStatus() == status)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets users by type.
     * 
     * @param userType the user type
     * @return list of users with the specified type
     */
    public List<User> getUsersByType(UserType userType) {
        return userRepository.values().stream()
                .filter(user -> user.getUserType() == userType)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets users with overdue books.
     * 
     * @return list of users with overdue books
     */
    public List<User> getUsersWithOverdueBooks() {
        return userRepository.values().stream()
                .filter(user -> user.getCurrentLoans().stream()
                        .anyMatch(Loan::isOverdue))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets users with books due soon.
     * 
     * @return list of users with books due soon
     */
    public List<User> getUsersWithBooksDueSoon() {
        return userRepository.values().stream()
                .filter(user -> user.getCurrentLoans().stream()
                        .anyMatch(Loan::isDueSoon))
                .collect(Collectors.toList());
    }
    
    /**
     * Searches users by name.
     * 
     * @param searchTerm the search term
     * @return list of users matching the search term
     */
    public List<User> searchUsersByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String lowerSearchTerm = searchTerm.toLowerCase();
        return userRepository.values().stream()
                .filter(user -> 
                    user.getFirstName().toLowerCase().contains(lowerSearchTerm) ||
                    user.getLastName().toLowerCase().contains(lowerSearchTerm) ||
                    (user.getFirstName() + " " + user.getLastName()).toLowerCase().contains(lowerSearchTerm))
                .collect(Collectors.toList());
    }
    
    /**
     * Gets user statistics.
     * 
     * @return map containing user statistics
     */
    public Map<String, Integer> getUserStats() {
        Map<String, Integer> stats = new HashMap<>();
        
        stats.put("total", userRepository.size());
        stats.put("active", (int) userRepository.values().stream()
                .filter(user -> user.getStatus() == UserStatus.ACTIVE).count());
        stats.put("suspended", (int) userRepository.values().stream()
                .filter(user -> user.getStatus() == UserStatus.SUSPENDED).count());
        stats.put("expired", (int) userRepository.values().stream()
                .filter(user -> user.getStatus() == UserStatus.EXPIRED).count());
        stats.put("blocked", (int) userRepository.values().stream()
                .filter(user -> user.getStatus() == UserStatus.BLOCKED).count());
        
        // By user type
        stats.put("students", (int) userRepository.values().stream()
                .filter(user -> user.getUserType() == UserType.STUDENT).count());
        stats.put("faculty", (int) userRepository.values().stream()
                .filter(user -> user.getUserType() == UserType.FACULTY).count());
        stats.put("staff", (int) userRepository.values().stream()
                .filter(user -> user.getUserType() == UserType.STAFF).count());
        stats.put("public", (int) userRepository.values().stream()
                .filter(user -> user.getUserType() == UserType.PUBLIC).count());
        
        return stats;
    }
    
    /**
     * Validates user fields.
     * 
     * @param user the user to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateUserFields(User user) {
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        if (!isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (user.getUserType() == null) {
            throw new IllegalArgumentException("User type is required");
        }
    }
    
    /**
     * Validates email format.
     * 
     * @param email the email to validate
     * @return true if email is valid
     */
    private boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
} 