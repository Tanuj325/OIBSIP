package com.oasis.guessinggame.ui;

import com.oasis.guessinggame.model.GameRound;
import com.oasis.guessinggame.model.GameStatistics;
import com.oasis.guessinggame.util.GameConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Statistics panel rendering aggregate performance cards, win rates, and round history table.
 */
public class StatisticsPanel extends JPanel {

    private final Runnable onResetScoreRequested;

    private JLabel valPlayed;
    private JLabel valWon;
    private JLabel valLost;
    private JLabel valWinRate;
    private DefaultTableModel tableModel;
    private JTable historyTable;

    public StatisticsPanel(Runnable onResetScoreRequested) {
        this.onResetScoreRequested = onResetScoreRequested;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 15));
        setBackground(GameConstants.BG_DARK);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Game Statistics & History");
        titleLabel.setFont(GameConstants.FONT_HEADER);
        titleLabel.setForeground(GameConstants.TEXT_PRIMARY);

        JButton resetButton = new JButton("Reset Score");
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBackground(GameConstants.COLOR_HARD);
        resetButton.setFocusPainted(false);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to reset all game statistics and round history?",
                    "Confirm Reset Score",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION && onResetScoreRequested != null) {
                onResetScoreRequested.run();
            }
        });

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(resetButton, BorderLayout.EAST);

        // Metric Cards Grid
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 10, 0));
        metricsGrid.setOpaque(false);

        valPlayed = new JLabel("0", SwingConstants.CENTER);
        valWon = new JLabel("0", SwingConstants.CENTER);
        valLost = new JLabel("0", SwingConstants.CENTER);
        valWinRate = new JLabel("0%", SwingConstants.CENTER);

        metricsGrid.add(createMetricCard("Rounds Played", valPlayed, GameConstants.TEXT_PRIMARY));
        metricsGrid.add(createMetricCard("Rounds Won", valWon, GameConstants.COLOR_CORRECT));
        metricsGrid.add(createMetricCard("Rounds Lost", valLost, GameConstants.COLOR_LOST));
        metricsGrid.add(createMetricCard("Win Rate", valWinRate, GameConstants.ACCENT_PRIMARY));

        // History Table Setup
        String[] columnNames = {"Round", "Difficulty", "Attempts", "Outcome", "Summary Log"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setFont(GameConstants.FONT_TABLE);
        historyTable.setRowHeight(28);
        historyTable.setBackground(GameConstants.BG_CARD);
        historyTable.setForeground(GameConstants.TEXT_PRIMARY);
        historyTable.setSelectionBackground(GameConstants.BG_CARD_HOVER);
        historyTable.setSelectionForeground(GameConstants.TEXT_PRIMARY);
        historyTable.setShowGrid(true);
        historyTable.setGridColor(GameConstants.BORDER_COLOR);

        historyTable.getTableHeader().setFont(GameConstants.FONT_TABLE_HEADER);
        historyTable.getTableHeader().setBackground(GameConstants.BG_INPUT);
        historyTable.getTableHeader().setForeground(GameConstants.TEXT_PRIMARY);
        historyTable.getTableHeader().setReorderingAllowed(false);

        // Column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(90);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(280);

        // Custom Cell Renderer for Outcomes
        historyTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                String val = value != null ? value.toString() : "";
                if ("WON".equalsIgnoreCase(val)) {
                    setForeground(GameConstants.COLOR_CORRECT);
                } else if ("LOST".equalsIgnoreCase(val)) {
                    setForeground(GameConstants.COLOR_LOST);
                } else {
                    setForeground(GameConstants.TEXT_PRIMARY);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.getViewport().setBackground(GameConstants.BG_CARD);
        scrollPane.setBorder(BorderFactory.createLineBorder(GameConstants.BORDER_COLOR, 1));

        JPanel historyContainer = new JPanel(new BorderLayout(0, 8));
        historyContainer.setOpaque(false);

        JLabel historyHeader = new JLabel("Round History Log");
        historyHeader.setFont(GameConstants.FONT_HEADER);
        historyHeader.setForeground(GameConstants.TEXT_PRIMARY);

        historyContainer.add(historyHeader, BorderLayout.NORTH);
        historyContainer.add(scrollPane, BorderLayout.CENTER);

        // Main Layout Assembly
        JPanel topContainer = new JPanel(new BorderLayout(0, 12));
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(metricsGrid, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(historyContainer, BorderLayout.CENTER);
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color valColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(GameConstants.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameConstants.BORDER_COLOR, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(GameConstants.FONT_CARD_LABEL);
        titleLbl.setForeground(GameConstants.TEXT_SECONDARY);

        valueLabel.setFont(GameConstants.FONT_CARD_VALUE);
        valueLabel.setForeground(valColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    public void updateStats(GameStatistics stats) {
        if (stats == null) {
            return;
        }

        valPlayed.setText(String.valueOf(stats.getRoundsPlayed()));
        valWon.setText(String.valueOf(stats.getRoundsWon()));
        valLost.setText(String.valueOf(stats.getRoundsLost()));
        valWinRate.setText(String.format("%.1f%%", stats.getWinRatePercentage()));

        tableModel.setRowCount(0);
        List<GameRound> history = stats.getRoundHistory();
        for (GameRound round : history) {
            String outcome = round.getStatus() == GameRound.Status.WON ? "WON" : "LOST";
            Object[] row = new Object[]{
                    round.getRoundNumber(),
                    round.getDifficulty().getDisplayName(),
                    round.getAttemptsUsed() + " / " + round.getDifficulty().getMaxAttempts(),
                    outcome,
                    round.getSummaryText()
            };
            tableModel.addRow(row);
        }
    }
}
