package com.oasis.exam.service;

import com.oasis.exam.model.User;
import com.oasis.exam.model.UserSession;
import com.oasis.exam.repository.UserRepository;
import com.oasis.exam.util.ValidationUtil;

import java.util.Optional;

/**
 * Handles user authentication, profile modification, and session lifecycle.
 */
public class AuthenticationService {
    private final UserRepository userRepository;
    private final UserSession userSession;

    public AuthenticationService(UserRepository userRepository, UserSession userSession) {
        this.userRepository = userRepository;
        this.userSession = userSession;
    }

    public boolean login(String username, String password) {
        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(password)) {
            return false;
        }
        Optional<User> userOpt = userRepository.authenticate(username, password);
        if (userOpt.isPresent()) {
            userSession.startSession(userOpt.get());
            return true;
        }
        return false;
    }

    public boolean updateProfile(String newDisplayName, String newPassword, String confirmPassword) {
        if (!userSession.isLoggedIn()) {
            return false;
        }
        if (ValidationUtil.isEmpty(newDisplayName)) {
            throw new IllegalArgumentException("Display Name cannot be empty.");
        }

        // If password fields are modified, check matching confirmation
        boolean updatePassword = !ValidationUtil.isEmpty(newPassword);
        if (updatePassword) {
            if (ValidationUtil.isEmpty(confirmPassword)) {
                throw new IllegalArgumentException("Please confirm your new password.");
            }
            if (!newPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("New Password and Confirm Password do not match.");
            }
        }

        User currentUser = userSession.getCurrentUser();
        boolean success = userRepository.updateProfile(currentUser.getUsername(), newDisplayName, updatePassword ? newPassword : null);
        if (success) {
            currentUser.setDisplayName(newDisplayName.trim());
        }
        return success;
    }

    public void logout() {
        userSession.clearSession();
    }

    public UserSession getSession() {
        return userSession;
    }
}
