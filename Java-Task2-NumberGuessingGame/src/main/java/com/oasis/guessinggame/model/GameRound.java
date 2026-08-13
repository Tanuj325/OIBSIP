package com.oasis.guessinggame.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model representing an active or completed round in the Number Guessing Game.
 */
public class GameRound {

    public enum Status {
        IN_PROGRESS,
        WON,
        LOST
    }

    private final int roundNumber;
    private final Difficulty difficulty;
    private final int targetNumber;
    private final LocalDateTime startTime;
    private final List<Integer> guesses;

    private Status status;
    private int minHint;
    private int maxHint;
    private LocalDateTime endTime;

    public GameRound(int roundNumber, Difficulty difficulty, int targetNumber) {
        this.roundNumber = roundNumber;
        this.difficulty = difficulty;
        this.targetNumber = targetNumber;
        this.startTime = LocalDateTime.now();
        this.guesses = new ArrayList<>();
        this.status = Status.IN_PROGRESS;
        this.minHint = difficulty.getMinNumber();
        this.maxHint = difficulty.getMaxNumber();
    }

    public void addGuess(int guess) {
        guesses.add(guess);
        if (guess < targetNumber) {
            minHint = Math.max(minHint, guess + 1);
        } else if (guess > targetNumber) {
            maxHint = Math.min(maxHint, guess - 1);
        }
    }

    public void completeRound(boolean won) {
        this.status = won ? Status.WON : Status.LOST;
        this.endTime = LocalDateTime.now();
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getTargetNumber() {
        return targetNumber;
    }

    public List<Integer> getGuesses() {
        return Collections.unmodifiableList(guesses);
    }

    public int getAttemptsUsed() {
        return guesses.size();
    }

    public int getAttemptsRemaining() {
        return difficulty.getMaxAttempts() - guesses.size();
    }

    public Status getStatus() {
        return status;
    }

    public int getMinHint() {
        return minHint;
    }

    public int getMaxHint() {
        return maxHint;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Generates a clean summary string for round history logging.
     * e.g., "Round 1 - guessed in 4 attempts (Medium)"
     * or "Round 3 - You Lost - number was 87 (Hard)"
     */
    public String getSummaryText() {
        if (status == Status.WON) {
            return String.format("Round %d — Guessed in %d attempt%s (%s)",
                    roundNumber, getAttemptsUsed(), getAttemptsUsed() == 1 ? "" : "s", difficulty.getDisplayName());
        } else if (status == Status.LOST) {
            return String.format("Round %d — You Lost — Target was %d (%s)",
                    roundNumber, targetNumber, difficulty.getDisplayName());
        } else {
            return String.format("Round %d — In Progress (%s)", roundNumber, difficulty.getDisplayName());
        }
    }

    public String getFormattedTime() {
        LocalDateTime time = endTime != null ? endTime : startTime;
        return time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
