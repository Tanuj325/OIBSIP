package com.oasis.service;

import com.oasis.dao.UserDAO;
import com.oasis.model.User;
import com.oasis.util.ValidationUtil;

/**
 * Service class handling user authentication business logic.
 */
public class AuthenticationService {

    private final UserDAO userDAO;

    public AuthenticationService() {
        this.userDAO = new UserDAO();
    }

    public AuthenticationService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Attempts to log in user with given credentials.
     *
     * @param username username input
     * @param password password input
     * @return User object if successful, null if failed
     * @throws IllegalArgumentException if username or password is empty
     */
    public User login(String username, String password) {
        if (ValidationUtil.isNullOrEmpty(username) || ValidationUtil.isNullOrEmpty(password)) {
            throw new IllegalArgumentException("Username and Password cannot be empty.");
        }
        return userDAO.authenticate(username.trim(), password);
    }
}
