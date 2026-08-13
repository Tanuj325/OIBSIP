package com.oasis.exam.util;

/**
 * Utility methods for string and field validation.
 */
public class ValidationUtil {

    private ValidationUtil() {
        // Utility class
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isSameText(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.trim().equals(str2.trim());
    }

    public static String safeTrim(String str) {
        return str == null ? "" : str.trim();
    }
}
