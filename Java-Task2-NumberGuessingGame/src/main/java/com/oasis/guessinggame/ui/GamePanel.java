package com.oasis.guessinggame.ui;

import com.oasis.guessinggame.model.Difficulty;
import com.oasis.guessinggame.model.GameRound;
import com.oasis.guessinggame.model.GuessFeedback;
import com.oasis.guessinggame.service.NumberGuessingGame;
import com.oasis.guessinggame.util.GameConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Main active gameplay panel containing game controls, input field,
 * dynamic feedback banner, attempts progress bar, and action buttons.
 */
public class GamePanel extends JPanel {

    private final NumberGuessingGame gameService;
    private final Consumer<String> onStateChangedCallback;

    private JComboBox<Difficulty> difficultyComboBox;
    private JLabel rangeValueLabel;
    private JLabel attemptsValueLabel;
    private JProgressBar attemptsProgressBar;
    private JLabel rangeHintLabel;

    private JTextField guessTextField;
    private JButton guessButton;
    private JLabel resultBannerLabel;
    private JLabel resultDescriptionLabel;

    private JButton playAgainButton;
    private JButton newGameButton;
    private JButton resetScoreButton;

    public GamePanel(NumberGuessingGame gameService, Consumer<String> onStateChangedCallback) {
        this.gameService = gameService;
        this.onStateChangedCallback = onStateChangedCallback;
        initUI();
        refreshDisplayFromState();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 15));
        setBackground(GameConstants.BG_DARK);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // 1. Top Header & Difficulty Selector Bar
        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setOpaque(false);

        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);

        JLabel titleLabel = new JLabel("NUMBER GUESSING GAME");
        titleLabel.setFont(GameConstants.FONT_TITLE);
        titleLabel.setForeground(GameConstants.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Guess the hidden number before you run out of attempts.");
        subtitleLabel.setFont(GameConstants.FONT_SUBTITLE);
        subtitleLabel.setForeground(GameConstants.TEXT_SECONDARY);

        titleGroup.add(titleLabel);
        titleGroup.add(Box.createVerticalStrut(2));
        titleGroup.add(subtitleLabel);

        JPanel diffSelectPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        diffSelectPanel.setOpaque(false);

        JLabel diffLabel = new JLabel("Difficulty:");
        diffLabel.setFont(GameConstants.FONT_BUTTON);
        diffLabel.setForeground(GameConstants.TEXT_PRIMARY);

        difficultyComboBox = new JComboBox<>(Difficulty.values());
        difficultyComboBox.setSelectedItem(gameService.getCurrentDifficulty());
        difficultyComboBox.setFont(GameConstants.FONT_BUTTON);
        difficultyComboBox.addActionListener(e -> {
            Difficulty selected = (Difficulty) difficultyComboBox.getSelectedItem();
            if (selected != null && selected != gameService.getCurrentDifficulty()) {
                startNewRound(selected);
            }
        });

        diffSelectPanel.add(diffLabel);
        diffSelectPanel.add(difficultyComboBox);

        topBar.add(titleGroup, BorderLayout.WEST);
        topBar.add(diffSelectPanel, BorderLayout.EAST);

        // 2. Info Cards (Difficulty, Range, Attempts, Progress Bar)
        JPanel cardsGrid = new JPanel(new GridLayout(1, 3, 15, 0));
        cardsGrid.setOpaque(false);

        rangeValueLabel = new JLabel("1 - 100", SwingConstants.CENTER);
        attemptsValueLabel = new JLabel("0 / 7", SwingConstants.CENTER);

        cardsGrid.add(createCard("TARGET RANGE", rangeValueLabel, GameConstants.ACCENT_PRIMARY));

        // Attempts Card with embedded Progress Bar
        JPanel attemptsCard = createCard("ATTEMPTS LEFT", attemptsValueLabel, GameConstants.COLOR_MEDIUM);
        attemptsProgressBar = new JProgressBar(0, 100);
        attemptsProgressBar.setValue(100);
        attemptsProgressBar.setPreferredSize(new Dimension(140, 8));
        attemptsProgressBar.setMaximumSize(new Dimension(180, 8));
        attemptsProgressBar.setOpaque(false);
        attemptsCard.add(attemptsProgressBar, BorderLayout.SOUTH);
        cardsGrid.add(attemptsCard);

        cardsGrid.add(createCard("ROUND STATUS", rangeHintLabel = new JLabel("Range: 1 - 100", SwingConstants.CENTER), GameConstants.TEXT_PRIMARY));

        // 3. Central Game Input & Result Area
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(GameConstants.BG_CARD);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameConstants.BORDER_COLOR, 1),
                new EmptyBorder(25, 30, 25, 30)
        ));

        // Guess Input Group
        JLabel enterPrompt = new JLabel("Enter Your Guess", SwingConstants.CENTER);
        enterPrompt.setFont(GameConstants.FONT_HEADER);
        enterPrompt.setForeground(GameConstants.TEXT_PRIMARY);
        enterPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

        guessTextField = new JTextField();
        guessTextField.setFont(GameConstants.FONT_INPUT);
        guessTextField.setHorizontalAlignment(JTextField.CENTER);
        guessTextField.setBackground(GameConstants.BG_INPUT);
        guessTextField.setForeground(GameConstants.TEXT_PRIMARY);
        guessTextField.setCaretColor(GameConstants.TEXT_PRIMARY);
        guessTextField.setMaximumSize(new Dimension(300, 50));
        guessTextField.setPreferredSize(new Dimension(300, 50));
        guessTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        guessTextField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameConstants.ACCENT_PRIMARY, 2),
                new EmptyBorder(5, 10, 5, 10)
        ));

        guessTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && guessButton.isEnabled()) {
                    handleGuessSubmitted();
                }
            }
        });

        guessButton = new JButton("GUESS");
        guessButton.setFont(GameConstants.FONT_BUTTON);
        guessButton.setForeground(Color.WHITE);
        guessButton.setBackground(GameConstants.ACCENT_PRIMARY);
        guessButton.setFocusPainted(false);
        guessButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        guessButton.setMaximumSize(new Dimension(300, 48));
        guessButton.setPreferredSize(new Dimension(300, 48));
        guessButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        guessButton.addActionListener(e -> handleGuessSubmitted());

        // Result Banner Container
        JPanel bannerPanel = new JPanel();
        bannerPanel.setLayout(new BoxLayout(bannerPanel, BoxLayout.Y_AXIS));
        bannerPanel.setBackground(GameConstants.BG_INPUT);
        bannerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameConstants.BORDER_COLOR, 1),
                new EmptyBorder(15, 20, 15, 20)
        ));
        bannerPanel.setMaximumSize(new Dimension(600, 100));
        bannerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultBannerLabel = new JLabel("Make your guess!", SwingConstants.CENTER);
        resultBannerLabel.setFont(GameConstants.FONT_RESULT);
        resultBannerLabel.setForeground(GameConstants.TEXT_PRIMARY);
        resultBannerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resultDescriptionLabel = new JLabel("Enter a number above to start.", SwingConstants.CENTER);
        resultDescriptionLabel.setFont(GameConstants.FONT_HINT);
        resultDescriptionLabel.setForeground(GameConstants.TEXT_SECONDARY);
        resultDescriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bannerPanel.add(resultBannerLabel);
        bannerPanel.add(Box.createVerticalStrut(4));
        bannerPanel.add(resultDescriptionLabel);

        centerPanel.add(enterPrompt);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(guessTextField);
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(guessButton);
        centerPanel.add(Box.createVerticalStrut(25));
        centerPanel.add(bannerPanel);

        // 4. Action Buttons Bar
        JPanel actionsBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actionsBar.setOpaque(false);

        playAgainButton = new JButton("Play Again");
        playAgainButton.setFont(GameConstants.FONT_BUTTON);
        playAgainButton.setForeground(Color.WHITE);
        playAgainButton.setBackground(GameConstants.COLOR_CORRECT);
        playAgainButton.setFocusPainted(false);
        playAgainButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        playAgainButton.setPreferredSize(new Dimension(160, 45));
        playAgainButton.setEnabled(false);
        playAgainButton.addActionListener(e -> startNewRound(gameService.getCurrentDifficulty()));

        newGameButton = new JButton("New Game");
        newGameButton.setFont(GameConstants.FONT_BUTTON);
        newGameButton.setForeground(GameConstants.TEXT_PRIMARY);
        newGameButton.setBackground(GameConstants.BG_CARD_HOVER);
        newGameButton.setFocusPainted(false);
        newGameButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newGameButton.setPreferredSize(new Dimension(160, 45));
        newGameButton.addActionListener(e -> startNewRound(gameService.getCurrentDifficulty()));

        resetScoreButton = new JButton("Reset Score");
        resetScoreButton.setFont(GameConstants.FONT_BUTTON);
        resetScoreButton.setForeground(Color.WHITE);
        resetScoreButton.setBackground(GameConstants.COLOR_HARD);
        resetScoreButton.setFocusPainted(false);
        resetScoreButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetScoreButton.setPreferredSize(new Dimension(160, 45));
        resetScoreButton.addActionListener(e -> handleResetScoreRequested());

        actionsBar.add(playAgainButton);
        actionsBar.add(newGameButton);
        actionsBar.add(resetScoreButton);

        // Main Layout Assembly
        JPanel headerSection = new JPanel(new BorderLayout(0, 15));
        headerSection.setOpaque(false);
        headerSection.add(topBar, BorderLayout.NORTH);
        headerSection.add(cardsGrid, BorderLayout.SOUTH);

        add(headerSection, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(actionsBar, BorderLayout.SOUTH);
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(GameConstants.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GameConstants.BORDER_COLOR, 1),
                new EmptyBorder(12, 15, 12, 15)
        ));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(GameConstants.FONT_CARD_LABEL);
        titleLbl.setForeground(GameConstants.TEXT_SECONDARY);

        valueLabel.setFont(GameConstants.FONT_CARD_VALUE);
        valueLabel.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void handleGuessSubmitted() {
        String input = guessTextField.getText();
        GuessFeedback feedback = gameService.processGuess(input);

        // Update UI Banner
        updateBanner(feedback);

        // Clear text field & focus
        guessTextField.setText("");
        guessTextField.requestFocusInWindow();

        // Refresh stats & round cards
        refreshDisplayFromState();

        if (onStateChangedCallback != null) {
            onStateChangedCallback.accept("GUESS_SUBMITTED");
        }
    }

    private void updateBanner(GuessFeedback feedback) {
        resultBannerLabel.setText(feedback.getResult().getTitle());
        resultDescriptionLabel.setText(feedback.getMessage());

        switch (feedback.getResult()) {
            case TOO_HIGH:
                resultBannerLabel.setForeground(GameConstants.COLOR_TOO_HIGH);
                break;
            case TOO_LOW:
                resultBannerLabel.setForeground(GameConstants.COLOR_TOO_LOW);
                break;
            case CORRECT:
                resultBannerLabel.setForeground(GameConstants.COLOR_CORRECT);
                break;
            case EXHAUSTED:
                resultBannerLabel.setForeground(GameConstants.COLOR_LOST);
                break;
            case OUT_OF_BOUNDS:
            case INVALID_FORMAT:
                resultBannerLabel.setForeground(GameConstants.COLOR_ERROR);
                break;
            default:
                resultBannerLabel.setForeground(GameConstants.TEXT_PRIMARY);
                break;
        }

        if (feedback.isRoundFinished()) {
            guessTextField.setEnabled(false);
            guessButton.setEnabled(false);
            playAgainButton.setEnabled(true);
            playAgainButton.requestFocusInWindow();
        }
    }

    private void startNewRound(Difficulty difficulty) {
        gameService.startNewRound(difficulty);
        difficultyComboBox.setSelectedItem(difficulty);

        guessTextField.setEnabled(true);
        guessTextField.setText("");
        guessButton.setEnabled(true);
        playAgainButton.setEnabled(false);

        resultBannerLabel.setText("Make your guess!");
        resultBannerLabel.setForeground(GameConstants.TEXT_PRIMARY);
        resultDescriptionLabel.setText("Enter a whole number within the difficulty range.");

        refreshDisplayFromState();
        guessTextField.requestFocusInWindow();

        if (onStateChangedCallback != null) {
            onStateChangedCallback.accept("NEW_ROUND");
        }
    }

    private void handleResetScoreRequested() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset all score statistics and round history?",
                "Confirm Reset Score",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            gameService.resetScore();
            startNewRound(gameService.getCurrentDifficulty());
            if (onStateChangedCallback != null) {
                onStateChangedCallback.accept("RESET_SCORE");
            }
        }
    }

    public void refreshDisplayFromState() {
        GameRound round = gameService.getCurrentRound();
        if (round == null) {
            return;
        }

        Difficulty diff = round.getDifficulty();
        rangeValueLabel.setText(diff.getRangeText());

        int used = round.getAttemptsUsed();
        int max = diff.getMaxAttempts();
        int remaining = round.getAttemptsRemaining();

        attemptsValueLabel.setText(remaining + " Left (" + used + "/" + max + ")");

        // Progress bar percentage
        int pct = (int) (((double) remaining / max) * 100);
        attemptsProgressBar.setValue(pct);
        if (pct > 50) {
            attemptsProgressBar.setForeground(GameConstants.PROGRESS_HIGH);
        } else if (pct > 25) {
            attemptsProgressBar.setForeground(GameConstants.PROGRESS_MID);
        } else {
            attemptsProgressBar.setForeground(GameConstants.PROGRESS_LOW);
        }

        rangeHintLabel.setText("Valid: " + round.getMinHint() + " - " + round.getMaxHint());
    }
}
