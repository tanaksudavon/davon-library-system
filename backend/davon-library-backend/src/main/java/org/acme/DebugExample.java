package org.acme;

import org.acme.model.User;
import org.acme.model.UserRole;
import org.acme.service.UserService;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;

/**
 * Example class to demonstrate debugging techniques
 * Set breakpoints on the lines marked with // BREAKPOINT
 */
@ApplicationScoped
public class DebugExample {

    @Inject
    private UserService userService;

    public void demonstrateDebugging() {
        System.out.println("=== Starting Debug Demo ===");

        // BREAKPOINT: Set a breakpoint here to start debugging
        String username = "debug_user";
        String email = "debug@example.com";

        // BREAKPOINT: Watch how the user object is created
        User user = new User();
        user.setUsername(username); // Watch username variable
        user.setEmail(email); // Watch email variable
        user.setRole(UserRole.MEMBER);
        user.setCreatedAt(LocalDateTime.now());

        // BREAKPOINT: Step into this method to see null handling
        User createdUser = userService.createUser(user);

        // BREAKPOINT: Check if user was created successfully
        if (createdUser != null) {
            System.out.println("User created: " + createdUser.getUsername());
            Long userId = createdUser.getId();

            // BREAKPOINT: Step into this method to see database query
            var foundUser = userService.getUserById(userId);

            if (foundUser.isPresent()) {
                System.out.println("User found: " + foundUser.get().getEmail());
            }
        }

        // BREAKPOINT: Test null handling
        User nullUser = null;
        User nullResult = userService.createUser(nullUser); // Watch this return null

        System.out.println("=== Debug Demo Complete ===");
    }

    /**
     * Demonstrates debugging with different data types and conditions
     */
    public void debugDataTypes() {
        // BREAKPOINT: Watch primitive variables
        int count = 0;
        boolean isActive = true;
        double score = 95.5;

        // BREAKPOINT: Watch object creation and modification
        User testUser = new User();
        testUser.setUsername("test" + count); // Watch string concatenation

        // BREAKPOINT: Watch loop execution
        for (int i = 0; i < 3; i++) {
            count++; // Watch count increment
            System.out.println("Iteration: " + i + ", Count: " + count);
        }

        // BREAKPOINT: Watch conditional logic
        if (score > 90) {
            isActive = true; // Watch boolean change
            System.out.println("High score achieved!");
        } else {
            isActive = false;
            System.out.println("Score needs improvement");
        }
    }
}