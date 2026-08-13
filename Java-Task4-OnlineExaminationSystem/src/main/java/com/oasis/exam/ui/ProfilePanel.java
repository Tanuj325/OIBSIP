package com.oasis.exam.ui;

import com.oasis.exam.model.User;
import com.oasis.exam.service.AuthenticationService;
import com.oasis.exam.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Professional profile confirmation & update screen with distinct sections,
 * optional password modification, and session profile completion state.
 */
public class ProfilePanel extends JPanel {
    private final AuthenticationService authenticationService;
    private final ProfileActionListener profileActionListener;

    private JTextField usernameField;
    private JTextField displayNameField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel errorLabel;

    public interface ProfileActionListener {
        void onProfileSavedAndContinued();
        void onLogoutRequested();
    }

    public ProfilePanel(AuthenticationService authenticationService, ProfileActionListener profileActionListener) {
        this.authenticationService = authenticationService;
        this.profileActionListener = profileActionListener;

        setLayout(new GridBagLayout());
        setBackground(ThemeManager.APP_BG);

        initUI();
    }

    private void initUI() {
        JPanel card = new JPanel();
        card.setLayout(new GridBagLayout());
        ThemeManager.styleCardPanel(card);
        card.setPreferredSize(new Dimension(560, 620));
        card.setMinimumSize(new Dimension(500, 560));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 15, 6, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        // Header Title
        JLabel titleLabel = new JLabel("User Profile & Security", SwingConstants.CENTER);
        titleLabel.setFont(ThemeManager.FONT_TITLE);
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        card.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel subtitleLabel = new JLabel("Confirm your account profile details before beginning the examination", SwingConstants.CENTER);
        subtitleLabel.setFont(ThemeManager.FONT_BODY);
        subtitleLabel.setForeground(ThemeManager.TEXT_SECONDARY);
        card.add(subtitleLabel, gbc);

        gbc.gridy++;
        card.add(Box.createVerticalStrut(6), gbc);

        // Error message banner
        gbc.gridy++;
        errorLabel = new JLabel("", SwingConstants.CENTER);
        errorLabel.setFont(ThemeManager.FONT_BODY_BOLD);
        errorLabel.setForeground(ThemeManager.DANGER_COLOR);
        errorLabel.setVisible(false);
        card.add(errorLabel, gbc);

        // SECTION 1: ACCOUNT INFORMATION
        gbc.gridy++;
        card.add(createSectionHeader("Account Information"), gbc);

        gbc.gridy++;
        JLabel userLabel = new JLabel("Username (Read-Only)");
        userLabel.setFont(ThemeManager.FONT_HEADER);
        userLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        card.add(userLabel, gbc);

        gbc.gridy++;
        usernameField = new JTextField(20);
        ThemeManager.styleTextField(usernameField);
        usernameField.setEditable(false);
        usernameField.setFocusable(false);
        usernameField.setBackground(new Color(24, 32, 47));
        card.add(usernameField, gbc);

        // SECTION 2: PERSONAL INFORMATION
        gbc.gridy++;
        card.add(createSectionHeader("Personal Information"), gbc);

        gbc.gridy++;
        JLabel nameLabel = new JLabel("Display Name");
        nameLabel.setFont(ThemeManager.FONT_HEADER);
        nameLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        card.add(nameLabel, gbc);

        gbc.gridy++;
        displayNameField = new JTextField(20);
        ThemeManager.styleTextField(displayNameField);
        card.add(displayNameField, gbc);

        // SECTION 3: SECURITY (OPTIONAL PASSWORD)
        gbc.gridy++;
        card.add(createSectionHeader("Security (Optional Password Update)"), gbc);

        gbc.gridy++;
        JLabel noteLabel = new JLabel("Note: Leave password fields empty if you do not wish to change your password.");
        noteLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        noteLabel.setForeground(ThemeManager.PRIMARY_COLOR);
        card.add(noteLabel, gbc);

        gbc.gridy++;
        JLabel passLabel = new JLabel("New Password");
        passLabel.setFont(ThemeManager.FONT_HEADER);
        passLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        card.add(passLabel, gbc);

        gbc.gridy++;
        newPasswordField = new JPasswordField(20);
        ThemeManager.stylePasswordField(newPasswordField);
        card.add(newPasswordField, gbc);

        gbc.gridy++;
        JLabel confirmPassLabel = new JLabel("Confirm New Password");
        confirmPassLabel.setFont(ThemeManager.FONT_HEADER);
        confirmPassLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        card.add(confirmPassLabel, gbc);

        gbc.gridy++;
        confirmPasswordField = new JPasswordField(20);
        ThemeManager.stylePasswordField(confirmPasswordField);
        card.add(confirmPasswordField, gbc);

        gbc.gridy++;
        card.add(Box.createVerticalStrut(10), gbc);

        // Action Buttons Row
        gbc.gridy++;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);

        JButton logoutBtn = new JButton("Logout");
        ThemeManager.styleSecondaryButton(logoutBtn);
        logoutBtn.setPreferredSize(new Dimension(0, 42));
        logoutBtn.addActionListener(e -> {
            authenticationService.logout();
            if (profileActionListener != null) {
                profileActionListener.onLogoutRequested();
            }
        });
        buttonPanel.add(logoutBtn);

        JButton saveContinueBtn = new JButton("Save & Continue");
        ThemeManager.stylePrimaryButton(saveContinueBtn);
        saveContinueBtn.setPreferredSize(new Dimension(0, 42));
        saveContinueBtn.addActionListener(e -> saveAndContinue());
        buttonPanel.add(saveContinueBtn);

        card.add(buttonPanel, gbc);

        add(card);
    }

    private JPanel createSectionHeader(String text) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(6, 0, 4, 0));

        JLabel l = new JLabel(text);
        l.setFont(ThemeManager.FONT_SUBTITLE);
        l.setForeground(ThemeManager.PRIMARY_COLOR);
        p.add(l, BorderLayout.WEST);

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(ThemeManager.BORDER_COLOR);
        p.add(sep, BorderLayout.SOUTH);

        return p;
    }

    public void loadUserProfile() {
        errorLabel.setVisible(false);
        if (authenticationService.getSession().isLoggedIn()) {
            User user = authenticationService.getSession().getCurrentUser();
            usernameField.setText(user.getUsername());
            displayNameField.setText(user.getDisplayName());
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        }
    }

    private void saveAndContinue() {
        errorLabel.setVisible(false);
        String displayName = displayNameField.getText();
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (ValidationUtil.isEmpty(displayName)) {
            showError("Display Name cannot be empty.");
            displayNameField.requestFocusInWindow();
            return;
        }

        if (!ValidationUtil.isEmpty(newPassword)) {
            if (ValidationUtil.isEmpty(confirmPassword)) {
                showError("Please confirm your new password.");
                confirmPasswordField.requestFocusInWindow();
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                showError("New Password and Confirm Password do not match.");
                confirmPasswordField.requestFocusInWindow();
                return;
            }
        }

        try {
            boolean updated = authenticationService.updateProfile(displayName, newPassword, confirmPassword);
            if (updated) {
                // Mark profile as completed for this session so it does not loop
                authenticationService.getSession().setProfileCompleted(true);
                if (profileActionListener != null) {
                    profileActionListener.onProfileSavedAndContinued();
                }
            } else {
                showError("Failed to update profile. Please try again.");
            }
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
