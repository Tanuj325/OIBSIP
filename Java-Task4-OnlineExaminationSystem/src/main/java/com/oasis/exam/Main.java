package com.oasis.exam;

import com.oasis.exam.ui.MainFrame;
import com.oasis.exam.ui.ThemeManager;

import javax.swing.*;

/**
 * Main application entry point for the Online Examination System.
 */
public class Main {
    public static void main(String[] args) {
        // Initialize Look and Feel
        ThemeManager.initializeTheme();

        // Launch GUI on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(true);
        });
    }
}
