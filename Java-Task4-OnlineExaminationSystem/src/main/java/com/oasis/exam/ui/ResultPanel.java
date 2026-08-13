package com.oasis.exam.ui;

import com.oasis.exam.model.ExamResult;
import com.oasis.exam.model.QuestionResult;
import com.oasis.exam.service.AuthenticationService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * Result display screen presenting metrics card, automatic vs manual submission indicator,
 * dynamic performance feedback banner, detailed breakdown table (JTable in JScrollPane),
 * retake exam option, and logout action.
 */
public class ResultPanel extends JPanel {
    private final AuthenticationService authenticationService;
    private final ResultActionListener actionListener;

    private JLabel studentNameLabel;
    private JLabel scoreLabel;
    private JLabel correctLabel;
    private JLabel incorrectLabel;
    private JLabel unansweredLabel;
    private JLabel timeTakenLabel;
    private JLabel submissionNoticeLabel;
    private JLabel performanceMessageLabel;

    private JTable breakdownTable;
    private DefaultTableModel tableModel;

    public interface ResultActionListener {
        void onLogoutClicked();
        void onRetakeExamClicked();
    }

    public ResultPanel(AuthenticationService authenticationService, ResultActionListener actionListener) {
        this.authenticationService = authenticationService;
        this.actionListener = actionListener;

        setLayout(new BorderLayout(15, 15));
        setBackground(ThemeManager.APP_BG);
        setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        initHeaderSummary();
        initTableBreakdown();
        initFooter();
    }

    private void initHeaderSummary() {
        JPanel container = new JPanel(new BorderLayout(0, 10));
        container.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("EXAMINATION RESULT SUMMARY", SwingConstants.CENTER);
        titleLabel.setFont(ThemeManager.FONT_TITLE);
        titleLabel.setForeground(ThemeManager.TEXT_PRIMARY);
        container.add(titleLabel, BorderLayout.NORTH);

        // Center notification group: Submission method + Performance message banner
        JPanel bannersGroup = new JPanel(new GridLayout(2, 1, 0, 4));
        bannersGroup.setOpaque(false);

        submissionNoticeLabel = new JLabel("", SwingConstants.CENTER);
        submissionNoticeLabel.setFont(ThemeManager.FONT_HEADER);
        submissionNoticeLabel.setForeground(ThemeManager.WARNING_COLOR);
        submissionNoticeLabel.setVisible(false);

        performanceMessageLabel = new JLabel("", SwingConstants.CENTER);
        performanceMessageLabel.setFont(ThemeManager.FONT_SUBTITLE);
        performanceMessageLabel.setForeground(ThemeManager.PRIMARY_COLOR);

        bannersGroup.add(submissionNoticeLabel);
        bannersGroup.add(performanceMessageLabel);

        // Metrics Card
        JPanel card = new JPanel(new GridLayout(2, 3, 20, 12));
        ThemeManager.styleCardPanel(card);

        studentNameLabel = createMetricItem(card, "STUDENT", "—", ThemeManager.TEXT_PRIMARY);
        scoreLabel = createMetricItem(card, "FINAL SCORE", "0 / 0", ThemeManager.PRIMARY_COLOR);
        timeTakenLabel = createMetricItem(card, "TIME TAKEN", "00:00", ThemeManager.TEXT_PRIMARY);

        correctLabel = createMetricItem(card, "CORRECT", "0", ThemeManager.SUCCESS_COLOR);
        incorrectLabel = createMetricItem(card, "INCORRECT", "0", ThemeManager.DANGER_COLOR);
        unansweredLabel = createMetricItem(card, "UNANSWERED", "0", ThemeManager.WARNING_COLOR);

        JPanel topHeaderGroup = new JPanel(new BorderLayout(0, 8));
        topHeaderGroup.setOpaque(false);
        topHeaderGroup.add(bannersGroup, BorderLayout.NORTH);
        topHeaderGroup.add(card, BorderLayout.CENTER);

        container.add(topHeaderGroup, BorderLayout.CENTER);
        add(container, BorderLayout.NORTH);
    }

    private JLabel createMetricItem(JPanel parentCard, String title, String initialVal, Color valueColor) {
        JPanel itemPanel = new JPanel(new GridLayout(2, 1, 0, 2));
        itemPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(ThemeManager.TEXT_SECONDARY);

        JLabel valLbl = new JLabel(initialVal);
        valLbl.setFont(ThemeManager.FONT_SUBTITLE);
        valLbl.setForeground(valueColor);

        itemPanel.add(titleLbl);
        itemPanel.add(valLbl);
        parentCard.add(itemPanel);

        return valLbl;
    }

