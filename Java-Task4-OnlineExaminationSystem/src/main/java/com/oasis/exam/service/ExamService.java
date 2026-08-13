package com.oasis.exam.service;

import com.oasis.exam.model.ExamResult;
import com.oasis.exam.model.ExamState;
import com.oasis.exam.model.Question;
import com.oasis.exam.model.UserSession;
import com.oasis.exam.repository.QuestionRepository;

import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Core service governing exam progress, navigation, answer storage, swing countdown timer,
 * submission safety, and result generation.
 */
public class ExamService {
    public static final int DEFAULT_EXAM_DURATION_SECONDS = 30 * 60; // 30 minutes

    private final QuestionRepository questionRepository;
    private final UserSession userSession;
    private final ResultService resultService;

    private final Map<Integer, String> studentAnswers = new ConcurrentHashMap<>();
    private final AtomicBoolean isSubmitted = new AtomicBoolean(false);

    private int currentQuestionIndex = 0;
    private int remainingSeconds = DEFAULT_EXAM_DURATION_SECONDS;
    private long startTimeMillis = 0;
    private long endTimeMillis = 0;

    private Timer swingTimer;
    private TimerListener timerListener;
    private ExamCompletionListener completionListener;

    public interface TimerListener {
        void onTimerTick(int remainingSeconds);
    }

    public interface ExamCompletionListener {
        void onExamSubmitted(ExamResult result, boolean isAutoSubmitted);
    }

    public ExamService(QuestionRepository questionRepository, UserSession userSession, ResultService resultService) {
        this.questionRepository = Objects.requireNonNull(questionRepository, "questionRepository cannot be null");
        this.userSession = Objects.requireNonNull(userSession, "userSession cannot be null");
        this.resultService = Objects.requireNonNull(resultService, "resultService cannot be null");
    }

    public synchronized void startExam(TimerListener timerListener, ExamCompletionListener completionListener) {
        if (userSession.getExamState() == ExamState.IN_PROGRESS) {
            return; // Already running
        }

        this.studentAnswers.clear();
        this.currentQuestionIndex = 0;
        this.remainingSeconds = DEFAULT_EXAM_DURATION_SECONDS;
        this.startTimeMillis = System.currentTimeMillis();
        this.endTimeMillis = 0;
        this.isSubmitted.set(false);
        this.timerListener = timerListener;
        this.completionListener = completionListener;

        userSession.setExamState(ExamState.IN_PROGRESS);

        // Initialize Swing Timer firing every 1000 ms on EDT
        if (swingTimer != null && swingTimer.isRunning()) {
            swingTimer.stop();
        }

        ActionListener timerAction = e -> {
            remainingSeconds--;
            if (timerListener != null) {
                timerListener.onTimerTick(remainingSeconds);
            }

            if (remainingSeconds <= 0) {
                stopTimer();
                handleAutoSubmit();
            }
        };

        swingTimer = new Timer(1000, timerAction);
        swingTimer.start();
    }

    public void saveAnswer(int questionId, String option) {
        if (userSession.getExamState() != ExamState.IN_PROGRESS || isSubmitted.get()) {
            return;
        }
        if (option == null || option.trim().isEmpty()) {
            studentAnswers.remove(questionId);
        } else {
            studentAnswers.put(questionId, option.trim().toUpperCase());
        }
    }

    public String getSavedAnswer(int questionId) {
        return studentAnswers.get(questionId);
    }

    public Question getCurrentQuestion() {
        return questionRepository.getQuestionByIndex(currentQuestionIndex);
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public int getTotalQuestions() {
        return questionRepository.getTotalQuestionsCount();
    }

    public boolean hasNext() {
        return currentQuestionIndex < getTotalQuestions() - 1;
    }

    public boolean hasPrevious() {
        return currentQuestionIndex > 0;
    }

    public void nextQuestion() {
        if (hasNext()) {
            currentQuestionIndex++;
        }
    }

    public void previousQuestion() {
        if (hasPrevious()) {
            currentQuestionIndex--;
        }
    }

    public void jumpToQuestion(int index) {
        if (index >= 0 && index < getTotalQuestions()) {
            currentQuestionIndex = index;
        }
    }

    public ExamResult submitManual() {
        return performSubmission("Manual Submission", false);
    }

    private void handleAutoSubmit() {
        performSubmission("Automatic Submission (Time Expired)", true);
    }

    private synchronized ExamResult performSubmission(String submissionMethod, boolean isAutoSubmitted) {
        // Atomic submission guard preventing double submission or race conditions
        if (!isSubmitted.compareAndSet(false, true)) {
            return null; // Already submitted
        }

        stopTimer();
        this.endTimeMillis = System.currentTimeMillis();
        userSession.setExamState(ExamState.SUBMITTED);

        long elapsedSeconds;
        if (isAutoSubmitted) {
            elapsedSeconds = DEFAULT_EXAM_DURATION_SECONDS;
        } else {
            elapsedSeconds = (endTimeMillis - startTimeMillis) / 1000;
            if (elapsedSeconds > DEFAULT_EXAM_DURATION_SECONDS) {
                elapsedSeconds = DEFAULT_EXAM_DURATION_SECONDS;
            }
        }

        String displayName = userSession.isLoggedIn() ? userSession.getCurrentUser().getDisplayName() : "Student";

        ExamResult result = resultService.calculateResult(
                questionRepository,
                studentAnswers,
                elapsedSeconds,
                displayName,
                submissionMethod
        );

        if (completionListener != null) {
            completionListener.onExamSubmitted(result, isAutoSubmitted);
        }

        return result;
    }

    public synchronized void cancelOrAbandonExam() {
        stopTimer();
        userSession.setExamState(ExamState.ABANDONED);
        studentAnswers.clear();
        isSubmitted.set(false);
    }

    public synchronized void stopTimer() {
        if (swingTimer != null && swingTimer.isRunning()) {
            swingTimer.stop();
        }
    }

    public Map<Integer, String> getStudentAnswers() {
        return studentAnswers;
    }

    public UserSession getUserSession() {
        return userSession;
    }

    public Question getQuestionByIndex(int index) {
        return questionRepository.getQuestionByIndex(index);
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public boolean isSubmitted() {
        return isSubmitted.get();
    }
}
