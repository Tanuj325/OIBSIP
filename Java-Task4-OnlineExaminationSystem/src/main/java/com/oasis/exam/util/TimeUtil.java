package com.oasis.exam.util;

/**
 * Utility methods for duration formatting and time calculations.
 */
public class TimeUtil {

    private TimeUtil() {
        // Utility class
    }

    /**
     * Formats seconds into MM:SS format.
     */
    public static String formatMMSS(long totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Formats elapsed duration into readable text e.g., "18 minutes 42 seconds".
     */
    public static String formatDurationText(long totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        if (minutes == 0) {
            return seconds + (seconds == 1 ? " second" : " seconds");
        } else if (seconds == 0) {
            return minutes + (minutes == 1 ? " minute" : " minutes");
        } else {
            return String.format("%d %s %d %s",
                    minutes, minutes == 1 ? "minute" : "minutes",
                    seconds, seconds == 1 ? "second" : "seconds");
        }
    }
}
