package com.oasis.guessinggame;

import com.oasis.guessinggame.model.Difficulty;
import com.oasis.guessinggame.model.GameRound;
import com.oasis.guessinggame.model.GameStatistics;
import com.oasis.guessinggame.model.GuessFeedback;
import com.oasis.guessinggame.model.GuessResult;
import com.oasis.guessinggame.service.NumberGuessingGame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated JUnit 5 unit test suite for core game engine logic, target generation,
 * input validation, win/loss state transitions, and score statistics tracking.
 */
class NumberGuessingGameTest {

    private NumberGuessingGame game;

    @BeforeEach
    void setUp() {
        game = new NumberGuessingGame(Difficulty.MEDIUM);
    }

    @Test
    @DisplayName("Test difficulty configurations match requirement specifications")
    void testDifficultyConfigurations() {
        assertEquals(1, Difficulty.EASY.getMinNumber());
        assertEquals(50, Difficulty.EASY.getMaxNumber());
        assertEquals(10, Difficulty.EASY.getMaxAttempts());

        assertEquals(1, Difficulty.MEDIUM.getMinNumber());
        assertEquals(100, Difficulty.MEDIUM.getMaxNumber());
        assertEquals(7, Difficulty.MEDIUM.getMaxAttempts());

        assertEquals(1, Difficulty.HARD.getMinNumber());
        assertEquals(200, Difficulty.HARD.getMaxNumber());
        assertEquals(5, Difficulty.HARD.getMaxAttempts());
    }

    @Test
    @DisplayName("Test target number is generated within valid difficulty range")
    void testRandomTargetGenerationBounds() {
        for (Difficulty diff : Difficulty.values()) {
            GameRound round = game.startNewRound(diff);
            int target = round.getTargetNumber();
            assertTrue(target >= diff.getMinNumber() && target <= diff.getMaxNumber(),
                    "Target " + target + " is outside " + diff.getDisplayName() + " bounds");
        }
    }

    @Test
    @DisplayName("Test empty or blank input shows error and does not consume attempt")
    void testEmptyInputValidation() {
        GameRound round = game.getCurrentRound();
        int attemptsBefore = round.getAttemptsUsed();

        GuessFeedback fb1 = game.processGuess("");
        assertFalse(fb1.isValid());
        assertEquals(GuessResult.INVALID_FORMAT, fb1.getResult());
        assertEquals(attemptsBefore, round.getAttemptsUsed(), "Attempts should not increase for empty input");

        GuessFeedback fb2 = game.processGuess("   ");
        assertFalse(fb2.isValid());
        assertEquals(attemptsBefore, round.getAttemptsUsed(), "Attempts should not increase for whitespace input");
    }

    @Test
    @DisplayName("Test non-numeric or decimal input shows error and does not consume attempt")
    void testNonNumericValidation() {
        GameRound round = game.getCurrentRound();
        int attemptsBefore = round.getAttemptsUsed();

        GuessFeedback fbLetters = game.processGuess("abc");
        assertFalse(fbLetters.isValid());
        assertEquals(attemptsBefore, round.getAttemptsUsed(), "Letters input should not consume attempt");

        GuessFeedback fbDecimal = game.processGuess("42.5");
        assertFalse(fbDecimal.isValid());
        assertEquals(attemptsBefore, round.getAttemptsUsed(), "Decimal input should not consume attempt");
    }

    @Test
    @DisplayName("Test out of range numbers show error and do not consume attempt")
    void testOutOfRangeValidation() {
        game.startNewRound(Difficulty.EASY); // 1-50
        GameRound round = game.getCurrentRound();
        int attemptsBefore = round.getAttemptsUsed();

        GuessFeedback fbZero = game.processGuess("0");
        assertFalse(fbZero.isValid());
        assertEquals(GuessResult.OUT_OF_BOUNDS, fbZero.getResult());
        assertEquals(attemptsBefore, round.getAttemptsUsed());

        GuessFeedback fbHigh = game.processGuess("51");
        assertFalse(fbHigh.isValid());
        assertEquals(GuessResult.OUT_OF_BOUNDS, fbHigh.getResult());
        assertEquals(attemptsBefore, round.getAttemptsUsed());
    }

    @Test
    @DisplayName("Test valid guess increments attempt counter and updates range hints")
    void testValidGuessEvaluation() {
        game.startNewRound(Difficulty.MEDIUM); // 1-100
        GameRound round = game.getCurrentRound();
        int target = round.getTargetNumber();

        if (target > 1) {
            GuessFeedback fb = game.processGuess(String.valueOf(1));
            assertTrue(fb.isValid());
            assertEquals(1, round.getAttemptsUsed());
            assertEquals(GuessResult.TOO_LOW, fb.getResult());
            assertEquals(2, round.getMinHint());
        }

        if (target < 100) {
            GuessFeedback fb = game.processGuess(String.valueOf(100));
            assertTrue(fb.isValid());
            assertEquals(GuessResult.TOO_HIGH, fb.getResult());
            assertEquals(99, round.getMaxHint());
        }
    }

    @Test
    @DisplayName("Test correct guess ends round as WON and updates statistics")
    void testWinningCondition() {
        game.startNewRound(Difficulty.EASY);
        GameRound round = game.getCurrentRound();
        int target = round.getTargetNumber();

        GuessFeedback fb = game.processGuess(String.valueOf(target));
        assertTrue(fb.isValid());
        assertTrue(fb.isRoundFinished());
        assertTrue(fb.isWon());
        assertEquals(GuessResult.CORRECT, fb.getResult());
        assertEquals(target, fb.getTargetRevealed());

        GameStatistics stats = game.getStatistics();
        assertEquals(1, stats.getRoundsPlayed());
        assertEquals(1, stats.getRoundsWon());
        assertEquals(0, stats.getRoundsLost());
        assertEquals(100.0, stats.getWinRatePercentage());
    }

    @Test
    @DisplayName("Test exhausting maximum attempts ends round as LOST, reveals target, and updates stats")
    void testLosingCondition() {
        game.startNewRound(Difficulty.HARD); // 5 attempts
        GameRound round = game.getCurrentRound();
        int target = round.getTargetNumber();
        int wrongGuess = (target == 1) ? 2 : 1;

        GuessFeedback fb = null;
        for (int i = 0; i < Difficulty.HARD.getMaxAttempts(); i++) {
            fb = game.processGuess(String.valueOf(wrongGuess));
        }

        assertNotNull(fb);
        assertTrue(fb.isRoundFinished());
        assertFalse(fb.isWon());
        assertEquals(GuessResult.EXHAUSTED, fb.getResult());
        assertEquals(target, fb.getTargetRevealed());

        GameStatistics stats = game.getStatistics();
        assertEquals(1, stats.getRoundsPlayed());
        assertEquals(0, stats.getRoundsWon());
        assertEquals(1, stats.getRoundsLost());
        assertEquals(0.0, stats.getWinRatePercentage());
    }

    @Test
    @DisplayName("Test resetScore resets statistics to 0 and clears round history")
    void testResetScore() {
        game.startNewRound(Difficulty.EASY);
        int target = game.getCurrentRound().getTargetNumber();
        game.processGuess(String.valueOf(target)); // Win 1 round

        assertEquals(1, game.getStatistics().getRoundsPlayed());

        game.resetScore();

        GameStatistics stats = game.getStatistics();
        assertEquals(0, stats.getRoundsPlayed());
        assertEquals(0, stats.getRoundsWon());
        assertEquals(0, stats.getRoundsLost());
        assertTrue(stats.getRoundHistory().isEmpty());
    }
}
