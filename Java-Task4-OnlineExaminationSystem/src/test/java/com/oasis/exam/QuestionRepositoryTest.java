package com.oasis.exam;

import com.oasis.exam.model.Question;
import com.oasis.exam.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionRepositoryTest {
    private QuestionRepository questionRepository;

    @BeforeEach
    void setUp() {
        questionRepository = new QuestionRepository();
    }

    @Test
    void testQuestionBankInitialization() {
        List<Question> questions = questionRepository.getAllQuestions();
        assertNotNull(questions);
        assertEquals(20, questions.size(), "Question repository should contain 20 questions.");
    }

    @Test
    void testGetQuestionByIndex() {
        Question q1 = questionRepository.getQuestionByIndex(0);
        assertNotNull(q1);
        assertEquals(1, q1.getQuestionId());
        assertNotNull(q1.getQuestionText());
        assertNotNull(q1.getCorrectOption());
        assertTrue(List.of("A", "B", "C", "D").contains(q1.getCorrectOption()));
    }

    @Test
    void testOutOfBoundsIndexThrowsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> questionRepository.getQuestionByIndex(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> questionRepository.getQuestionByIndex(100));
    }
}
