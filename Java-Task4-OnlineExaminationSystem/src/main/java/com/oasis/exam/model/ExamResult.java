package com.oasis.exam.model;

import java.util.Collections;
import java.util.List;

/**
 * Summary result object calculated upon exam submission.
 */
public class ExamResult {
    private final String studentDisplayName;
    private final int totalQuestions;
    private final int correctCount;
    private final int incorrectCount;
    private final int unansweredCount;
    private final int scorePercentage;
    private final String performanceMessage;
    private final String timeTakenFormatted;
    private final String submissionMethod; // "Manual" or "Time Expired (Auto)"
    private final List<QuestionResult> questionResults;

    public ExamResult(String studentDisplayName, int totalQuestions, int correctCount, int incorrectCount,
                      int unansweredCount, String timeTakenFormatted, String submissionMethod,
                      List<QuestionResult> questionResults) {
        this.studentDisplayName = studentDisplayName;
        this.totalQuestions = totalQuestions;
        this.correctCount = correctCount;
        this.incorrectCount = incorrectCount;
        this.unansweredCount = unansweredCount;
        this.scorePercentage = totalQuestions > 0 ? (int) Math.round(((double) correctCount / totalQuestions) * 100) : 0;
        this.performanceMessage = computePerformanceMessage(this.scorePercentage);
        this.timeTakenFormatted = timeTakenFormatted;
        this.submissionMethod = submissionMethod;
        this.questionResults = questionResults != null ? List.copyOf(questionResults) : Collections.emptyList();
    }

    private static String computePerformanceMessage(int percentage) {
        if (percentage >= 80) {
            return "Excellent Performance!";
        } else if (percentage >= 60) {
            return "Good Performance!";
        } else if (percentage >= 40) {
            return "Keep Practicing!";
        } else {
            return "More Practice Recommended.";
        }
    }

    public String getStudentDisplayName() {
        return studentDisplayName;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getIncorrectCount() {
        return incorrectCount;
    }

    public int getUnansweredCount() {
        return unansweredCount;
    }

    public int getScorePercentage() {
        return scorePercentage;
    }

    public String getPerformanceMessage() {
        return performanceMessage;
    }

    public String getScoreFormatted() {
        return correctCount + " out of " + totalQuestions;
    }

    public String getTimeTakenFormatted() {
        return timeTakenFormatted;
    }

    public String getSubmissionMethod() {
        return submissionMethod;
    }

    public List<QuestionResult> getQuestionResults() {
        return questionResults;
    }
}
