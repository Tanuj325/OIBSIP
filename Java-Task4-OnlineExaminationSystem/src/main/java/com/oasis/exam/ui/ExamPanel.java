package com.oasis.exam.ui;

import com.oasis.exam.model.ExamResult;
import com.oasis.exam.model.Question;
import com.oasis.exam.service.ExamService;
import com.oasis.exam.util.TimeUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced examination view featuring 1 MCQ at a time, dynamic JProgressBar,
 * live Answered / Unanswered counter, question navigator grid, visible timer alerts,
 * answer persistence, and submission safety.
 */
public class ExamPanel extends JPanel {
    private final ExamService examService;
    private final ExamNavigationListener navigationListener;

    // Header UI
    private JLabel studentNameLabel;
    private JLabel questionProgressLabel;
    private JLabel answeredCountLabel;
    private JLabel timerLabel;
    private JPanel timerCard;

    // Progress Bar
    private JProgressBar examProgressBar;

    // Question & Options UI
    private JLabel questionIdHeaderLabel;
    private JTextArea questionTextArea;
    private JRadioButton optionARadio;
    private JRadioButton optionBRadio;
    private JRadioButton optionCRadio;
    private JRadioButton optionDRadio;
    private ButtonGroup optionGroup;

    // Navigation UI
    private JButton previousButton;
    private JButton nextButton;
    private JButton submitButton;

    // Side Navigation Grid
    private JPanel gridPanel;
    private final List<JButton> questionGridButtons = new ArrayList<>();

    public interface ExamNavigationListener {
        void onExamSubmitted(ExamResult result, boolean isAutoSubmitted);
    }

    public ExamPanel(ExamService examService, ExamNavigationListener navigationListener) {
        this.examService = examService;
        this.navigationListener = navigationListener;

        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeManager.APP_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        initHeader();
        initCenter();
        initSideGrid();
        initFooter();
    }

    private void initHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(ThemeManager.HEADER_BG);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        // Left info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        infoPanel.setOpaque(false);

        JLabel appTitle = new JLabel("ONLINE EXAMINATION SYSTEM");
        appTitle.setFont(ThemeManager.FONT_HEADER);
        appTitle.setForeground(ThemeManager.PRIMARY_COLOR);

        studentNameLabel = new JLabel("Student: Candidate");
        studentNameLabel.setFont(ThemeManager.FONT_BODY_BOLD);
        studentNameLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        infoPanel.add(appTitle);
        infoPanel.add(studentNameLabel);
        headerPanel.add(infoPanel, BorderLayout.WEST);

        // Center info (Progress & Answered count)
        JPanel centerHeader = new JPanel(new GridLayout(2, 1, 0, 2));
        centerHeader.setOpaque(false);

        questionProgressLabel = new JLabel("Question 1 of " + examService.getTotalQuestions(), SwingConstants.CENTER);
        questionProgressLabel.setFont(ThemeManager.FONT_SUBTITLE);
        questionProgressLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        answeredCountLabel = new JLabel("Answered: 0 / " + examService.getTotalQuestions(), SwingConstants.CENTER);
        answeredCountLabel.setFont(ThemeManager.FONT_HEADER);
        answeredCountLabel.setForeground(ThemeManager.SUCCESS_COLOR);

        centerHeader.add(questionProgressLabel);
        centerHeader.add(answeredCountLabel);
        headerPanel.add(centerHeader, BorderLayout.CENTER);

