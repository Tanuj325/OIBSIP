package com.oasis.exam.service;

import com.oasis.exam.model.ExamResult;
import com.oasis.exam.model.Question;
import com.oasis.exam.model.QuestionResult;
import com.oasis.exam.repository.QuestionRepository;
import com.oasis.exam.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Computes exact exam statistics and creates ExamResult breakdown objects.
 */
public class ResultService {

    public ExamResult calculateResult(QuestionRepository repository, Map<Integer, String> studentAnswers,
                                       long elapsedSeconds, String studentDisplayName, String submissionMethod) {
        List<Question> questions = repository.getAllQuestions();
        int totalQuestions = questions.size();
        int correctCount = 0;
        int incorrectCount = 0;
        int unansweredCount = 0;

        List<QuestionResult> questionResults = new ArrayList<>();

        for (int i = 0; i < totalQuestions; i++) {
            Question q = questions.get(i);
            int qId = q.getQuestionId();
            String selectedOption = studentAnswers.get(qId); // "A", "B", "C", "D" or null/empty
            String correctOption = q.getCorrectOption();

            QuestionResult.Status status;
            if (selectedOption == null || selectedOption.trim().isEmpty()) {
                status = QuestionResult.Status.UNANSWERED;
                unansweredCount++;
            } else if (selectedOption.trim().equalsIgnoreCase(correctOption)) {
                status = QuestionResult.Status.CORRECT;
                correctCount++;
            } else {
                status = QuestionResult.Status.INCORRECT;
                incorrectCount++;
            }

            String selectedText = (selectedOption != null && !selectedOption.isEmpty()) ?
                    selectedOption + ": " + q.getOptionText(selectedOption) : "Unanswered";
            String correctText = correctOption + ": " + q.getOptionText(correctOption);

            questionResults.add(new QuestionResult(
                    i + 1,
                    q.getQuestionText(),
                    selectedOption,
                    correctOption,
                    selectedText,
                    correctText,
                    status
            ));
        }

        String formattedTime = TimeUtil.formatDurationText(elapsedSeconds);

        return new ExamResult(
                studentDisplayName,
                totalQuestions,
                correctCount,
                incorrectCount,
                unansweredCount,
                formattedTime,
                submissionMethod,
                questionResults
        );
    }
}
