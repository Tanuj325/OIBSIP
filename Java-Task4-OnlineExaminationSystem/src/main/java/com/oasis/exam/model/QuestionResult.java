package com.oasis.exam.model;

/**
 * Detailed result of a single question post-exam.
 */
public class QuestionResult {

    public enum Status {
        CORRECT,
        INCORRECT,
        UNANSWERED
    }

    private final int questionNumber;
    private final String questionText;
    private final String selectedOption; // "A", "B", "C", "D" or null
    private final String correctOption;  // "A", "B", "C", "D"
    private final String selectedText;
    private final String correctText;
    private final Status status;

    public QuestionResult(int questionNumber, String questionText, String selectedOption, String correctOption,
                          String selectedText, String correctText, Status status) {
        this.questionNumber = questionNumber;
        this.questionText = questionText;
        this.selectedOption = selectedOption;
        this.correctOption = correctOption;
        this.selectedText = selectedText;
        this.correctText = correctText;
        this.status = status;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public String getSelectedText() {
        return selectedText;
    }

    public String getCorrectText() {
        return correctText;
    }

    public Status getStatus() {
        return status;
    }
}
