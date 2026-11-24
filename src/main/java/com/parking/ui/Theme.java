package com.parking.ui;

import java.awt.Color;
import java.awt.Font;

public final class Theme {

    private Theme() {
        // Utility class
    }

    private static boolean darkMode = false;

    // Light theme
    private static final Color LIGHT_BACKGROUND =
            new Color(245, 248, 250);

    private static final Color LIGHT_SURFACE =
            Color.WHITE;

    private static final Color LIGHT_SIDEBAR =
            new Color(20, 38, 55);

    private static final Color LIGHT_TEXT =
            new Color(28, 43, 56);

    private static final Color LIGHT_MUTED =
            new Color(105, 122, 137);

    private static final Color LIGHT_BORDER =
            new Color(225, 233, 238);

    // Dark theme
    private static final Color DARK_BACKGROUND =
            new Color(15, 23, 33);

    private static final Color DARK_SURFACE =
            new Color(25, 36, 49);

    private static final Color DARK_SIDEBAR =
            new Color(10, 18, 28);

    private static final Color DARK_TEXT =
            new Color(235, 243, 247);

    private static final Color DARK_MUTED =
            new Color(157, 174, 188);

    private static final Color DARK_BORDER =
            new Color(48, 65, 80);

    // Shared accent colors
    public static final Color ACCENT =
            new Color(0, 150, 145);

    public static final Color ACCENT_HOVER =
            new Color(0, 125, 122);

    public static final Color SUCCESS =
            new Color(34, 166, 115);

    public static final Color WARNING =
            new Color(225, 160, 55);

    public static final Color DANGER =
            new Color(220, 80, 90);

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void toggle() {
        darkMode = !darkMode;
    }

    public static void setDarkMode(boolean enabled) {
        darkMode = enabled;
    }

    public static Color background() {
        return darkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }

    public static Color surface() {
        return darkMode ? DARK_SURFACE : LIGHT_SURFACE;
    }

    public static Color sidebar() {
        return darkMode ? DARK_SIDEBAR : LIGHT_SIDEBAR;
    }

    public static Color text() {
        return darkMode ? DARK_TEXT : LIGHT_TEXT;
    }

    public static Color muted() {
        return darkMode ? DARK_MUTED : LIGHT_MUTED;
    }

    public static Color border() {
        return darkMode ? DARK_BORDER : LIGHT_BORDER;
    }

    public static Font headingFont() {
        return new Font("SansSerif", Font.BOLD, 22);
    }

    public static Font bodyFont() {
        return new Font("SansSerif", Font.PLAIN, 14);
    }

    public static Font smallFont() {
        return new Font("SansSerif", Font.PLAIN, 12);
    }
}