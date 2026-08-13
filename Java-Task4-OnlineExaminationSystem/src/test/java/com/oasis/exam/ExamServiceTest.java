package com.oasis.exam;

import com.oasis.exam.model.ExamResult;
import com.oasis.exam.model.ExamState;
import com.oasis.exam.model.UserSession;
import com.oasis.exam.repository.QuestionRepository;
import com.oasis.exam.repository.UserRepository;
import com.oasis.exam.service.AuthenticationService;
import com.oasis.exam.service.ExamService;
import com.oasis.exam.service.ResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExamServiceTest {
    private QuestionRepository questionRepository;
    private UserSession userSession;
    private AuthenticationService authService;
    private ResultService resultService;
    private ExamService examService;

    @BeforeEach
    void setUp() {
        UserRepository userRepository = new UserRepository();
        userSession = new UserSession();
        authService = new AuthenticationService(userRepository, userSession);
        resultService = new ResultService();
        examService = new ExamService(questionRepository = new QuestionRepository(), userSession, resultService);

        authService.login("student1", "password123");
    }

    @Test
    void testExamStartAndNavigation() {
        examService.startExam(null, null);
        assertEquals(ExamState.IN_PROGRESS, userSession.getExamState());

        assertEquals(0, examService.getCurrentQuestionIndex());
        assertFalse(examService.hasPrevious());
        assertTrue(examService.hasNext());

        examService.nextQuestion();
        assertEquals(1, examService.getCurrentQuestionIndex());
        assertTrue(examService.hasPrevious());

        examService.previousQuestion();
        assertEquals(0, examService.getCurrentQuestionIndex());

        examService.stopTimer();
    }

    @Test
    void testAnswerPersistence() {
        examService.startExam(null, null);
        int q1Id = examService.getCurrentQuestion().getQuestionId();

        examService.saveAnswer(q1Id, "B");
        assertEquals("B", examService.getSavedAnswer(q1Id));

        examService.nextQuestion();
        int q2Id = examService.getCurrentQuestion().getQuestionId();
        examService.saveAnswer(q2Id, "C");

        examService.previousQuestion();
        assertEquals("B", examService.getSavedAnswer(q1Id));

        examService.stopTimer();
    }

    @Test
    void testManualSubmissionLocksStateAndStopsTimer() {
        examService.startExam(null, null);
        int q1Id = examService.getCurrentQuestion().getQuestionId();
        examService.saveAnswer(q1Id, "B");

        ExamResult result = examService.submitManual();
        assertNotNull(result);
        assertTrue(examService.isSubmitted());
        assertEquals(ExamState.SUBMITTED, userSession.getExamState());

        // Attempting second submission returns null due to atomic submission guard
        ExamResult duplicate = examService.submitManual();
        assertNull(duplicate, "Second submission attempt must be blocked.");
    }
}
