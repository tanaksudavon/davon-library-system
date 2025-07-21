package org.acme.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * Unit tests for User entity
 */
@DisplayName("User Entity Tests")
class UserTest {

    private User user;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        user = new User();
    }

    @Test
    @DisplayName("Test no-args constructor")
    void testNoArgsConstructor() {
        User newUser = new User();
        assertNotNull(newUser);
        assertNull(newUser.getId());
        assertNull(newUser.getUsername());
        assertNull(newUser.getPassword());
        assertNull(newUser.getEmail());
        assertNull(newUser.getFirstName());
        assertNull(newUser.getLastName());
        assertNull(newUser.getPhoneNumber());
        assertNull(newUser.getRole());
        assertNull(newUser.getCreatedAt());
        assertNull(newUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Test all-args constructor")
    void testAllArgsConstructor() {
        User newUser = new User(1L, "john_doe", "password123", "john@example.com",
                "John", "Doe", "+1234567890", UserRole.MEMBER, now, now);

        assertEquals(1L, newUser.getId());
        assertEquals("john_doe", newUser.getUsername());
        assertEquals("password123", newUser.getPassword());
        assertEquals("john@example.com", newUser.getEmail());
        assertEquals("John", newUser.getFirstName());
        assertEquals("Doe", newUser.getLastName());
        assertEquals("+1234567890", newUser.getPhoneNumber());
        assertEquals(UserRole.MEMBER, newUser.getRole());
        assertEquals(now, newUser.getCreatedAt());
        assertEquals(now, newUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Test setters and getters")
    void testSettersAndGetters() {
        user.setId(1L);
        user.setUsername("jane_smith");
        user.setPassword("securePassword");
        user.setEmail("jane.smith@example.com");
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setPhoneNumber("+1987654321");
        user.setRole(UserRole.LIBRARIAN);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertEquals(1L, user.getId());
        assertEquals("jane_smith", user.getUsername());
        assertEquals("securePassword", user.getPassword());
        assertEquals("jane.smith@example.com", user.getEmail());
        assertEquals("Jane", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("+1987654321", user.getPhoneNumber());
        assertEquals(UserRole.LIBRARIAN, user.getRole());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    @DisplayName("Test all user roles")
    void testUserRoles() {
        user.setRole(UserRole.GUEST);
        assertEquals(UserRole.GUEST, user.getRole());

        user.setRole(UserRole.MEMBER);
        assertEquals(UserRole.MEMBER, user.getRole());

        user.setRole(UserRole.LIBRARIAN);
        assertEquals(UserRole.LIBRARIAN, user.getRole());
    }

    @Test
    @DisplayName("Test email validation scenarios")
    void testEmailScenarios() {
        // Test valid email formats
        String[] validEmails = {
                "user@example.com",
                "user.name@example.com",
                "user+tag@example.com",
                "user_name@example-domain.com",
                "123@example.com"
        };

        for (String email : validEmails) {
            user.setEmail(email);
            assertEquals(email, user.getEmail());
        }

        // Test empty and null email
        user.setEmail("");
        assertEquals("", user.getEmail());

        user.setEmail(null);
        assertNull(user.getEmail());
    }

    @Test
    @DisplayName("Test username scenarios")
    void testUsernameScenarios() {
        // Test various username formats
        String[] validUsernames = {
                "john_doe",
                "jane123",
                "user-name",
                "simple",
                "a"
        };

        for (String username : validUsernames) {
            user.setUsername(username);
            assertEquals(username, user.getUsername());
        }

        // Test empty and null username
        user.setUsername("");
        assertEquals("", user.getUsername());

        user.setUsername(null);
        assertNull(user.getUsername());
    }

    @Test
    @DisplayName("Test phone number scenarios")
    void testPhoneNumberScenarios() {
        String[] phoneNumbers = {
                "+1234567890",
                "123-456-7890",
                "(123) 456-7890",
                "123.456.7890",
                "1234567890"
        };

        for (String phoneNumber : phoneNumbers) {
            user.setPhoneNumber(phoneNumber);
            assertEquals(phoneNumber, user.getPhoneNumber());
        }

        // Test null phone number
        user.setPhoneNumber(null);
        assertNull(user.getPhoneNumber());
    }

    @Test
    @DisplayName("Test password scenarios")
    void testPasswordScenarios() {
        // Test different password types
        String[] passwords = {
                "simplePassword",
                "Complex123!",
                "very_long_password_with_many_characters_123456789",
                "123456",
                "!@#$%^&*()"
        };

        for (String password : passwords) {
            user.setPassword(password);
            assertEquals(password, user.getPassword());
        }

        // Test empty and null password
        user.setPassword("");
        assertEquals("", user.getPassword());

        user.setPassword(null);
        assertNull(user.getPassword());
    }

    @Test
    @DisplayName("Test null values handling")
    void testNullValues() {
        assertDoesNotThrow(() -> {
            user.setUsername(null);
            user.setPassword(null);
            user.setEmail(null);
            user.setFirstName(null);
            user.setLastName(null);
            user.setPhoneNumber(null);
            user.setRole(null);
            user.setCreatedAt(null);
            user.setUpdatedAt(null);
        });

        assertNull(user.getUsername());
        assertNull(user.getPassword());
        assertNull(user.getEmail());
        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getPhoneNumber());
        assertNull(user.getRole());
        assertNull(user.getCreatedAt());
        assertNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Test name combinations")
    void testNameCombinations() {
        // Test with both first and last name
        user.setFirstName("John");
        user.setLastName("Doe");
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());

        // Test with only first name
        user.setFirstName("Madonna");
        user.setLastName(null);
        assertEquals("Madonna", user.getFirstName());
        assertNull(user.getLastName());

        // Test with only last name
        user.setFirstName(null);
        user.setLastName("Cher");
        assertNull(user.getFirstName());
        assertEquals("Cher", user.getLastName());

        // Test with empty names
        user.setFirstName("");
        user.setLastName("");
        assertEquals("", user.getFirstName());
        assertEquals("", user.getLastName());
    }

    @Test
    @DisplayName("Test timestamp scenarios")
    void testTimestampScenarios() {
        LocalDateTime pastTime = LocalDateTime.of(2020, 1, 1, 12, 0, 0);
        LocalDateTime futureTime = LocalDateTime.of(2030, 1, 1, 12, 0, 0);

        user.setCreatedAt(pastTime);
        user.setUpdatedAt(futureTime);

        assertEquals(pastTime, user.getCreatedAt());
        assertEquals(futureTime, user.getUpdatedAt());

        // Test with same timestamps
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }
}