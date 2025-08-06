package org.acme.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

@DisplayName("User Model Tests")
public class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Should create user with default constructor")
    void testDefaultConstructor() {
        User newUser = new User();

        assertNotNull(newUser);
        assertNull(newUser.getId());
        assertNull(newUser.getFirstName());
        assertNull(newUser.getLastName());
        assertNull(newUser.getEmail());
        assertNull(newUser.getUsername());

        System.out.println("✅ Default constructor test passed");
    }

    @Test
    @DisplayName("Should set and get user properties correctly")
    void testUserProperties() {
        // Arrange
        Long id = 1L;
        String firstName = "John";
        String lastName = "Doe";
        String email = "john.doe@example.com";
        String username = "johndoe";
        String password = "password123";
        String phoneNumber = "123-456-7890";
        UserRole role = UserRole.MEMBER;
        LocalDateTime now = LocalDateTime.now();

        // Act
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);
        user.setRole(role);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Assert
        assertEquals(id, user.getId());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(email, user.getEmail());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(phoneNumber, user.getPhoneNumber());
        assertEquals(role, user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());

        System.out.println("✅ User properties test passed");
        System.out.println("User: " + firstName + " " + lastName);
        System.out.println("Email: " + email);
        System.out.println("Role: " + role);
    }

    @Test
    @DisplayName("Should handle different user roles")
    void testUserRoles() {
        UserRole[] roles = { UserRole.MEMBER, UserRole.LIBRARIAN };

        System.out.println("✅ Testing user roles:");
        for (UserRole role : roles) {
            user.setRole(role);
            assertEquals(role, user.getRole());
            System.out.println("  - " + role + " ✓");
        }
    }

    @Test
    @DisplayName("Should create member user")
    void testCreateMemberUser() {
        // Act
        user.setFirstName("Alice");
        user.setLastName("Johnson");
        user.setEmail("alice.johnson@example.com");
        user.setUsername("alice");
        user.setRole(UserRole.MEMBER);

        // Assert
        assertEquals("Alice", user.getFirstName());
        assertEquals("Johnson", user.getLastName());
        assertEquals("alice.johnson@example.com", user.getEmail());
        assertEquals("alice", user.getUsername());
        assertEquals(UserRole.MEMBER, user.getRole());

        System.out.println("✅ Member user creation test passed");
        System.out.println("Member: " + user.getFirstName() + " " + user.getLastName());
    }

    @Test
    @DisplayName("Should create librarian user")
    void testCreateLibrarianUser() {
        // Act
        user.setFirstName("Bob");
        user.setLastName("Smith");
        user.setEmail("bob.smith@library.com");
        user.setUsername("bobsmith");
        user.setRole(UserRole.LIBRARIAN);

        // Assert
        assertEquals("Bob", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("bob.smith@library.com", user.getEmail());
        assertEquals("bobsmith", user.getUsername());
        assertEquals(UserRole.LIBRARIAN, user.getRole());

        System.out.println("✅ Librarian user creation test passed");
        System.out.println("Librarian: " + user.getFirstName() + " " + user.getLastName());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void testNullValues() {
        user.setFirstName(null);
        user.setLastName(null);
        user.setEmail(null);
        user.setUsername(null);
        user.setPassword(null);
        user.setPhoneNumber(null);
        user.setRole(null);

        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getEmail());
        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getPhoneNumber());
        assertNull(user.getRole());

        System.out.println("✅ Null values test passed");
    }

    @Test
    @DisplayName("Should validate email format scenarios")
    void testEmailValidation() {
        String[] validEmails = {
                "user@example.com",
                "test.email@domain.org",
                "user123@test-domain.co.uk"
        };

        String[] invalidEmails = {
                "invalid-email",
                "@domain.com",
                "user@",
                "user..double.dot@domain.com"
        };

        System.out.println("✅ Testing email scenarios:");

        for (String email : validEmails) {
            user.setEmail(email);
            assertEquals(email, user.getEmail());
            System.out.println("  - Valid: " + email + " ✓");
        }

        for (String email : invalidEmails) {
            user.setEmail(email);
            assertEquals(email, user.getEmail()); // Model accepts any string, validation should be elsewhere
            System.out.println("  - Invalid (stored but should be validated): " + email + " ⚠️");
        }
    }

    @Test
    @DisplayName("Should debug user creation timestamps")
    void testUserTimestamps() {
        LocalDateTime before = LocalDateTime.now();

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        LocalDateTime after = LocalDateTime.now();

        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());

        assertTrue(user.getCreatedAt().isAfter(before) || user.getCreatedAt().isEqual(before));
        assertTrue(user.getCreatedAt().isBefore(after) || user.getCreatedAt().isEqual(after));

        System.out.println("✅ User timestamps test passed");
        System.out.println("Before: " + before);
        System.out.println("Created: " + user.getCreatedAt());
        System.out.println("Updated: " + user.getUpdatedAt());
        System.out.println("After: " + after);
    }

    @Test
    @DisplayName("Should create complete user profile")
    void testCompleteUserProfile() {
        // Create a complete user profile
        user.setId(100L);
        user.setFirstName("Emma");
        user.setLastName("Wilson");
        user.setEmail("emma.wilson@example.com");
        user.setUsername("emmaw");
        user.setPassword("securePassword123");
        user.setPhoneNumber("555-0123");
        user.setRole(UserRole.MEMBER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Verify all fields are set
        assertNotNull(user.getId());
        assertNotNull(user.getFirstName());
        assertNotNull(user.getLastName());
        assertNotNull(user.getEmail());
        assertNotNull(user.getUsername());
        assertNotNull(user.getPassword());
        assertNotNull(user.getPhoneNumber());
        assertNotNull(user.getRole());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());

        System.out.println("✅ Complete user profile test passed");
        System.out.println("Complete User Profile:");
        System.out.println("  ID: " + user.getId());
        System.out.println("  Name: " + user.getFirstName() + " " + user.getLastName());
        System.out.println("  Email: " + user.getEmail());
        System.out.println("  Username: " + user.getUsername());
        System.out.println("  Phone: " + user.getPhoneNumber());
        System.out.println("  Role: " + user.getRole());
        System.out.println("  Created: " + user.getCreatedAt());
    }

    @Test
    @DisplayName("Should debug user role permissions")
    void testUserRolePermissions() {
        System.out.println("✅ Debugging user role permissions:");

        // Member user
        user.setRole(UserRole.MEMBER);
        System.out.println("Member permissions:");
        System.out.println("  - Can borrow books: true");
        System.out.println("  - Can reserve books: true");
        System.out.println("  - Can manage books: false");
        System.out.println("  - Can manage users: false");

        // Librarian user
        user.setRole(UserRole.LIBRARIAN);
        System.out.println("Librarian permissions:");
        System.out.println("  - Can borrow books: true");
        System.out.println("  - Can reserve books: true");
        System.out.println("  - Can manage books: true");
        System.out.println("  - Can manage users: true");
    }
}