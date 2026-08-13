package com.oasis.exam.ui;

import com.oasis.exam.service.AuthenticationService;
import com.oasis.exam.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Centered, professional login panel with credential validation.
 */
public class LoginPanel extends JPanel {
    private final AuthenticationService authenticationService;
    private final LoginSuccessListener successListener;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;

    public interface LoginSuccessListener {
        void onLoginSuccess();
    }

    public LoginPanel(AuthenticationService authenticationService, LoginSuccessListener successListener) {
        this.authenticationService = authenticationService;
        this.successListener = successListener;

        setLayout(new GridBagLayout());
        setBackground(ThemeManager.APP_BG);

        initUI();
    }

    private void initUI() {
        JPanel card = new JPanel();
        card.setLayout(new GridBagLayout());
        ThemeManager.styleCardPanel(card);
        card.setPreferredSize(new Dimension(460, 480));
        card.setMinimumSize(new Dimension(420, 450));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        // System Title
        JLabel titleLabel = new JLabel("Online Examination System", SwingConstants.CENTER);
        titleLabel.setFont(ThemeManager.FONT_TITLE);
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        card.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Sign in to access your examination panel", SwingConstants.CENTER);
        subtitleLabel.setFont(ThemeManager.FONT_BODY);
        subtitleLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        card.add(subtitleLabel, gbc);

        gbc.gridy++;
        card.add(Box.createVerticalStrut(15), gbc);

        // Error Message Banner (Hidden by default)
        gbc.gridy++;
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(ThemeManager.FONT_BODY_BOLD);
        errorLabel.setForeground(ThemeManager.DANGER_COLOR);
        errorLabel.setVisible(false);
        card.add(errorLabel, gbc);

        // Username Field
        gbc.gridy++;
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(ThemeManager.FONT_HEADER);
        userLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        card.add(userLabel, gbc);

        gbc.gridy++;
        usernameField = new JTextField(20);
        ThemeManager.styleTextField(usernameField);
        usernameField.addActionListener(e -> performLogin());
        card.add(usernameField, gbc);

        // Password Field
        gbc.gridy++;
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(ThemeManager.FONT_HEADER);
        passLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        card.add(passLabel, gbc);

        gbc.gridy++;
        passwordField = new JPasswordField(20);
        ThemeManager.stylePasswordField(passwordField);
        passwordField.addActionListener(e -> performLogin());
        card.add(passwordField, gbc);

        gbc.gridy++;
        card.add(Box.createVerticalStrut(10), gbc);

        // Login Button
        gbc.gridy++;
        JButton loginButton = new JButton("Login");
        ThemeManager.stylePrimaryButton(loginButton);
        loginButton.setPreferredSize(new Dimension(0, 42));
        loginButton.addActionListener(e -> performLogin());
        card.add(loginButton, gbc);

        // Sample Account Hint
        gbc.gridy++;
        JLabel hintLabel = new JLabel("Default Login: student1 / password123", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        card.add(hintLabel, gbc);

        add(card);
    }

    private void performLogin() {
        errorLabel.setVisible(false);
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (ValidationUtil.isEmpty(username)) {
            showError("Please enter your username.");
            usernameField.requestFocusInWindow();
            return;
        }

        if (ValidationUtil.isEmpty(password)) {
            showError("Please enter your password.");
            passwordField.requestFocusInWindow();
            return;
        }

        boolean success = authenticationService.login(username, password);
        if (success) {
            clearFields();
            if (successListener != null) {
                successListener.onLoginSuccess();
            }
        } else {
            showError("Invalid username or password. Please try again.");
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        errorLabel.setVisible(false);
    }
}
