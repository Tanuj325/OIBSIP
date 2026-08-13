package com.oasis.guessinggame.service;

import com.oasis.guessinggame.model.Difficulty;
import com.oasis.guessinggame.model.GameRound;
import com.oasis.guessinggame.model.GameStatistics;
import com.oasis.guessinggame.model.GuessFeedback;
import com.oasis.guessinggame.model.GuessResult;

import java.util.Random;

/**
 * Core game engine service managing target number generation, input validation,
 * guess evaluation, and round transitions.
 */
public class NumberGuessingGame {

    private final Random random;
    private final GameStatistics statistics;

    private Difficulty currentDifficulty;
    private GameRound currentRound;
    private int roundCounter;

    public NumberGuessingGame() {
        this(Difficulty.MEDIUM);
    }

    public NumberGuessingGame(Difficulty initialDifficulty) {
        this.random = new Random();
        this.statistics = new GameStatistics();
        this.currentDifficulty = initialDifficulty != null ? initialDifficulty : Difficulty.MEDIUM;
        this.roundCounter = 0;
        startNewRound(this.currentDifficulty);
    }

    /**
     * Starts a new round with specified difficulty level.
     * Generates a new random target within [min, max] range.
     */
    public synchronized GameRound startNewRound(Difficulty difficulty) {
        if (difficulty != null) {
            this.currentDifficulty = difficulty;
        }

        this.roundCounter++;
        int min = currentDifficulty.getMinNumber();
        int max = currentDifficulty.getMaxNumber();

        // Generate target in [min, max] inclusive using Random
        int target = random.nextInt((max - min) + 1) + min;

        this.currentRound = new GameRound(roundCounter, currentDifficulty, target);
        return currentRound;
    }

    /**
     * Evaluates a user input string as a guess.
     * Validates numeric input and range boundaries before consuming an attempt.
     */
    public synchronized GuessFeedback processGuess(String rawInput) {
        if (currentRound == null || currentRound.getStatus() != GameRound.Status.IN_PROGRESS) {
            return GuessFeedback.invalid("Round is not in progress. Start a new round.",
                    0, 0, currentDifficulty.getMinNumber(), currentDifficulty.getMaxNumber());
        }

        // 1. Check empty input
        if (rawInput == null || rawInput.trim().isEmpty()) {
            return GuessFeedback.invalid("Please enter a guess before submitting.",
                    currentRound.getAttemptsUsed(), currentRound.getAttemptsRemaining(),
                    currentRound.getMinHint(), currentRound.getMaxHint());
        }

        String input = rawInput.trim();

        // 2. Check decimal point
        if (input.contains(".")) {
            return GuessFeedback.invalid("Decimal numbers are not allowed. Enter a whole number.",
                    currentRound.getAttemptsUsed(), currentRound.getAttemptsRemaining(),
                    currentRound.getMinHint(), currentRound.getMaxHint());
        }

        // 3. Parse integer
        int guess;
        try {
            guess = Integer.parseInt(input);
        } catch (NumberFormatException ex) {
            return GuessFeedback.invalid("Letters or special characters are not allowed.",
                    currentRound.getAttemptsUsed(), currentRound.getAttemptsRemaining(),
                    currentRound.getMinHint(), currentRound.getMaxHint());
        }

        // 4. Validate range
        if (!currentDifficulty.isInRange(guess)) {
            String errorMsg = String.format("Out of range! Please enter a number between %d and %d.",
                    currentDifficulty.getMinNumber(), currentDifficulty.getMaxNumber());
            return GuessFeedback.outOfBounds(guess, errorMsg,
                    currentRound.getAttemptsUsed(), currentRound.getAttemptsRemaining(),
                    currentRound.getMinHint(), currentRound.getMaxHint());
        }

        // 5. Valid Guess -> Record guess in round (increments attempts)
        currentRound.addGuess(guess);
        int target = currentRound.getTargetNumber();
        int attemptsUsed = currentRound.getAttemptsUsed();
        int attemptsRemaining = currentRound.getAttemptsRemaining();
        int minHint = currentRound.getMinHint();
        int maxHint = currentRound.getMaxHint();

        // 6. Check Win Condition
        if (guess == target) {
            currentRound.completeRound(true);
            statistics.recordRound(currentRound);
            String winMessage = String.format("🎉 Correct! You guessed the number in %d attempt%s.",
                    attemptsUsed, attemptsUsed == 1 ? "" : "s");
            return new GuessFeedback(true, guess, GuessResult.CORRECT, winMessage,
                    attemptsUsed, attemptsRemaining, minHint, maxHint, true, true, target);
        }

        // 7. Check Loss Condition (Exhausted attempts)
        if (attemptsRemaining <= 0) {
            currentRound.completeRound(false);
            statistics.recordRound(currentRound);
            String lossMessage = String.format("You Lost! The correct number was %d.", target);
            return new GuessFeedback(true, guess, GuessResult.EXHAUSTED, lossMessage,
                    attemptsUsed, 0, minHint, maxHint, true, false, target);
        }

        // 8. Too High or Too Low
        if (guess > target) {
            String highMessage = String.format("Too High! Attempt %d of %d.",
                    attemptsUsed, currentDifficulty.getMaxAttempts());
            return new GuessFeedback(true, guess, GuessResult.TOO_HIGH, highMessage,
                    attemptsUsed, attemptsRemaining, minHint, maxHint, false, false, null);
        } else {
            String lowMessage = String.format("Too Low! Attempt %d of %d.",
                    attemptsUsed, currentDifficulty.getMaxAttempts());
            return new GuessFeedback(true, guess, GuessResult.TOO_LOW, lowMessage,
                    attemptsUsed, attemptsRemaining, minHint, maxHint, false, false, null);
        }
    }

    public synchronized void resetScore() {
        statistics.resetAll();
        startNewRound(currentDifficulty);
    }

    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    public GameRound getCurrentRound() {
        return currentRound;
    }

    public GameStatistics getStatistics() {
        return statistics;
    }
}
