package com.oasis.exam.model;

/**
 * Manages the currently logged-in user session and profile completion state.
 */
public class UserSession {
    private User currentUser;
    private ExamState examState = ExamState.NOT_STARTED;
    private boolean profileCompleted = false;

    public UserSession() {
    }

    public synchronized void startSession(User user) {
        this.currentUser = user;
        this.examState = ExamState.NOT_STARTED;
        this.profileCompleted = false;
    }

    public synchronized void clearSession() {
        this.currentUser = null;
        this.examState = ExamState.NOT_STARTED;
        this.profileCompleted = false;
    }

    public synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    public synchronized User getCurrentUser() {
        return currentUser;
    }

    public synchronized ExamState getExamState() {
        return examState;
    }

    public synchronized void setExamState(ExamState examState) {
        this.examState = examState;
    }

    public synchronized boolean isProfileCompleted() {
        return profileCompleted;
    }

    public synchronized void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }
}
