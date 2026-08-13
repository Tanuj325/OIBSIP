package com.oasis.exam.ui;

import com.oasis.exam.repository.QuestionRepository;

import javax.swing.*;
import java.awt.*;

/**
 * Exam instructions and preparation panel displayed before starting the test timer.
 */
public class InstructionsPanel extends JPanel {
    private final QuestionRepository questionRepository;
    private final InstructionsActionListener actionListener;

    private JLabel questionCountLabel;

    public interface InstructionsActionListener {
        void onStartExamClicked();
        void onBackToProfileClicked();
    }

    public InstructionsPanel(QuestionRepository questionRepository, InstructionsActionListener actionListener) {
        this.questionRepository = questionRepository;
        this.actionListener = actionListener;

        setLayout(new GridBagLayout());
        setBackground(ThemeManager.APP_BG);

        initUI();
    }

    private void initUI() {
        JPanel card = new JPanel();
        card.setLayout(new GridBagLayout());
        ThemeManager.styleCardPanel(card);
        card.setPreferredSize(new Dimension(650, 580));
        card.setMinimumSize(new Dimension(550, 520));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        // Title
        JLabel titleLabel = new JLabel("Examination Instructions", SwingConstants.CENTER);
        titleLabel.setFont(ThemeManager.FONT_TITLE);
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        card.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel subLabel = new JLabel("General Computer Science & Java Proficiency Assessment", SwingConstants.CENTER);
        subLabel.setFont(ThemeManager.FONT_BODY_BOLD);
        subLabel.setForeground(ThemeManager.PRIMARY_COLOR);
        card.add(subLabel, gbc);

        gbc.gridy++;
        card.add(Box.createVerticalStrut(15), gbc);

        // Exam Metadata Info Box
        gbc.gridy++;
        JPanel metaPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        metaPanel.setBackground(new Color(15, 23, 42));
        metaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(12, 20, 12, 20)
        ));

        questionCountLabel = new JLabel("Total Questions: " + questionRepository.getTotalQuestionsCount(), SwingConstants.CENTER);
        questionCountLabel.setFont(ThemeManager.FONT_HEADER);
        questionCountLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        JLabel durationLabel = new JLabel("Duration: 30 Minutes", SwingConstants.CENTER);
        durationLabel.setFont(ThemeManager.FONT_HEADER);
        durationLabel.setForeground(ThemeManager.TEXT_PRIMARY);

        metaPanel.add(questionCountLabel);
        metaPanel.add(durationLabel);
        card.add(metaPanel, gbc);

        gbc.gridy++;
        card.add(Box.createVerticalStrut(15), gbc);

        // Rules Section
        gbc.gridy++;
        JLabel rulesHeader = new JLabel("Rules & Instructions:");
        rulesHeader.setFont(ThemeManager.FONT_SUBTITLE);
        rulesHeader.setForeground(ThemeManager.TEXT_PRIMARY);
        card.add(rulesHeader, gbc);

        gbc.gridy++;
        JTextArea rulesText = new JTextArea();
        rulesText.setText(
                "• Each question presents four multiple-choice options (A, B, C, D).\n" +
                "• Select exactly one option per question.\n" +
                "• Use the Next and Previous buttons to navigate through questions.\n" +
                "• You can change your answers anytime before final submission.\n" +
                "• The countdown timer starts immediately when you click 'Start Exam'.\n" +
                "• When time expires (00:00), your exam will automatically submit.\n" +
                "• Click 'Submit Exam' when completed to view your final breakdown."
        );
        rulesText.setFont(ThemeManager.FONT_BODY);
        rulesText.setForeground(ThemeManager.TEXT_SECONDARY);
        rulesText.setBackground(ThemeManager.CARD_BG);
        rulesText.setEditable(false);
        rulesText.setFocusable(false);
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);
        card.add(rulesText, gbc);

        gbc.gridy++;
        card.add(Box.createVerticalStrut(20), gbc);

        // Buttons
        gbc.gridy++;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setOpaque(false);

        JButton backBtn = new JButton("Back to Profile");
        ThemeManager.styleSecondaryButton(backBtn);
        backBtn.setPreferredSize(new Dimension(0, 44));
        backBtn.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onBackToProfileClicked();
            }
        });
        buttonPanel.add(backBtn);

        JButton startBtn = new JButton("Start Exam");
        ThemeManager.styleSuccessButton(startBtn);
        startBtn.setPreferredSize(new Dimension(0, 44));
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        startBtn.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onStartExamClicked();
            }
        });
        buttonPanel.add(startBtn);

        card.add(buttonPanel, gbc);

        add(card);
    }

    public void updateQuestionCount() {
        if (questionCountLabel != null && questionRepository != null) {
            questionCountLabel.setText("Total Questions: " + questionRepository.getTotalQuestionsCount());
        }
    }
}
