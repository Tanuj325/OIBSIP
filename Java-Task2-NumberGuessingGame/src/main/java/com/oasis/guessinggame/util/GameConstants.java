package com.oasis.guessinggame.util;

import java.awt.*;

/**
 * Styling design system and color palette constants for the Number Guessing Game.
 */
public final class GameConstants {

    private GameConstants() {}

    // Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_CARD_LABEL = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_CARD_VALUE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_INPUT = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_RESULT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HINT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 14);

    // Color Palette (Modern Dark & Vibrant High Contrast)
    public static final Color BG_DARK = new Color(24, 25, 38);         // #181926 Main App Background
    public static final Color BG_CARD = new Color(36, 39, 58);         // #24273A Card Surface
    public static final Color BG_CARD_HOVER = new Color(49, 53, 77);   // Card Hover state
    public static final Color BG_INPUT = new Color(30, 32, 48);        // Input Field Background
    public static final Color BORDER_COLOR = new Color(69, 75, 110);   // Surface Borders

    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(240, 244, 248);  // Bright Crisp White
    public static final Color TEXT_SECONDARY = new Color(175, 184, 201); // Readable Soft Blue-Gray
    public static final Color TEXT_MUTED = new Color(110, 120, 145);    // Muted Gray

    // Action Colors
    public static final Color ACCENT_PRIMARY = new Color(138, 92, 246); // Purple Primary Accent
    public static final Color ACCENT_HOVER = new Color(124, 58, 237);   // Darker Accent
    public static final Color COLOR_EASY = new Color(34, 197, 94);      // Green
    public static final Color COLOR_MEDIUM = new Color(245, 158, 11);    // Amber
    public static final Color COLOR_HARD = new Color(239, 68, 68);      // Coral Red

    // Result Banner Colors
    public static final Color COLOR_TOO_HIGH = new Color(245, 158, 11); // Amber Banner
    public static final Color COLOR_TOO_LOW = new Color(59, 130, 246);  // Blue Banner
    public static final Color COLOR_CORRECT = new Color(34, 197, 94);   // Green Banner
    public static final Color COLOR_LOST = new Color(239, 68, 68);      // Red Banner
    public static final Color COLOR_ERROR = new Color(239, 68, 68);     // Error Badge

    // Progress Bar Colors
    public static final Color PROGRESS_HIGH = new Color(34, 197, 94);
    public static final Color PROGRESS_MID = new Color(245, 158, 11);
    public static final Color PROGRESS_LOW = new Color(239, 68, 68);
}
