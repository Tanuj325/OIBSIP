package com.oasis.ui;

import com.oasis.model.User;
import com.oasis.service.ReservationService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Dashboard Content Panel featuring real-time MySQL database metric cards.
 */
public class DashboardPanel extends JPanel {

    private final User currentUser;
    private final MainFrame mainFrame;
    private final ReservationService reservationService;

    private JLabel lblTotalTrainsValue;
    private JLabel lblTotalReservationsValue;
    private JLabel lblUserReservationsValue;

    public DashboardPanel(User currentUser, MainFrame mainFrame) {
        this.currentUser = currentUser;
        this.mainFrame = mainFrame;
        this.reservationService = new ReservationService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);

        // Header Welcome Panel
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setOpaque(false);

        String name = (currentUser != null && currentUser.getFullName() != null) 
                ? currentUser.getFullName() : (currentUser != null ? currentUser.getUsername() : "User");

        JLabel titleLabel = new JLabel("Welcome back, " + name + "!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subtitleLabel = new JLabel("Overview of system statistics and quick reservation actions");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(139, 148, 158));

        welcomePanel.add(titleLabel, BorderLayout.NORTH);
        welcomePanel.add(subtitleLabel, BorderLayout.SOUTH);

        add(welcomePanel, BorderLayout.NORTH);

        // Center Content Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        // Metric Cards Section
        JLabel metricsHeading = new JLabel("DATABASE METRICS");
        metricsHeading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        metricsHeading.setForeground(new Color(139, 148, 158));
        metricsHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerPanel.add(metricsHeading);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel cardsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsGrid.setOpaque(false);
        cardsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        cardsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Metric 1: Total Trains
        lblTotalTrainsValue = new JLabel("0", SwingConstants.CENTER);
        JPanel cardTrains = createStatCard("Active Trains", lblTotalTrainsValue, new Color(56, 139, 253));

        // Metric 2: Total Reservations
        lblTotalReservationsValue = new JLabel("0", SwingConstants.CENTER);
        JPanel cardTotalRes = createStatCard("Total System Bookings", lblTotalReservationsValue, new Color(46, 160, 67));

        // Metric 3: User Bookings
        lblUserReservationsValue = new JLabel("0", SwingConstants.CENTER);
        JPanel cardUserRes = createStatCard("Your Bookings", lblUserReservationsValue, new Color(163, 113, 247));

        cardsGrid.add(cardTrains);
        cardsGrid.add(cardTotalRes);
        cardsGrid.add(cardUserRes);

        centerPanel.add(cardsGrid);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Quick Actions Section
        JLabel actionsHeading = new JLabel("QUICK ACTIONS");
        actionsHeading.setFont(new Font("Segoe UI", Font.BOLD, 13));
        actionsHeading.setForeground(new Color(139, 148, 158));
        actionsHeading.setAlignmentX(Component.LEFT_ALIGNMENT);

        centerPanel.add(actionsHeading);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel actionsGrid = new JPanel(new GridLayout(1, 2, 20, 0));
        actionsGrid.setOpaque(false);
        actionsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        actionsGrid.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel actionBook = createQuickActionCard(
                "Book New Ticket",
                "Reserve train tickets by selecting trains and journey dates.",
                "New Reservation",
                e -> mainFrame.showReservation()
        );

        JPanel actionCancel = createQuickActionCard(
                "Cancel Ticket",
                "Lookup an existing reservation by PNR and request cancellation.",
                "Cancel Reservation",
                e -> mainFrame.showCancellation()
        );

        actionsGrid.add(actionBook);
        actionsGrid.add(actionCancel);

        centerPanel.add(actionsGrid);

        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(new Color(32, 38, 46));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(48, 54, 61), 1, true),
                new EmptyBorder(15, 18, 15, 18)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(new Color(139, 148, 158));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        valueLabel.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createQuickActionCard(String title, String description, String buttonText, java.awt.event.ActionListener listener) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(new Color(32, 38, 46));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(48, 54, 61), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        textPanel.setOpaque(false);

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel descLbl = new JLabel("<html>" + description + "</html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLbl.setForeground(new Color(139, 148, 158));

        textPanel.add(titleLbl);
        textPanel.add(descLbl);

        JButton actionBtn = new JButton(buttonText);
        actionBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        actionBtn.setPreferredSize(new Dimension(160, 36));
        actionBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        actionBtn.addActionListener(listener);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(actionBtn);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(btnPanel, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Refresh database metrics dynamically from MySQL.
     */
    public void refreshMetrics() {
        try {
            int totalTrains = reservationService.getTrainCount();
            int totalRes = reservationService.getTotalReservationCount();
            int userRes = reservationService.getUserReservationCount(currentUser);

            lblTotalTrainsValue.setText(String.valueOf(totalTrains));
            lblTotalReservationsValue.setText(String.valueOf(totalRes));
            lblUserReservationsValue.setText(String.valueOf(userRes));
        } catch (Exception e) {
            lblTotalTrainsValue.setText("N/A");
            lblTotalReservationsValue.setText("N/A");
            lblUserReservationsValue.setText("N/A");
        }
    }
}
