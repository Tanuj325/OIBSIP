package com.oasis;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.oasis.config.DatabaseConnection;
import com.oasis.ui.LoginFrame;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Entry point for the Online Reservation System Application.
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Set Look and Feel (FlatLaf Dark Theme with fallback to System Look and Feel)
        try {
            FlatDarkLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Failed to initialize FlatLaf look and feel. Falling back to system look and feel.", ex);
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        // Test MySQL Database Connectivity
        boolean dbConnected = DatabaseConnection.testConnection();
        if (dbConnected) {
            LOGGER.info("Successfully connected to MySQL database 'online_reservation'.");
        } else {
            LOGGER.warning("Could not verify MySQL connection on startup. Ensure MySQL server is running.");
        }

        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
