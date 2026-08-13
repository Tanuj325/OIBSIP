package com.oasis.guessinggame.ui;

import com.oasis.guessinggame.model.Difficulty;
import com.oasis.guessinggame.service.NumberGuessingGame;
import com.oasis.guessinggame.util.GameConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Top-level JFrame desktop application container.
 * Opens maximized and manages card layout navigation between Start screen and Gameplay/Statistics dashboard.
 */
public class MainFrame extends JFrame {

    private final NumberGuessingGame gameService;
    private final CardLayout cardLayout;
    private final JPanel mainContainer;

    private StartPanel startPanel;
    private GamePanel gamePanel;
    private StatisticsPanel statisticsPanel;
    private JTabbedPane tabbedPane;

    public MainFrame() {
        super("Number Guessing Game");
        this.gameService = new NumberGuessingGame();
        this.cardLayout = new CardLayout();
        this.mainContainer = new JPanel(cardLayout);

        initUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1150, 780));
        setPreferredSize(new Dimension(1280, 850));

        // Open Maximized on Desktop
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // 1. Initialize Views
        startPanel = new StartPanel(this::handleGameStarted);

        statisticsPanel = new StatisticsPanel(() -> {
            gameService.resetScore();
            if (gamePanel != null) {
                gamePanel.refreshDisplayFromState();
            }
            if (statisticsPanel != null) {
                statisticsPanel.updateStats(gameService.getStatistics());
            }
        });

        gamePanel = new GamePanel(gameService, state -> {
            if (statisticsPanel != null) {
                statisticsPanel.updateStats(gameService.getStatistics());
            }
        });

        // 2. Tabbed Dashboard Container (Gameplay + Statistics)
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(GameConstants.FONT_HEADER);
        tabbedPane.setBackground(GameConstants.BG_DARK);
        tabbedPane.setForeground(GameConstants.TEXT_PRIMARY);

        tabbedPane.addTab("🎮  GAMEPLAY", gamePanel);
        tabbedPane.addTab("📊  STATS & HISTORY", statisticsPanel);

        // 3. Add Screens to CardLayout
        mainContainer.add(startPanel, "START_SCREEN");
        mainContainer.add(tabbedPane, "GAME_SCREEN");

        add(mainContainer);

        // Show Welcome Screen initially
        cardLayout.show(mainContainer, "START_SCREEN");
    }

    private void handleGameStarted(Difficulty selectedDifficulty) {
        gameService.startNewRound(selectedDifficulty);
        gamePanel.refreshDisplayFromState();
        statisticsPanel.updateStats(gameService.getStatistics());

        cardLayout.show(mainContainer, "GAME_SCREEN");
        tabbedPane.setSelectedIndex(0); // Switch to Gameplay tab
    }
}
