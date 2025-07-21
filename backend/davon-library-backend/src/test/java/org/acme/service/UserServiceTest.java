package org.acme.service;

import org.acme.model.User;
import org.acme.model.UserRole;
import org.acme.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService class
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        testUser = new User(1L, "john_doe", "password123", "john@example.com",
                "John", "Doe", "+1234567890", UserRole.MEMBER, now, now);

        User user2 = new User(2L, "jane_smith", "securePass", "jane@example.com",
                "Jane", "Smith", "+1987654321", UserRole.LIBRARIAN, now, now);

        testUsers = Arrays.asList(testUser, user2);
    }

    @Test
    @DisplayName("Test getAllUsers returns all users")
    void testGetAllUsers() {
        // Given
        when(userRepository.listAll()).thenReturn(testUsers);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUsers, result);
        verify(userRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Test getAllUsers returns empty list when no users exist")
    void testGetAllUsersWhenEmpty() {
        // Given
        when(userRepository.listAll()).thenReturn(Collections.emptyList());

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Test getUserById returns user when found")
    void testGetUserByIdFound() {
        // Given
        Long userId = 1L;
        when(userRepository.findByIdOptional(userId)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(userId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        assertEquals("john_doe", result.get().getUsername());
        assertEquals("john@example.com", result.get().getEmail());
        assertEquals(UserRole.MEMBER, result.get().getRole());
        verify(userRepository, times(1)).findByIdOptional(userId);
    }

    @Test
    @DisplayName("Test getUserById returns empty when not found")
    void testGetUserByIdNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findByIdOptional(userId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(userId);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByIdOptional(userId);
    }

    @Test
    @DisplayName("Test getUserById with null id")
    void testGetUserByIdWithNullId() {
        // Given
        when(userRepository.findByIdOptional(null)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(null);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByIdOptional(null);
    }

    @Test
    @DisplayName("Test createUser successfully creates and returns user")
    void testCreateUserSuccess() {
        // Given
        User newUser = new User();
        newUser.setUsername("new_user");
        newUser.setPassword("password");
        newUser.setEmail("newuser@example.com");
        newUser.setRole(UserRole.MEMBER);

        doNothing().when(userRepository).persist(newUser);

        // When
        User result = userService.createUser(newUser);

        // Then
        assertNotNull(result);
        assertEquals(newUser, result);
        assertEquals("new_user", result.getUsername());
        assertEquals("newuser@example.com", result.getEmail());
        assertEquals(UserRole.MEMBER, result.getRole());
        verify(userRepository, times(1)).persist(newUser);
    }

    @Test
    @DisplayName("Test createUser with null user")
    void testCreateUserWithNullUser() {
        // Given
        User nullUser = null;
        doNothing().when(userRepository).persist(nullUser);

        // When
        User result = userService.createUser(nullUser);

        // Then
        assertNull(result);
        verify(userRepository, times(1)).persist(nullUser);
    }

    @Test
    @DisplayName("Test createUser with complete user data")
    void testCreateUserWithCompleteData() {
        // Given
        User completeUser = new User();
        completeUser.setUsername("complete_user");
        completeUser.setPassword("securePassword123");
        completeUser.setEmail("complete@example.com");
        completeUser.setFirstName("Complete");
        completeUser.setLastName("User");
        completeUser.setPhoneNumber("+1555123456");
        completeUser.setRole(UserRole.LIBRARIAN);
        completeUser.setCreatedAt(LocalDateTime.now());
        completeUser.setUpdatedAt(LocalDateTime.now());

        doNothing().when(userRepository).persist(completeUser);

        // When
        User result = userService.createUser(completeUser);

        // Then
        assertNotNull(result);
        assertEquals("complete_user", result.getUsername());
        assertEquals("complete@example.com", result.getEmail());
        assertEquals("Complete", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("+1555123456", result.getPhoneNumber());
        assertEquals(UserRole.LIBRARIAN, result.getRole());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(userRepository, times(1)).persist(completeUser);
    }

    @Test
    @DisplayName("Test createUser with different user roles")
    void testCreateUserWithDifferentRoles() {
        // Test GUEST role
        User guestUser = new User();
        guestUser.setUsername("guest");
        guestUser.setRole(UserRole.GUEST);
        doNothing().when(userRepository).persist(guestUser);

        User guestResult = userService.createUser(guestUser);
        assertEquals(UserRole.GUEST, guestResult.getRole());

        // Test MEMBER role
        User memberUser = new User();
        memberUser.setUsername("member");
        memberUser.setRole(UserRole.MEMBER);
        doNothing().when(userRepository).persist(memberUser);

        User memberResult = userService.createUser(memberUser);
        assertEquals(UserRole.MEMBER, memberResult.getRole());

        // Test LIBRARIAN role
        User librarianUser = new User();
        librarianUser.setUsername("librarian");
        librarianUser.setRole(UserRole.LIBRARIAN);
        doNothing().when(userRepository).persist(librarianUser);

        User librarianResult = userService.createUser(librarianUser);
        assertEquals(UserRole.LIBRARIAN, librarianResult.getRole());

        verify(userRepository, times(3)).persist(any(User.class));
    }

    @Test
    @DisplayName("Test deleteUser successfully deletes existing user")
    void testDeleteUserSuccess() {
        // Given
        Long userId = 1L;
        when(userRepository.deleteById(userId)).thenReturn(true);

        // When
        assertDoesNotThrow(() -> userService.deleteUser(userId));

        // Then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Test deleteUser with non-existent user")
    void testDeleteUserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.deleteById(userId)).thenReturn(false);

        // When
        assertDoesNotThrow(() -> userService.deleteUser(userId));

        // Then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Test deleteUser with null id")
    void testDeleteUserWithNullId() {
        // Given
        when(userRepository.deleteById(null)).thenReturn(false);

        // When
        assertDoesNotThrow(() -> userService.deleteUser(null));

        // Then
        verify(userRepository, times(1)).deleteById(null);
    }

    @Test
    @DisplayName("Test repository interaction count")
    void testRepositoryInteractionCount() {
        // Given
        when(userRepository.listAll()).thenReturn(testUsers);
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).persist(any(User.class));

        // When
        userService.getAllUsers();
        userService.getUserById(1L);
        userService.createUser(new User());
        userService.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).listAll();
        verify(userRepository, times(1)).findByIdOptional(1L);
        verify(userRepository, times(1)).persist(any(User.class));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test service behavior with repository exceptions")
    void testServiceWithRepositoryExceptions() {
        // Given
        when(userRepository.listAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> userService.getAllUsers());
        verify(userRepository, times(1)).listAll();
    }

    @Test
    @DisplayName("Test createUser persistence behavior")
    void testCreateUserPersistenceBehavior() {
        // Given
        User user = new User();
        user.setUsername("test_user");
        user.setEmail("test@example.com");

        doAnswer(invocation -> {
            User persistedUser = invocation.getArgument(0);
            persistedUser.setId(1L); // Simulate ID assignment
            persistedUser.setCreatedAt(LocalDateTime.now());
            persistedUser.setUpdatedAt(LocalDateTime.now());
            return null;
        }).when(userRepository).persist(user);

        // When
        User result = userService.createUser(user);

        // Then
        assertNotNull(result);
        assertEquals("test_user", result.getUsername());
        assertEquals("test@example.com", result.getEmail());
        assertEquals(1L, result.getId()); // Verify ID was set
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(userRepository, times(1)).persist(user);
    }

    @Test
    @DisplayName("Test multiple user retrieval scenarios")
    void testMultipleUserRetrievalScenarios() {
        // Test scenario 1: Get existing member
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(testUser));
        Optional<User> result1 = userService.getUserById(1L);
        assertTrue(result1.isPresent());
        assertEquals("john_doe", result1.get().getUsername());
        assertEquals(UserRole.MEMBER, result1.get().getRole());

        // Test scenario 2: Get non-existing user
        when(userRepository.findByIdOptional(99L)).thenReturn(Optional.empty());
        Optional<User> result2 = userService.getUserById(99L);
        assertFalse(result2.isPresent());

        // Test scenario 3: Get librarian user
        User librarian = new User();
        librarian.setId(3L);
        librarian.setUsername("librarian");
        librarian.setRole(UserRole.LIBRARIAN);
        when(userRepository.findByIdOptional(3L)).thenReturn(Optional.of(librarian));
        Optional<User> result3 = userService.getUserById(3L);
        assertTrue(result3.isPresent());
        assertEquals("librarian", result3.get().getUsername());
        assertEquals(UserRole.LIBRARIAN, result3.get().getRole());

        // Verify all interactions
        verify(userRepository, times(1)).findByIdOptional(1L);
        verify(userRepository, times(1)).findByIdOptional(99L);
        verify(userRepository, times(1)).findByIdOptional(3L);
    }

    @Test
    @DisplayName("Test user data validation scenarios")
    void testUserDataValidationScenarios() {
        // Test with minimal required data
        User minimalUser = new User();
        minimalUser.setUsername("minimal");
        minimalUser.setPassword("pass");
        doNothing().when(userRepository).persist(minimalUser);

        User result1 = userService.createUser(minimalUser);
        assertEquals("minimal", result1.getUsername());
        assertNull(result1.getEmail());
        assertNull(result1.getRole());

        // Test with email only
        User emailOnlyUser = new User();
        emailOnlyUser.setEmail("email@example.com");
        doNothing().when(userRepository).persist(emailOnlyUser);

        User result2 = userService.createUser(emailOnlyUser);
        assertEquals("email@example.com", result2.getEmail());
        assertNull(result2.getUsername());

        // Test with full contact information
        User fullContactUser = new User();
        fullContactUser.setUsername("full_contact");
        fullContactUser.setEmail("full@example.com");
        fullContactUser.setFirstName("Full");
        fullContactUser.setLastName("Contact");
        fullContactUser.setPhoneNumber("+1234567890");
        doNothing().when(userRepository).persist(fullContactUser);

        User result3 = userService.createUser(fullContactUser);
        assertEquals("full_contact", result3.getUsername());
        assertEquals("full@example.com", result3.getEmail());
        assertEquals("Full", result3.getFirstName());
        assertEquals("Contact", result3.getLastName());
        assertEquals("+1234567890", result3.getPhoneNumber());

        verify(userRepository, times(3)).persist(any(User.class));
    }
}