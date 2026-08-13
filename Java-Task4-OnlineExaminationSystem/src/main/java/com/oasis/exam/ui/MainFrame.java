package com.oasis.exam.ui;

import com.oasis.exam.model.ExamResult;
import com.oasis.exam.model.ExamState;
import com.oasis.exam.model.UserSession;
import com.oasis.exam.repository.QuestionRepository;
import com.oasis.exam.repository.UserRepository;
import com.oasis.exam.service.AuthenticationService;
import com.oasis.exam.service.ExamService;
import com.oasis.exam.service.ResultService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application window organizing view panels with CardLayout, managing window closing safety,
 * single session lifecycle, and profile navigation guards.
 */
public class MainFrame extends JFrame {
    public static final String CARD_LOGIN = "LOGIN";
    public static final String CARD_PROFILE = "PROFILE";
    public static final String CARD_INSTRUCTIONS = "INSTRUCTIONS";
    public static final String CARD_EXAM = "EXAM";
    public static final String CARD_RESULT = "RESULT";

    private final CardLayout cardLayout;
    private final JPanel mainContainer;

    private final UserSession userSession;
    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AuthenticationService authenticationService;
    private final ResultService resultService;
    private final ExamService examService;

    private LoginPanel loginPanel;
    private ProfilePanel profilePanel;
    private InstructionsPanel instructionsPanel;
    private ExamPanel examPanel;
    private ResultPanel resultPanel;

    public MainFrame() {
        setTitle("Online Examination System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 768));

        // Initialize Services & Repositories
        this.userSession = new UserSession();
        this.userRepository = new UserRepository();
        this.questionRepository = new QuestionRepository();
        this.authenticationService = new AuthenticationService(userRepository, userSession);
        this.resultService = new ResultService();
        this.examService = new ExamService(questionRepository, userSession, resultService);

        // Setup CardLayout
        this.cardLayout = new CardLayout();
        this.mainContainer = new JPanel(cardLayout);
        this.mainContainer.setBackground(ThemeManager.APP_BG);

        initPanels();

        add(mainContainer);

        // Intercept window close button (X)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClose();
            }
        });

        // Show initial login card
        showCard(CARD_LOGIN);
    }

    private void initPanels() {
        // 1. LOGIN
        loginPanel = new LoginPanel(authenticationService, () -> {
            profilePanel.loadUserProfile();
            if (userSession.isProfileCompleted()) {
                instructionsPanel.updateQuestionCount();
                showCard(CARD_INSTRUCTIONS);
            } else {
                showCard(CARD_PROFILE);
            }
        });

        // 2. PROFILE
        profilePanel = new ProfilePanel(authenticationService, new ProfilePanel.ProfileActionListener() {
            @Override
            public void onProfileSavedAndContinued() {
                instructionsPanel.updateQuestionCount();
                showCard(CARD_INSTRUCTIONS);
            }

            @Override
            public void onLogoutRequested() {
                handleLogout();
            }
        });

        // 3. INSTRUCTIONS
        instructionsPanel = new InstructionsPanel(questionRepository, new InstructionsPanel.InstructionsActionListener() {
            @Override
            public void onStartExamClicked() {
                showCard(CARD_EXAM);
                examPanel.startExamSession();
            }

            @Override
            public void onBackToProfileClicked() {
                showCard(CARD_PROFILE);
            }
        });

        // 4. EXAM
        examPanel = new ExamPanel(examService, new ExamPanel.ExamNavigationListener() {
            @Override
            public void onExamSubmitted(ExamResult result, boolean isAutoSubmitted) {
                resultPanel.displayResult(result, isAutoSubmitted);
                showCard(CARD_RESULT);
            }
        });

        // 5. RESULT
        resultPanel = new ResultPanel(authenticationService, new ResultPanel.ResultActionListener() {
            @Override
            public void onLogoutClicked() {
                handleLogout();
            }

            @Override
            public void onRetakeExamClicked() {
                examService.cancelOrAbandonExam();
                instructionsPanel.updateQuestionCount();
                showCard(CARD_INSTRUCTIONS);
            }
        });

        mainContainer.add(loginPanel, CARD_LOGIN);
        mainContainer.add(profilePanel, CARD_PROFILE);
        mainContainer.add(instructionsPanel, CARD_INSTRUCTIONS);
        mainContainer.add(examPanel, CARD_EXAM);
        mainContainer.add(resultPanel, CARD_RESULT);
    }

    public void showCard(String cardName) {
        // Safety guard against unauthorized card access
        if ((CARD_PROFILE.equals(cardName) || CARD_INSTRUCTIONS.equals(cardName) || CARD_EXAM.equals(cardName) || CARD_RESULT.equals(cardName))
                && !userSession.isLoggedIn()) {
            cardLayout.show(mainContainer, CARD_LOGIN);
            return;
        }

        cardLayout.show(mainContainer, cardName);
    }

    private void handleLogout() {
        examService.cancelOrAbandonExam();
        authenticationService.logout();
        loginPanel.clearFields();
        showCard(CARD_LOGIN);
    }

    private void handleWindowClose() {
        if (userSession.getExamState() == ExamState.IN_PROGRESS) {
            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to quit the active exam?\nYour current progress will be lost.",
                    "Quit Exam Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                examService.cancelOrAbandonExam();
                authenticationService.logout();
                loginPanel.clearFields();
                showCard(CARD_LOGIN);
            }
            // If NO_OPTION, do nothing and keep exam active
        } else {
            dispose();
            System.exit(0);
        }
    }
}
