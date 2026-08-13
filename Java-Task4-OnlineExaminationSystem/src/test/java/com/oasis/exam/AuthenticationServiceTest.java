package com.oasis.exam;

import com.oasis.exam.model.UserSession;
import com.oasis.exam.repository.UserRepository;
import com.oasis.exam.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationServiceTest {
    private UserRepository userRepository;
    private UserSession userSession;
    private AuthenticationService authService;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        userSession = new UserSession();
        authService = new AuthenticationService(userRepository, userSession);
    }

    @Test
    void testSuccessfulLogin() {
        boolean success = authService.login("student1", "password123");
        assertTrue(success, "Login should succeed with valid credentials.");
        assertTrue(userSession.isLoggedIn(), "Session should be active after login.");
        assertEquals("Candidate Student", userSession.getCurrentUser().getDisplayName());
    }

    @Test
    void testFailedLoginInvalidCredentials() {
        boolean success = authService.login("student1", "wrongpass");
        assertFalse(success, "Login should fail with wrong password.");
        assertFalse(userSession.isLoggedIn(), "Session should remain inactive after failed login.");
    }

    @Test
    void testFailedLoginEmptyInputs() {
        assertFalse(authService.login("", "password123"));
        assertFalse(authService.login("student1", ""));
        assertFalse(authService.login(null, null));
    }

    @Test
    void testUpdateProfileSuccess() {
        authService.login("student1", "password123");
        boolean updated = authService.updateProfile("Tanuj S.", "newpass123", "newpass123");
        assertTrue(updated, "Profile update should succeed.");
        assertEquals("Tanuj S.", userSession.getCurrentUser().getDisplayName());

        // Logout and verify new credentials work
        authService.logout();
        assertTrue(authService.login("student1", "newpass123"));
    }

    @Test
    void testUpdateProfilePasswordMismatchThrowsException() {
        authService.login("student1", "password123");
        assertThrows(IllegalArgumentException.class, () ->
                authService.updateProfile("Tanuj S.", "newpass123", "mismatchpass")
        );
    }

    @Test
    void testLogoutClearsSession() {
        authService.login("student1", "password123");
        assertTrue(userSession.isLoggedIn());
        authService.logout();
        assertFalse(userSession.isLoggedIn());
        assertNull(userSession.getCurrentUser());
    }
}