    private void initTableBreakdown() {
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        ThemeManager.styleCardPanel(tableContainer);

        JLabel tableTitle = new JLabel("Question-by-Question Breakdown");
        tableTitle.setFont(ThemeManager.FONT_SUBTITLE);
        tableTitle.setForeground(ThemeManager.TEXT_PRIMARY);
        tableContainer.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"Q#", "Question Text", "Your Selection", "Correct Answer", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only JTable
            }
        };

        breakdownTable = new JTable(tableModel);
        breakdownTable.setFont(ThemeManager.FONT_BODY);
        breakdownTable.setBackground(ThemeManager.CARD_BG);
        breakdownTable.setForeground(ThemeManager.TEXT_PRIMARY);
        breakdownTable.setSelectionBackground(new Color(51, 65, 85));
        breakdownTable.setSelectionForeground(ThemeManager.TEXT_PRIMARY);
        breakdownTable.setRowHeight(36);

        JTableHeader header = breakdownTable.getTableHeader();
        header.setFont(ThemeManager.FONT_HEADER);
        header.setBackground(new Color(15, 23, 42));
        header.setForeground(ThemeManager.TEXT_PRIMARY);
        header.setReorderingAllowed(false);

        // Column width adjustments
        breakdownTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        breakdownTable.getColumnModel().getColumn(1).setPreferredWidth(450);
        breakdownTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        breakdownTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        breakdownTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        // Custom Cell Renderer for Status Column
        breakdownTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(ThemeManager.FONT_BODY_BOLD);

                if (value != null) {
                    String status = value.toString();
                    if ("CORRECT".equalsIgnoreCase(status)) {
                        setForeground(ThemeManager.SUCCESS_COLOR);
                    } else if ("INCORRECT".equalsIgnoreCase(status)) {
                        setForeground(ThemeManager.DANGER_COLOR);
                    } else {
                        setForeground(ThemeManager.WARNING_COLOR);
                    }
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(breakdownTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(ThemeManager.CARD_BG);

        tableContainer.add(scrollPane, BorderLayout.CENTER);
        add(tableContainer, BorderLayout.CENTER);
    }

    private void initFooter() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footerPanel.setOpaque(false);

        JButton retakeButton = new JButton("Retake Exam");
        ThemeManager.styleSuccessButton(retakeButton);
        retakeButton.setPreferredSize(new Dimension(140, 42));
        retakeButton.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.onRetakeExamClicked();
            }
        });

        JButton logoutButton = new JButton("Logout");
        ThemeManager.styleSecondaryButton(logoutButton);
        logoutButton.setPreferredSize(new Dimension(140, 42));
        logoutButton.addActionListener(e -> {
            authenticationService.logout();
            if (actionListener != null) {
                actionListener.onLogoutClicked();
            }
        });

        footerPanel.add(retakeButton);
        footerPanel.add(logoutButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public void displayResult(ExamResult result, boolean isAutoSubmitted) {
        if (result == null) return;

        studentNameLabel.setText(result.getStudentDisplayName());
        scoreLabel.setText(result.getScoreFormatted() + " (" + result.getScorePercentage() + "%)");
        correctLabel.setText(String.valueOf(result.getCorrectCount()));
        incorrectLabel.setText(String.valueOf(result.getIncorrectCount()));
        unansweredLabel.setText(String.valueOf(result.getUnansweredCount()));
        timeTakenLabel.setText(result.getTimeTakenFormatted());
        performanceMessageLabel.setText(result.getPerformanceMessage());

        if (isAutoSubmitted) {
            submissionNoticeLabel.setText("⚠ Time Expired: Your examination was automatically submitted.");
            submissionNoticeLabel.setVisible(true);
        } else {
            submissionNoticeLabel.setText("✓ Examination Successfully Submitted.");
            submissionNoticeLabel.setForeground(ThemeManager.SUCCESS_COLOR);
            submissionNoticeLabel.setVisible(true);
        }

        // Populate breakdown table
        tableModel.setRowCount(0);
        List<QuestionResult> questionResults = result.getQuestionResults();
        for (QuestionResult qr : questionResults) {
            tableModel.addRow(new Object[]{
                    qr.getQuestionNumber(),
                    qr.getQuestionText(),
                    qr.getSelectedText(),
                    qr.getCorrectText(),
                    qr.getStatus().name()
            });
        }
    }
}
