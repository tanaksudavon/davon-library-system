package org.acme;

import org.acme.model.User;
import org.acme.model.UserRole;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple Main class for debugging demonstration
 * No framework dependencies - pure Java debugging
 * 
 * HOW TO DEBUG:
 * 1. Set breakpoints on lines with // BREAKPOINT comments
 * 2. Right-click this class and select "Debug 'DebugMain.main()'"
 * 3. Use debugging controls:
 * - F8: Step Over (execute current line, don't go into methods)
 * - F7: Step Into (go inside method calls)
 * - Shift+F8: Step Out (exit current method)
 * - F9: Resume (continue until next breakpoint)
 */
public class DebugMain {

    public static void main(String[] args) {
        DebugMain debugDemo = new DebugMain();

        System.out.println("=== Starting Debug Demo ===");

        // BREAKPOINT: Start debugging here
        debugDemo.demonstrateVariableWatching();

        // BREAKPOINT: Watch method call
        debugDemo.demonstrateLoopDebugging();

        // BREAKPOINT: Watch object manipulation
        debugDemo.demonstrateObjectDebugging();

        System.out.println("=== Debug Demo Complete ===");
    }

    /**
     * Demonstrates watching variables change
     */
    public void demonstrateVariableWatching() {
        System.out.println("\n--- Variable Watching Demo ---");

        // BREAKPOINT: Watch primitive variables
        int counter = 0; // Watch: counter = 0
        String name = "John"; // Watch: name = "John"
        boolean isActive = false; // Watch: isActive = false
        double score = 0.0; // Watch: score = 0.0

        // BREAKPOINT: Watch variables change
        counter = 10; // Watch: counter changes to 10
        name = name + " Doe"; // Watch: name becomes "John Doe"
        isActive = true; // Watch: isActive becomes true
        score = 95.5; // Watch: score becomes 95.5

        // BREAKPOINT: Watch calculations
        // Watch: doubleCounter = 20
        // Watch: fullName = "Mr. John Doe"

        System.out.println("Counter: " + counter);
        System.out.println("Name: " + name);
        System.out.println("Active: " + isActive);
        System.out.println("Score: " + score);
    }

    /**
     * Demonstrates debugging loops and conditions
     */
    public void demonstrateLoopDebugging() {
        System.out.println("\n--- Loop Debugging Demo ---");

        // BREAKPOINT: Initialize loop variables
        List<String> names = new ArrayList<>();
        int validCount = 0;

        // BREAKPOINT: Watch loop execution
        for (int i = 0; i < 5; i++) { // Watch: i = 0, 1, 2, 3, 4
            String currentName = "User" + i; // Watch: currentName changes each iteration
            names.add(currentName); // Watch: names list grows

            // BREAKPOINT: Watch conditional logic
            if (currentName.length() > 4) { // Watch: condition evaluation
                validCount++; // Watch: validCount increment
                System.out.println("Valid name: " + currentName);
            }

            // BREAKPOINT: Watch loop variable increment
            // i will increment automatically here
        }

        // BREAKPOINT: Watch final values
        System.out.println("Total names: " + names.size()); // Watch: should be 5
        System.out.println("Valid names: " + validCount); // Watch: should be 4
    }

    /**
     * Demonstrates debugging object creation and manipulation
     */
    public void demonstrateObjectDebugging() {
        System.out.println("\n--- Object Debugging Demo ---");

        // BREAKPOINT: Watch object creation
        User user = new User(); // Watch: user object created (initially all fields null/default)

        // BREAKPOINT: Watch field assignments
        user.setId(1L); // Watch: id = 1
        user.setUsername("debug_user"); // Watch: username = "debug_user"
        user.setEmail("debug@example.com"); // Watch: email = "debug@example.com"
        user.setRole(UserRole.MEMBER); // Watch: role = MEMBER

        LocalDateTime now = LocalDateTime.now(); // Watch: now = current timestamp
        user.setCreatedAt(now); // Watch: createdAt = now

        // BREAKPOINT: Watch method call and return
        String userInfo = getUserInfo(user); // Step INTO this method

        // BREAKPOINT: Watch final state
        System.out.println("User info: " + userInfo);

        // BREAKPOINT: Watch object modification
        String oldUsername = user.getUsername(); // Watch: oldUsername = "debug_user"
        user.setUsername("modified_user"); // Watch: username changes
        String newUsername = user.getUsername(); // Watch: newUsername = "modified_user"

        System.out.println("Old username: " + oldUsername);
        System.out.println("New username: " + newUsername);
    }

    /**
     * Helper method to demonstrate stepping into methods
     */
    private String getUserInfo(User user) {
        // BREAKPOINT: Method entry - examine parameters
        if (user == null) { // Watch: condition evaluation
            return "No user provided";
        }

        // BREAKPOINT: Watch string building
        String info = "User: " + user.getUsername(); // Watch: string concatenation

        if (user.getEmail() != null) { // Watch: null check
            info += " (" + user.getEmail() + ")"; // Watch: string append
        }

        // BREAKPOINT: Method exit - examine return value
        return info;
    }
}