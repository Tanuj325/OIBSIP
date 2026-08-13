package com.oasis.exam;

import com.oasis.exam.model.ExamResult;
import com.oasis.exam.model.QuestionResult;
import com.oasis.exam.repository.QuestionRepository;
import com.oasis.exam.service.ResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ResultServiceTest {
    private QuestionRepository questionRepository;
    private ResultService resultService;

    @BeforeEach
    void setUp() {
        questionRepository = new QuestionRepository();
        resultService = new ResultService();
    }

    @Test
    void testCalculateResultAllCorrect() {
        Map<Integer, String> answers = new HashMap<>();
        for (var q : questionRepository.getAllQuestions()) {
            answers.put(q.getQuestionId(), q.getCorrectOption());
        }

        ExamResult result = resultService.calculateResult(questionRepository, answers, 600, "Tanuj", "Manual Submission");
        assertEquals(20, result.getTotalQuestions());
        assertEquals(20, result.getCorrectCount());
        assertEquals(0, result.getIncorrectCount());
        assertEquals(0, result.getUnansweredCount());
        assertEquals(100, result.getScorePercentage());
        assertEquals("20 out of 20", result.getScoreFormatted());
    }

    @Test
    void testCalculateResultWithUnansweredAndIncorrect() {
        Map<Integer, String> answers = new HashMap<>();
        // Q1 correct ("B")
        answers.put(1, "B");
        // Q2 incorrect ("A" instead of "B")
        answers.put(2, "A");
        // Q3 to Q20 unanswered

        ExamResult result = resultService.calculateResult(questionRepository, answers, 300, "Tanuj", "Manual Submission");
        assertEquals(20, result.getTotalQuestions());
        assertEquals(1, result.getCorrectCount());
        assertEquals(1, result.getIncorrectCount());
        assertEquals(18, result.getUnansweredCount());
        assertEquals(5, result.getScorePercentage());
        assertEquals("1 out of 20", result.getScoreFormatted());

        // Check Q1 status
        QuestionResult q1Res = result.getQuestionResults().get(0);
        assertEquals(QuestionResult.Status.CORRECT, q1Res.getStatus());

        // Check Q2 status
        QuestionResult q2Res = result.getQuestionResults().get(1);
        assertEquals(QuestionResult.Status.INCORRECT, q2Res.getStatus());

        // Check Q3 status
        QuestionResult q3Res = result.getQuestionResults().get(2);
        assertEquals(QuestionResult.Status.UNANSWERED, q3Res.getStatus());
    }
}
