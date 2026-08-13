package com.oasis.exam.model;

import java.util.Objects;

/**
 * Represents a multiple-choice examination question.
 */
public class Question {
    private final int questionId;
    private final String questionText;
    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String optionD;
    private final String correctOption; // "A", "B", "C", or "D"

    public Question(int questionId, String questionText, String optionA, String optionB, String optionC, String optionD, String correctOption) {
        this.questionId = questionId;
        this.questionText = Objects.requireNonNull(questionText, "questionText cannot be null");
        this.optionA = Objects.requireNonNull(optionA, "optionA cannot be null");
        this.optionB = Objects.requireNonNull(optionB, "optionB cannot be null");
        this.optionC = Objects.requireNonNull(optionC, "optionC cannot be null");
        this.optionD = Objects.requireNonNull(optionD, "optionD cannot be null");
        this.correctOption = Objects.requireNonNull(correctOption, "correctOption cannot be null").toUpperCase().trim();
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public String getOptionText(String optionKey) {
        if (optionKey == null) return "";
        switch (optionKey.toUpperCase()) {
            case "A": return optionA;
            case "B": return optionB;
            case "C": return optionC;
            case "D": return optionD;
            default: return "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return questionId == question.questionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId);
    }
}
