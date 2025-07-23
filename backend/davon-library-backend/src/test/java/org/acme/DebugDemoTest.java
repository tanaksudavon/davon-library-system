package org.acme;

import org.acme.model.User;
import org.acme.model.UserRole;
import org.acme.service.UserService;
import org.acme.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Debug Demo Test - Perfect for step-by-step debugging
 * 
 * HOW TO DEBUG THIS TEST:
 * 1. Set breakpoints on lines marked with // DEBUG BREAKPOINT
 * 2. Right-click and select "Debug Test"
 * 3. Use F8 (Step Over), F7 (Step Into), Shift+F8 (Step Out)
 * 4. Watch variables in the Variables panel
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Debug Demo Test")
class DebugDemoTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Debug Step-by-Step User Creation")
    void debugUserCreation() {
        // DEBUG BREAKPOINT: Start here - examine initial state
        String originalUsername = "john_doe";
        String originalEmail = "john@example.com";

        // DEBUG BREAKPOINT: Watch object creation
        User newUser = new User();

        // DEBUG BREAKPOINT: Watch each property being set
        newUser.setUsername(originalUsername); // Watch: originalUsername -> newUser.username
        newUser.setEmail(originalEmail); // Watch: originalEmail -> newUser.email
        newUser.setRole(UserRole.MEMBER); // Watch: enum assignment

        LocalDateTime currentTime = LocalDateTime.now();
        newUser.setCreatedAt(currentTime); // Watch: time assignment

        // DEBUG BREAKPOINT: Setup mock behavior
        doNothing().when(userRepository).persist(newUser);

        // DEBUG BREAKPOINT: Step INTO this method to see null check
        User result = userService.createUser(newUser);

        // DEBUG BREAKPOINT: Examine result
        assertNotNull(result);
        assertEquals(originalUsername, result.getUsername()); // Watch: comparison
        assertEquals(originalEmail, result.getEmail()); // Watch: comparison
        assertEquals(UserRole.MEMBER, result.getRole()); // Watch: enum comparison

        // Verify the repository was called
        verify(userRepository, times(1)).persist(newUser);
    }

    @Test
    @DisplayName("Debug Null Handling Step-by-Step")
    void debugNullHandling() {
        // DEBUG BREAKPOINT: Start with null
        User nullUser = null; // Watch: nullUser = null

        // DEBUG BREAKPOINT: Step INTO this method to see null check logic
        User result = userService.createUser(nullUser);

        // DEBUG BREAKPOINT: Check the result
        assertNull(result); // Watch: result should be null

        // Verify repository was NOT called
        verify(userRepository, times(0)).persist(any(User.class));
    }

    @Test
    @DisplayName("Debug Loop and Conditional Logic")
    void debugLoopAndConditions() {
        // DEBUG BREAKPOINT: Initialize counter
        int userCount = 0;
        boolean hasValidUsers = false;

        // DEBUG BREAKPOINT: Watch loop execution
        for (int i = 1; i <= 3; i++) { // Watch: i changes from 1 to 3
            User user = new User();
            user.setUsername("user" + i); // Watch: string concatenation

            // DEBUG BREAKPOINT: Watch conditional logic
            if (user.getUsername().length() > 4) { // Watch: condition evaluation
                userCount++; // Watch: userCount increment
                hasValidUsers = true; // Watch: boolean change
            }

            doNothing().when(userRepository).persist(user);
            userService.createUser(user);
        }

        // DEBUG BREAKPOINT: Final state check
        assertEquals(3, userCount); // Watch: final userCount value
        assertTrue(hasValidUsers); // Watch: final boolean value

        verify(userRepository, times(3)).persist(any(User.class));
    }

    @Test
    @DisplayName("Debug Complex Object Manipulation")
    void debugComplexObjectManipulation() {
        // DEBUG BREAKPOINT: Create complex user
        User complexUser = createComplexUser(); // Step INTO this method

        // DEBUG BREAKPOINT: Examine the created user
        assertNotNull(complexUser.getUsername());
        assertNotNull(complexUser.getEmail());
        assertNotNull(complexUser.getCreatedAt());

        // DEBUG BREAKPOINT: Modify the user
        String oldEmail = complexUser.getEmail(); // Watch: old value
        complexUser.setEmail("new_email@example.com"); // Watch: new value
        String newEmail = complexUser.getEmail(); // Watch: updated value

        assertNotEquals(oldEmail, newEmail); // Watch: comparison result
    }

    /**
     * Helper method to demonstrate stepping into methods
     */
    private User createComplexUser() {
        // DEBUG BREAKPOINT: Method entry
        User user = new User();

        // DEBUG BREAKPOINT: Watch each assignment
        user.setUsername("complex_user");
        user.setEmail("complex@example.com");
        user.setFirstName("Complex");
        user.setLastName("User");
        user.setPhoneNumber("+1234567890");
        user.setRole(UserRole.LIBRARIAN);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // DEBUG BREAKPOINT: Method exit - examine final object
        return user;
    }
}