package com.oasis.guessinggame.ui;

import com.oasis.guessinggame.model.Difficulty;
import com.oasis.guessinggame.util.GameConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Welcome screen panel allowing players to select difficulty before starting a round.
 */
public class StartPanel extends JPanel {

    private final Consumer<Difficulty> onStartGameCallback;
    private JComboBox<Difficulty> difficultyComboBox;
    private Difficulty selectedDifficulty = Difficulty.MEDIUM;

    public StartPanel(Consumer<Difficulty> onStartGameCallback) {
        this.onStartGameCallback = onStartGameCallback;
        initUI();
    }

    private void initUI() {
        setLayout(new GridBagLayout());
        setBackground(GameConstants.BG_DARK);

        JPanel contentBox = new JPanel();
        contentBox.setLayout(new BoxLayout(contentBox, BoxLayout.Y_AXIS));
        contentBox.setBackground(GameConstants.BG_CARD);
        contentBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameConstants.BORDER_COLOR, 1),
                new EmptyBorder(40, 50, 40, 50)
        ));

        // Title
        JLabel titleLabel = new JLabel("NUMBER GUESSING GAME", SwingConstants.CENTER);
        titleLabel.setFont(GameConstants.FONT_TITLE);
        titleLabel.setForeground(GameConstants.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Guess the hidden number before you run out of attempts.", SwingConstants.CENTER);
        subtitleLabel.setFont(GameConstants.FONT_SUBTITLE);
        subtitleLabel.setForeground(GameConstants.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Difficulty Selection Label
        JLabel diffPromptLabel = new JLabel("Select Game Difficulty", SwingConstants.CENTER);
        diffPromptLabel.setFont(GameConstants.FONT_HEADER);
        diffPromptLabel.setForeground(GameConstants.TEXT_PRIMARY);
        diffPromptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Difficulty Selector Dropdown
        difficultyComboBox = new JComboBox<>(Difficulty.values());
        difficultyComboBox.setSelectedItem(Difficulty.MEDIUM);
        difficultyComboBox.setFont(GameConstants.FONT_BUTTON);
        difficultyComboBox.setMaximumSize(new Dimension(320, 45));
        difficultyComboBox.setPreferredSize(new Dimension(320, 45));
        difficultyComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Difficulty Info Cards Container
        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setMaximumSize(new Dimension(500, 100));

        cardsPanel.add(createCard("Easy", "1 - 50", "10 Attempts", GameConstants.COLOR_EASY));
        cardsPanel.add(createCard("Medium", "1 - 100", "7 Attempts", GameConstants.COLOR_MEDIUM));
        cardsPanel.add(createCard("Hard", "1 - 200", "5 Attempts", GameConstants.COLOR_HARD));

        // Start Game Button
        JButton startButton = new JButton("START GAME");
        startButton.setFont(GameConstants.FONT_BUTTON);
        startButton.setForeground(Color.WHITE);
        startButton.setBackground(GameConstants.ACCENT_PRIMARY);
        startButton.setFocusPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setMaximumSize(new Dimension(280, 50));
        startButton.setPreferredSize(new Dimension(280, 50));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        startButton.addActionListener(e -> {
            Difficulty selected = (Difficulty) difficultyComboBox.getSelectedItem();
            if (onStartGameCallback != null && selected != null) {
                onStartGameCallback.accept(selected);
            }
        });

        // Assemble Content Box
        contentBox.add(titleLabel);
        contentBox.add(Box.createVerticalStrut(8));
        contentBox.add(subtitleLabel);
        contentBox.add(Box.createVerticalStrut(35));
        contentBox.add(diffPromptLabel);
        contentBox.add(Box.createVerticalStrut(15));
        contentBox.add(difficultyComboBox);
        contentBox.add(Box.createVerticalStrut(20));
        contentBox.add(cardsPanel);
        contentBox.add(Box.createVerticalStrut(35));
        contentBox.add(startButton);

        add(contentBox);
    }

    private JPanel createCard(String title, String range, String attempts, Color badgeColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(GameConstants.BG_INPUT);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameConstants.BORDER_COLOR, 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLbl.setForeground(badgeColor);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rangeLbl = new JLabel(range);
        rangeLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rangeLbl.setForeground(GameConstants.TEXT_PRIMARY);
        rangeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel attLbl = new JLabel(attempts);
        attLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        attLbl.setForeground(GameConstants.TEXT_SECONDARY);
        attLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(rangeLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(attLbl);

        return card;
    }
}
