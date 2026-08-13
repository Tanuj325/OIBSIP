package com.oasis.ui;

import com.oasis.model.User;
import com.oasis.service.AuthenticationService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Modern Login Screen for Online Reservation System.
 */
public class LoginFrame extends JFrame {

    private final AuthenticationService authService;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;

    public LoginFrame() {
        this.authService = new AuthenticationService();
        initUI();
    }

    private void initUI() {
        setTitle("Online Reservation System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel rootPanel = new JPanel(new GridBagLayout());
        rootPanel.setBackground(new Color(28, 33, 40)); // Dark sleek background

        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(new Color(37, 43, 54));
        cardPanel.setBorder(new EmptyBorder(30, 35, 30, 35));

        // Header Title
        JLabel titleLabel = new JLabel("Online Reservation System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(240, 246, 252));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sign in to manage your transport bookings");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(139, 148, 158));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        cardPanel.add(titleLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        cardPanel.add(subtitleLabel);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Form Fields
        JPanel formGrid = new JPanel(new GridLayout(4, 1, 0, 8));
        formGrid.setOpaque(false);

        JLabel lblUsername = new JLabel("Username");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUsername.setForeground(new Color(201, 209, 217));

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(300, 36));

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPassword.setForeground(new Color(201, 209, 217));

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(300, 36));

        formGrid.add(lblUsername);
        formGrid.add(txtUsername);
        formGrid.add(lblPassword);
        formGrid.add(txtPassword);

        cardPanel.add(formGrid);
        cardPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);

        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(0, 38));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnExit = new JButton("Exit");
        btnExit.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnExit.setPreferredSize(new Dimension(0, 38));
        btnExit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        cardPanel.add(buttonPanel);

        rootPanel.add(cardPanel);
        add(rootPanel);

        // Events
        btnLogin.addActionListener(e -> performLogin());
        btnExit.addActionListener(e -> System.exit(0));

        KeyAdapter enterAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterAdapter);
        txtPassword.addKeyListener(enterAdapter);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username and Password fields cannot be empty.",
                    "Authentication Required",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        btnLogin.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            User user = authService.login(username, password);

            if (user != null) {
                // Open Full-Screen Main Desktop Application Frame
                MainFrame mainFrame = new MainFrame(user);
                mainFrame.setVisible(true);

                // Close Login Window
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid username or password.",
                        "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtPassword.requestFocusInWindow();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Database / System Error: " + ex.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            btnLogin.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }
    }
}
