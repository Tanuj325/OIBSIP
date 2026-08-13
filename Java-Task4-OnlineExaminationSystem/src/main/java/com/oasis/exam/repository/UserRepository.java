package com.oasis.exam.repository;

import com.oasis.exam.model.User;
import com.oasis.exam.util.ValidationUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe user repository supporting user lookup, authentication, and profile updates.
 */
public class UserRepository {
    private final Map<String, User> userMap = new ConcurrentHashMap<>();

    public UserRepository() {
        seedInitialUsers();
    }

    private void seedInitialUsers() {
        // Pre-seeded standard accounts
        addUser("USR-001", "student1", "password123", "Candidate Student");
        addUser("USR-002", "admin", "admin123", "System Administrator");
        addUser("USR-003", "candidate", "pass123", "Alex Johnson");
    }

    public void addUser(String userId, String username, String rawPassword, String displayName) {
        String hash = hashPassword(rawPassword);
        User user = new User(userId, username, hash, displayName);
        userMap.put(username.toLowerCase(), user);
    }

    public Optional<User> findByUsername(String username) {
        if (username == null) return Optional.empty();
        return Optional.ofNullable(userMap.get(username.trim().toLowerCase()));
    }

    public Optional<User> authenticate(String username, String rawPassword) {
        if (ValidationUtil.isEmpty(username) || ValidationUtil.isEmpty(rawPassword)) {
            return Optional.empty();
        }
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String hash = hashPassword(rawPassword);
            if (user.getPasswordHash().equals(hash)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public boolean updateProfile(String username, String newDisplayName, String newPassword) {
        Optional<User> userOpt = findByUsername(username);
        if (userOpt.isEmpty()) {
            return false;
        }
        User user = userOpt.get();
        if (!ValidationUtil.isEmpty(newDisplayName)) {
            user.setDisplayName(newDisplayName.trim());
        }
        if (!ValidationUtil.isEmpty(newPassword)) {
            user.setPasswordHash(hashPassword(newPassword.trim()));
        }
        return true;
    }

    public static String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            // Fallback for extreme cases
            return String.valueOf(rawPassword.hashCode());
        }
    }
}
