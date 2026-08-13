package com.oasis.guessinggame.model;

/**
 * Result enum for evaluated guesses.
 */
public enum GuessResult {
    TOO_HIGH("Too High!", "Your guess is higher than the secret number."),
    TOO_LOW("Too Low!", "Your guess is lower than the secret number."),
    CORRECT("Correct!", "Congratulations! You found the hidden number!"),
    OUT_OF_BOUNDS("Out of Range", "Please enter a number within the valid difficulty range."),
    EXHAUSTED("You Lost!", "You have run out of attempts."),
    INVALID_FORMAT("Invalid Input", "Please enter a valid whole number.");

    private final String title;
    private final String description;

    GuessResult(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