        // Right Timer Card
        timerCard = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        timerCard.setBackground(ThemeManager.CARD_BG);
        timerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)
        ));

        JLabel timerTitle = new JLabel("TIME REMAINING");
        timerTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        timerTitle.setForeground(ThemeManager.TEXT_SECONDARY);

        timerLabel = new JLabel(TimeUtil.formatMMSS(ExamService.DEFAULT_EXAM_DURATION_SECONDS));
        timerLabel.setFont(ThemeManager.FONT_TIMER);
        timerLabel.setForeground(ThemeManager.PRIMARY_COLOR);

        JPanel timerInner = new JPanel(new GridLayout(2, 1, 0, 2));
        timerInner.setOpaque(false);
        timerInner.add(timerTitle);
        timerInner.add(timerLabel);

        timerCard.add(timerInner);
        headerPanel.add(timerCard, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void initCenter() {
        JPanel centerContainer = new JPanel(new BorderLayout(0, 10));
        centerContainer.setOpaque(false);

        // Visual JProgressBar
        examProgressBar = new JProgressBar(0, 100);
        examProgressBar.setValue(0);
        examProgressBar.setStringPainted(true);
        examProgressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        examProgressBar.setForeground(ThemeManager.PRIMARY_COLOR);
        examProgressBar.setBackground(ThemeManager.CARD_BG);
        examProgressBar.setPreferredSize(new Dimension(0, 20));
        centerContainer.add(examProgressBar, BorderLayout.NORTH);

        // Question Display Box
        JPanel questionBox = new JPanel(new BorderLayout(0, 10));
        ThemeManager.styleCardPanel(questionBox);

        questionIdHeaderLabel = new JLabel("Question 1");
        questionIdHeaderLabel.setFont(ThemeManager.FONT_SUBTITLE);
        questionIdHeaderLabel.setForeground(ThemeManager.PRIMARY_COLOR);
        questionBox.add(questionIdHeaderLabel, BorderLayout.NORTH);

        questionTextArea = new JTextArea();
        questionTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        questionTextArea.setForeground(ThemeManager.TEXT_PRIMARY);
        questionTextArea.setBackground(ThemeManager.CARD_BG);
        questionTextArea.setEditable(false);
        questionTextArea.setFocusable(false);
        questionTextArea.setLineWrap(true);
        questionTextArea.setWrapStyleWord(true);
        questionTextArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        questionBox.add(questionTextArea, BorderLayout.CENTER);

        // Options Radio Panel
        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 0, 12));
        optionsPanel.setOpaque(false);

        optionGroup = new ButtonGroup();

        optionARadio = createOptionRadioButton("A");
        optionBRadio = createOptionRadioButton("B");
        optionCRadio = createOptionRadioButton("C");
        optionDRadio = createOptionRadioButton("D");

        optionGroup.add(optionARadio);
        optionGroup.add(optionBRadio);
        optionGroup.add(optionCRadio);
        optionGroup.add(optionDRadio);

        optionsPanel.add(optionARadio);
        optionsPanel.add(optionBRadio);
        optionsPanel.add(optionCRadio);
        optionsPanel.add(optionDRadio);

        questionBox.add(optionsPanel, BorderLayout.SOUTH);

        centerContainer.add(questionBox, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);
    }

    private JRadioButton createOptionRadioButton(String optionCode) {
        JRadioButton radioButton = new JRadioButton();
        radioButton.setFont(ThemeManager.FONT_BODY_BOLD);
        radioButton.setForeground(ThemeManager.TEXT_PRIMARY);
        radioButton.setBackground(ThemeManager.CARD_BG);
        radioButton.setOpaque(true);
        radioButton.setFocusPainted(false);
        radioButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        radioButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        radioButton.addActionListener(e -> saveCurrentSelection());
        return radioButton;
    }

    private void initSideGrid() {
        JPanel sideContainer = new JPanel(new BorderLayout(0, 10));
        sideContainer.setOpaque(false);
        sideContainer.setPreferredSize(new Dimension(220, 0));

        JLabel gridTitle = new JLabel("Question Navigator", SwingConstants.CENTER);
        gridTitle.setFont(ThemeManager.FONT_HEADER);
        gridTitle.setForeground(ThemeManager.TEXT_PRIMARY);

        JPanel sideCard = new JPanel(new BorderLayout(0, 10));
        ThemeManager.styleCardPanel(sideCard);
        sideCard.add(gridTitle, BorderLayout.NORTH);

        gridPanel = new JPanel(new GridLayout(0, 4, 8, 8));
        gridPanel.setOpaque(false);

        JScrollPane gridScroll = new JScrollPane(gridPanel);
        gridScroll.setBorder(null);
        gridScroll.setOpaque(false);
        gridScroll.getViewport().setOpaque(false);

        sideCard.add(gridScroll, BorderLayout.CENTER);
        sideContainer.add(sideCard, BorderLayout.CENTER);

        add(sideContainer, BorderLayout.EAST);
    }

    private void initFooter() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        // Left: Nav buttons
        JPanel leftNav = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftNav.setOpaque(false);

        previousButton = new JButton("Previous");
        ThemeManager.styleSecondaryButton(previousButton);
        previousButton.setPreferredSize(new Dimension(130, 42));
        previousButton.addActionListener(e -> navigatePrevious());

        nextButton = new JButton("Next");
        ThemeManager.stylePrimaryButton(nextButton);
        nextButton.setPreferredSize(new Dimension(130, 42));
        nextButton.addActionListener(e -> navigateNext());

        leftNav.add(previousButton);
        leftNav.add(nextButton);

        // Right: Submit button
        submitButton = new JButton("Submit Exam");
        ThemeManager.styleDangerButton(submitButton);
        submitButton.setPreferredSize(new Dimension(160, 42));
        submitButton.addActionListener(e -> confirmAndSubmitManual());

        footerPanel.add(leftNav, BorderLayout.WEST);
        footerPanel.add(submitButton, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    public void startExamSession() {
        if (examService.getUserSession() != null && examService.getUserSession().isLoggedIn()) {
            studentNameLabel.setText("Student: " + examService.getUserSession().getCurrentUser().getDisplayName());
        } else {
            studentNameLabel.setText("Student: Candidate");
        }

        buildNavigationGrid();

        examService.startExam(
                remainingSeconds -> SwingUtilities.invokeLater(() -> updateTimerDisplay(remainingSeconds)),
                (result, isAutoSubmitted) -> SwingUtilities.invokeLater(() -> {
                    if (navigationListener != null) {
                        navigationListener.onExamSubmitted(result, isAutoSubmitted);
                    }
                })
        );

        loadCurrentQuestion();
    }

    private void buildNavigationGrid() {
        gridPanel.removeAll();
        questionGridButtons.clear();
        int total = examService.getTotalQuestions();

        for (int i = 0; i < total; i++) {
            final int index = i;
            JButton qBtn = new JButton(String.valueOf(i + 1));
            qBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            qBtn.setFocusPainted(false);
            qBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            qBtn.addActionListener(e -> {
                saveCurrentSelection();
                examService.jumpToQuestion(index);
                loadCurrentQuestion();
            });
            questionGridButtons.add(qBtn);
            gridPanel.add(qBtn);
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void updateTimerDisplay(int remainingSeconds) {
        timerLabel.setText(TimeUtil.formatMMSS(remainingSeconds));

        // Visual warnings
        if (remainingSeconds <= 60) {
            // Critical Red (<= 1 min)
            timerLabel.setForeground(ThemeManager.DANGER_COLOR);
            timerCard.setBorder(BorderFactory.createLineBorder(ThemeManager.DANGER_COLOR, 2, true));
        } else if (remainingSeconds <= 300) {
            // Warning Amber (<= 5 min)
            timerLabel.setForeground(ThemeManager.WARNING_COLOR);
            timerCard.setBorder(BorderFactory.createLineBorder(ThemeManager.WARNING_COLOR, 2, true));
        } else {
            // Normal Blue
            timerLabel.setForeground(ThemeManager.PRIMARY_COLOR);
            timerCard.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true));
        }
    }

    private void saveCurrentSelection() {
        Question current = examService.getCurrentQuestion();
        if (current == null) return;

        String selectedOption = null;
        if (optionARadio.isSelected()) selectedOption = "A";
        else if (optionBRadio.isSelected()) selectedOption = "B";
        else if (optionCRadio.isSelected()) selectedOption = "C";
        else if (optionDRadio.isSelected()) selectedOption = "D";

        examService.saveAnswer(current.getQuestionId(), selectedOption);
        updateProgressAndAnsweredCounts();
        updateGridButtonStyles();
    }

    private void updateProgressAndAnsweredCounts() {
        int total = examService.getTotalQuestions();
        int answeredCount = examService.getStudentAnswers().size();
        answeredCountLabel.setText(String.format("Answered: %d / %d", answeredCount, total));

        int currentIdx = examService.getCurrentQuestionIndex();
        int progressPct = Math.min(100, Math.max(0, (int) Math.round(((double) (currentIdx + 1) / total) * 100)));
        examProgressBar.setValue(progressPct);
        examProgressBar.setString(String.format("Progress: Question %d of %d (%d%%)", currentIdx + 1, total, progressPct));
    }

    private void loadCurrentQuestion() {
        Question q = examService.getCurrentQuestion();
        int currentIdx = examService.getCurrentQuestionIndex();
        int total = examService.getTotalQuestions();

        questionProgressLabel.setText(String.format("Question %d of %d", currentIdx + 1, total));
        questionIdHeaderLabel.setText("Question " + (currentIdx + 1));
        questionTextArea.setText(q.getQuestionText());

        optionARadio.setText("A)  " + q.getOptionA());
        optionBRadio.setText("B)  " + q.getOptionB());
        optionCRadio.setText("C)  " + q.getOptionC());
        optionDRadio.setText("D)  " + q.getOptionD());

        optionGroup.clearSelection();

        String savedOption = examService.getSavedAnswer(q.getQuestionId());
        if ("A".equalsIgnoreCase(savedOption)) optionARadio.setSelected(true);
        else if ("B".equalsIgnoreCase(savedOption)) optionBRadio.setSelected(true);
        else if ("C".equalsIgnoreCase(savedOption)) optionCRadio.setSelected(true);
        else if ("D".equalsIgnoreCase(savedOption)) optionDRadio.setSelected(true);

        // Update nav buttons
        previousButton.setEnabled(examService.hasPrevious());
        nextButton.setEnabled(examService.hasNext());

        updateProgressAndAnsweredCounts();
        updateGridButtonStyles();
    }

    private void updateGridButtonStyles() {
        int currentIdx = examService.getCurrentQuestionIndex();
        for (int i = 0; i < questionGridButtons.size(); i++) {
            JButton btn = questionGridButtons.get(i);
            Question q = examService.getQuestionByIndex(i);
            String saved = examService.getSavedAnswer(q.getQuestionId());

            if (i == currentIdx) {
                btn.setBackground(ThemeManager.PRIMARY_COLOR);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
            } else if (saved != null && !saved.isEmpty()) {
                btn.setBackground(ThemeManager.SUCCESS_COLOR);
                btn.setForeground(Color.WHITE);
                btn.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true));
            } else {
                btn.setBackground(ThemeManager.CARD_BG);
                btn.setForeground(ThemeManager.TEXT_SECONDARY);
                btn.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true));
            }
        }
    }

    private void navigateNext() {
        saveCurrentSelection();
        if (examService.hasNext()) {
            examService.nextQuestion();
            loadCurrentQuestion();
        }
    }

    private void navigatePrevious() {
        saveCurrentSelection();
        if (examService.hasPrevious()) {
            examService.previousQuestion();
            loadCurrentQuestion();
        }
    }

    private void confirmAndSubmitManual() {
        saveCurrentSelection();
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to submit the exam?\nYou will not be able to change your answers once submitted.",
                "Confirm Exam Submission",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (choice == JOptionPane.YES_OPTION) {
            examService.submitManual();
        }
    }
}
