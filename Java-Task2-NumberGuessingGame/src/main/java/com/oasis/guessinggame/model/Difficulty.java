package com.oasis.guessinggame.model;

/**
 * Represents difficulty configurations for the Number Guessing Game.
 */
public enum Difficulty {
    EASY("Easy", 1, 50, 10, "1 - 50", "10 Attempts"),
    MEDIUM("Medium", 1, 100, 7, "1 - 100", "7 Attempts"),
    HARD("Hard", 1, 200, 5, "1 - 200", "5 Attempts");

    private final String displayName;
    private final int minNumber;
    private final int maxNumber;
    private final int maxAttempts;
    private final String rangeText;
    private final String attemptsText;

    Difficulty(String displayName, int minNumber, int maxNumber, int maxAttempts, String rangeText, String attemptsText) {
        this.displayName = displayName;
        this.minNumber = minNumber;
        this.maxNumber = maxNumber;
        this.maxAttempts = maxAttempts;
        this.rangeText = rangeText;
        this.attemptsText = attemptsText;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinNumber() {
        return minNumber;
    }

    public int getMaxNumber() {
        return maxNumber;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public String getRangeText() {
        return rangeText;
    }

    public String getAttemptsText() {
        return attemptsText;
    }

    public boolean isInRange(int guess) {
        return guess >= minNumber && guess <= maxNumber;
    }

    @Override
    public String toString() {
        return displayName + " (" + rangeText + ", " + maxAttempts + " attempts)";
    }
}
