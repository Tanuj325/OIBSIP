package com.oasis.exam.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Manages application themes, typography, color palettes, and component styling.
 */
public class ThemeManager {

    // Modern High-Contrast Color Palette
    public static final Color PRIMARY_COLOR = new Color(59, 130, 246);      // Vibrant Blue
    public static final Color PRIMARY_HOVER = new Color(37, 99, 235);
    public static final Color SUCCESS_COLOR = new Color(34, 197, 94);      // Emerald Green
    public static final Color WARNING_COLOR = new Color(245, 158, 11);     // Amber Warning
    public static final Color DANGER_COLOR = new Color(239, 68, 68);       // Bright Red
    public static final Color CARD_BG = new Color(30, 41, 59);            // Dark Slate Card
    public static final Color APP_BG = new Color(15, 23, 42);              // Midnight Deep Dark
    public static final Color HEADER_BG = new Color(15, 23, 42);
    public static final Color TEXT_PRIMARY = new Color(248, 250, 252);     // Pure Crisp White
    public static final Color TEXT_SECONDARY = new Color(148, 163, 184);   // Muted Slate
    public static final Color BORDER_COLOR = new Color(51, 65, 85);

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TIMER = new Font("Monospaced", Font.BOLD, 22);

    private ThemeManager() {
    }

    public static void initializeTheme() {
        try {
            FlatDarkLaf.setup();
            UIManager.put("Component.arc", 12);
            UIManager.put("Button.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("ScrollBar.thumbArc", 10);
            UIManager.put("ScrollBar.width", 12);
            UIManager.put("Table.rowHeight", 32);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", true);
            UIManager.put("Table.intercellSpacing", new Dimension(1, 1));
            UIManager.put("Table.gridColor", BORDER_COLOR);
            UIManager.put("TableHeader.font", FONT_HEADER);
            UIManager.put("TableHeader.background", CARD_BG);
            UIManager.put("TableHeader.foreground", TEXT_PRIMARY);
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf theme: " + e.getMessage());
        }
    }

    public static void styleCardPanel(JPanel panel) {
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(24, 28, 24, 28)
        ));
    }

    public static void stylePrimaryButton(JButton button) {
        button.setFont(FONT_BODY_BOLD);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty(FlatClientProperties.STYLE, "hoverBackground: #2563eb; pressedBackground: #1d4ed8;");
    }

    public static void styleSecondaryButton(JButton button) {
        button.setFont(FONT_BODY_BOLD);
        button.setBackground(CARD_BG);
        button.setForeground(TEXT_PRIMARY);
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty(FlatClientProperties.STYLE, "hoverBackground: #334155;");
    }

    public static void styleDangerButton(JButton button) {
        button.setFont(FONT_BODY_BOLD);
        button.setBackground(DANGER_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty(FlatClientProperties.STYLE, "hoverBackground: #dc2626; pressedBackground: #b91c1c;");
    }

    public static void styleSuccessButton(JButton button) {
        button.setFont(FONT_BODY_BOLD);
        button.setBackground(SUCCESS_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty(FlatClientProperties.STYLE, "hoverBackground: #16a34a; pressedBackground: #15803d;");
    }

    public static void styleTextField(JTextField textField) {
        textField.setFont(FONT_BODY);
        textField.setCaretColor(TEXT_PRIMARY);
        textField.putClientProperty(FlatClientProperties.STYLE, "margin: 6,10,6,10;");
    }

    public static void stylePasswordField(JPasswordField passwordField) {
        passwordField.setFont(FONT_BODY);
        passwordField.setCaretColor(TEXT_PRIMARY);
        passwordField.putClientProperty(FlatClientProperties.STYLE, "margin: 6,10,6,10;");
        passwordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");
    }
}
