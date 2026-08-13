package com.oasis.guessinggame.model;

/**
 * Data container representing feedback provided to the player after an evaluation.
 */
public class GuessFeedback {

    private final boolean valid;
    private final int guessValue;
    private final GuessResult result;
    private final String message;
    private final int attemptsUsed;
    private final int attemptsRemaining;
    private final int minHint;
    private final int maxHint;
    private final boolean roundFinished;
    private final boolean won;
    private final Integer targetRevealed;

    public GuessFeedback(boolean valid, int guessValue, GuessResult result, String message,
                         int attemptsUsed, int attemptsRemaining, int minHint, int maxHint,
                         boolean roundFinished, boolean won, Integer targetRevealed) {
        this.valid = valid;
        this.guessValue = guessValue;
        this.result = result;
        this.message = message;
        this.attemptsUsed = attemptsUsed;
        this.attemptsRemaining = attemptsRemaining;
        this.minHint = minHint;
        this.maxHint = maxHint;
        this.roundFinished = roundFinished;
        this.won = won;
        this.targetRevealed = targetRevealed;
    }

    public static GuessFeedback invalid(String message, int attemptsUsed, int attemptsRemaining, int minHint, int maxHint) {
        return new GuessFeedback(false, -1, GuessResult.INVALID_FORMAT, message,
                attemptsUsed, attemptsRemaining, minHint, maxHint, false, false, null);
    }

    public static GuessFeedback outOfBounds(int guess, String message, int attemptsUsed, int attemptsRemaining, int minHint, int maxHint) {
        return new GuessFeedback(false, guess, GuessResult.OUT_OF_BOUNDS, message,
                attemptsUsed, attemptsRemaining, minHint, maxHint, false, false, null);
    }

    public boolean isValid() {
        return valid;
    }

    public int getGuessValue() {
        return guessValue;
    }

    public GuessResult getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public int getAttemptsUsed() {
        return attemptsUsed;
    }

    public int getAttemptsRemaining() {
        return attemptsRemaining;
    }

    public int getMinHint() {
        return minHint;
    }

    public int getMaxHint() {
        return maxHint;
    }

    public boolean isRoundFinished() {
        return roundFinished;
    }

    public boolean isWon() {
        return won;
    }

    public Integer getTargetRevealed() {
        return targetRevealed;
    }
}
