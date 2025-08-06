package org.acme.service;

import org.acme.model.User;
import org.acme.model.Member;
import org.acme.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Debug/Test class for AuthenticationService
 * Used for debugging authentication functionality
 */
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private User testUser;
    private Member testMember;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Debug: Setup test data
        System.out.println("DEBUG: Setting up AuthenticationServiceTest");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("testpass");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testMember = new Member();
        testMember.setId(2L);
        testMember.setUsername("testmember");
        testMember.setPassword("memberpass");
        testMember.setEmail("member@example.com");
        testMember.setFirstName("Test");
        testMember.setLastName("Member");
        testMember.setPhoneNumber("555-0123");
        testMember.setCreatedAt(LocalDateTime.now());
        testMember.setUpdatedAt(LocalDateTime.now());

        System.out.println("DEBUG: Test data initialized");
    }

    @Test
    @DisplayName("Should register new member successfully")
    void testRegisterMember_Success() {
        System.out.println("DEBUG: Testing registerMember - Success scenario");

        // Given
        String username = "newmember";
        String password = "newpass";
        String email = "new@example.com";
        String firstName = "New";
        String lastName = "Member";
        String phoneNumber = "555-0456";

        // When
        Member result = authenticationService.registerMember(username, password, email, firstName, lastName,
                phoneNumber);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertEquals(email, result.getEmail());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(phoneNumber, result.getPhoneNumber());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());

        verify(userRepository, times(1)).persist(any(Member.class));
        System.out.println("DEBUG: Member registration successful - " + result.getUsername());
    }

    @Test
    @DisplayName("Should register member with null phone number")
    void testRegisterMember_NullPhoneNumber() {
        System.out.println("DEBUG: Testing registerMember - Null phone number scenario");

        // Given
        String username = "member2";
        String password = "pass2";
        String email = "member2@example.com";
        String firstName = "Member";
        String lastName = "Two";
        String phoneNumber = null;

        // When
        Member result = authenticationService.registerMember(username, password, email, firstName, lastName,
                phoneNumber);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertNull(result.getPhoneNumber());

        verify(userRepository, times(1)).persist(any(Member.class));
        System.out.println("DEBUG: Member registration with null phone successful");
    }

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void testLogin_Success() {
        System.out.println("DEBUG: Testing login - Success scenario");

        // Given
        when(userRepository.find("username", "testuser"))
                .thenReturn(mock(io.quarkus.hibernate.orm.panache.PanacheQuery.class));
        when(userRepository.find("username", "testuser").firstResultOptional()).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = authenticationService.login("testuser", "testpass");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getUsername(), result.get().getUsername());
        assertEquals(testUser.getId(), result.get().getId());

        verify(userRepository, times(1)).find("username", "testuser");
        System.out.println("DEBUG: Login successful for user: " + result.get().getUsername());
    }

    @Test
    @DisplayName("Should fail login with wrong password")
    void testLogin_WrongPassword() {
        System.out.println("DEBUG: Testing login - Wrong password scenario");

        // Given
        when(userRepository.find("username", "testuser"))
                .thenReturn(mock(io.quarkus.hibernate.orm.panache.PanacheQuery.class));
        when(userRepository.find("username", "testuser").firstResultOptional()).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = authenticationService.login("testuser", "wrongpass");

        // Then
        assertFalse(result.isPresent());

        verify(userRepository, times(1)).find("username", "testuser");
        System.out.println("DEBUG: Login failed with wrong password - as expected");
    }

    @Test
    @DisplayName("Should fail login with non-existent username")
    void testLogin_UserNotFound() {
        System.out.println("DEBUG: Testing login - User not found scenario");

        // Given
        when(userRepository.find("username", "nonexistent"))
                .thenReturn(mock(io.quarkus.hibernate.orm.panache.PanacheQuery.class));
        when(userRepository.find("username", "nonexistent").firstResultOptional()).thenReturn(Optional.empty());

        // When
        Optional<User> result = authenticationService.login("nonexistent", "anypass");

        // Then
        assertFalse(result.isPresent());

        verify(userRepository, times(1)).find("username", "nonexistent");
        System.out.println("DEBUG: Login failed for non-existent user - as expected");
    }

    @Test
    @DisplayName("Should handle null username in login")
    void testLogin_NullUsername() {
        System.out.println("DEBUG: Testing login - Null username scenario");

        // When - test with null username directly
        Optional<User> result = authenticationService.login(null, "anypass");

        // Then
        assertFalse(result.isPresent());

        // Note: We don't verify repository calls for null username as the service
        // handles this case before making repository calls
        System.out.println("DEBUG: Login with null username handled correctly");
    }

    @Test
    @DisplayName("Should handle null password in login")
    void testLogin_NullPassword() {
        System.out.println("DEBUG: Testing login - Null password scenario");

        // Given
        when(userRepository.find("username", "testuser"))
                .thenReturn(mock(io.quarkus.hibernate.orm.panache.PanacheQuery.class));
        when(userRepository.find("username", "testuser").firstResultOptional()).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = authenticationService.login("testuser", null);

        // Then
        assertFalse(result.isPresent());

        verify(userRepository, times(1)).find("username", "testuser");
        System.out.println("DEBUG: Login with null password handled correctly");
    }

    @Test
    @DisplayName("Should handle empty credentials")
    void testLogin_EmptyCredentials() {
        System.out.println("DEBUG: Testing login - Empty credentials scenario");

        // Given
        when(userRepository.find("username", "")).thenReturn(mock(io.quarkus.hibernate.orm.panache.PanacheQuery.class));
        when(userRepository.find("username", "").firstResultOptional()).thenReturn(Optional.empty());

        // When
        Optional<User> result = authenticationService.login("", "");

        // Then
        assertFalse(result.isPresent());

        verify(userRepository, times(1)).find("username", "");
        System.out.println("DEBUG: Login with empty credentials handled correctly");
    }

    @Test
    @DisplayName("Should register member with all fields populated")
    void testRegisterMember_AllFields() {
        System.out.println("DEBUG: Testing registerMember - All fields populated");

        // Given
        String username = "fullmember";
        String password = "fullpass";
        String email = "full@example.com";
        String firstName = "Full";
        String lastName = "Member";
        String phoneNumber = "555-9999";

        // When
        Member result = authenticationService.registerMember(username, password, email, firstName, lastName,
                phoneNumber);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(password, result.getPassword());
        assertEquals(email, result.getEmail());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(phoneNumber, result.getPhoneNumber());

        // Verify timestamps are set
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(result.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));

        verify(userRepository, times(1)).persist(argThat((Member member) -> {
            boolean usernameMatches = username.equals(member.getUsername());
            boolean emailMatches = email.equals(member.getEmail());
            boolean timestampsSet = member.getCreatedAt() != null && member.getUpdatedAt() != null;

            System.out.println("DEBUG: Verifying member persist - username: " + usernameMatches +
                    ", email: " + emailMatches + ", timestamps: " + timestampsSet);

            return usernameMatches && emailMatches && timestampsSet;
        }));

        System.out.println("DEBUG: Full member registration test completed successfully");
    }

    @Test
    @DisplayName("Should handle case-sensitive login")
    void testLogin_CaseSensitive() {
        System.out.println("DEBUG: Testing login - Case sensitivity");

        // Given - setup user with lowercase username
        User lowerCaseUser = new User();
        lowerCaseUser.setUsername("testuser");
        lowerCaseUser.setPassword("testpass");

        when(userRepository.find("username", "testuser"))
                .thenReturn(mock(io.quarkus.hibernate.orm.panache.PanacheQuery.class));
        when(userRepository.find("username", "testuser").firstResultOptional()).thenReturn(Optional.of(lowerCaseUser));

        when(userRepository.find("username", "TestUser"))
                .thenReturn(mock(io.quarkus.hibernate.orm.panache.PanacheQuery.class));
        when(userRepository.find("username", "TestUser").firstResultOptional()).thenReturn(Optional.empty());

        // When - try login with different case
        Optional<User> successResult = authenticationService.login("testuser", "testpass");
        Optional<User> failResult = authenticationService.login("TestUser", "testpass");

        // Then
        assertTrue(successResult.isPresent());
        assertFalse(failResult.isPresent());

        System.out.println("DEBUG: Case sensitivity test completed - lowercase worked, uppercase failed as expected");
    }
}