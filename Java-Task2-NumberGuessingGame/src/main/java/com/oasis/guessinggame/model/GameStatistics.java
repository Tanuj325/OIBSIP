package com.oasis.guessinggame.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Maintains aggregate gameplay metrics and history logs across multiple rounds.
 */
public class GameStatistics {

    private final List<GameRound> roundHistory;
    private int roundsPlayed;
    private int roundsWon;
    private int roundsLost;
    private int currentWinStreak;
    private int bestWinStreak;
    private Integer bestAttemptsCount; // Fewest attempts taken to win a round

    public GameStatistics() {
        this.roundHistory = new ArrayList<>();
        resetAll();
    }

    public synchronized void recordRound(GameRound round) {
        if (round == null || round.getStatus() == GameRound.Status.IN_PROGRESS) {
            return;
        }

        roundHistory.add(round);
        roundsPlayed++;

        if (round.getStatus() == GameRound.Status.WON) {
            roundsWon++;
            currentWinStreak++;
            if (currentWinStreak > bestWinStreak) {
                bestWinStreak = currentWinStreak;
            }

            int attempts = round.getAttemptsUsed();
            if (bestAttemptsCount == null || attempts < bestAttemptsCount) {
                bestAttemptsCount = attempts;
            }
        } else if (round.getStatus() == GameRound.Status.LOST) {
            roundsLost++;
            currentWinStreak = 0;
        }
    }

    public synchronized void resetAll() {
        roundHistory.clear();
        roundsPlayed = 0;
        roundsWon = 0;
        roundsLost = 0;
        currentWinStreak = 0;
        bestWinStreak = 0;
        bestAttemptsCount = null;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public int getRoundsWon() {
        return roundsWon;
    }

    public int getRoundsLost() {
        return roundsLost;
    }

    public double getWinRatePercentage() {
        if (roundsPlayed == 0) {
            return 0.0;
        }
        return ((double) roundsWon / roundsPlayed) * 100.0;
    }

    public int getCurrentWinStreak() {
        return currentWinStreak;
    }

    public int getBestWinStreak() {
        return bestWinStreak;
    }

    public Integer getBestAttemptsCount() {
        return bestAttemptsCount;
    }

    public List<GameRound> getRoundHistory() {
        return Collections.unmodifiableList(roundHistory);
    }
}
