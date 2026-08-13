package com.oasis.ui;

import com.oasis.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Main Full-Screen Desktop Window with CardLayout navigation and role-based views.
 */
public class MainFrame extends JFrame {

    private final User currentUser;
    private CardLayout cardLayout;
    private JPanel contentCardPanel;

    private DashboardPanel dashboardPanel;
    private ReservationPanel reservationPanel;
    private ViewReservationsPanel viewReservationsPanel;
    private CancellationPanel cancellationPanel;

    private JButton btnNavDashboard;
    private JButton btnNavReservation;
    private JButton btnNavViewReservations;
    private JButton btnNavCancellation;
    private JButton btnNavLogout;

    public MainFrame(User currentUser) {
        this.currentUser = currentUser;
        initUI();
    }

    private void initUI() {
        setTitle("Online Reservation System - Desktop Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Full-screen desktop window
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1024, 720));
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout());

        // 1. TOP HEADER PANEL
        JPanel headerPanel = createTopHeaderPanel();
        rootPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. LEFT SIDEBAR PANEL
        JPanel sidebarPanel = createLeftSidebarPanel();
        rootPanel.add(sidebarPanel, BorderLayout.WEST);

        // 3. CENTER CONTENT CARD PANEL
        cardLayout = new CardLayout();
        contentCardPanel = new JPanel(cardLayout);
        contentCardPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        contentCardPanel.setBackground(new Color(28, 33, 40));

        // Instantiate Sub-panels
        dashboardPanel = new DashboardPanel(currentUser, this);
        reservationPanel = new ReservationPanel(currentUser, this);
        viewReservationsPanel = new ViewReservationsPanel(currentUser, this);
        cancellationPanel = new CancellationPanel(currentUser, this);

        contentCardPanel.add(dashboardPanel, "DASHBOARD");
        contentCardPanel.add(reservationPanel, "RESERVATION");
        contentCardPanel.add(viewReservationsPanel, "VIEW_RESERVATIONS");
        contentCardPanel.add(cancellationPanel, "CANCELLATION");

        rootPanel.add(contentCardPanel, BorderLayout.CENTER);

        add(rootPanel);

        // Default view: Dashboard
        showDashboard();
    }

    private JPanel createTopHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(23, 27, 33));
        header.setBorder(new EmptyBorder(14, 25, 14, 25));

        // Branding Title
        JLabel brandLabel = new JLabel("Online Reservation System");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandLabel.setForeground(new Color(240, 246, 252));

        // User Account Session Summary
        String displayName = (currentUser != null && currentUser.getFullName() != null) 
                ? currentUser.getFullName() : (currentUser != null ? currentUser.getUsername() : "User");
        String roleName = (currentUser != null && currentUser.getRole() != null) 
                ? currentUser.getRole() : "USER";

        JLabel userSessionLabel = new JLabel("Logged in as: " + displayName + " (" + roleName + ")  ");
        userSessionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userSessionLabel.setForeground(new Color(201, 209, 217));

        header.add(brandLabel, BorderLayout.WEST);
        header.add(userSessionLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createLeftSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(32, 38, 46));
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel navTitle = new JLabel("NAVIGATION");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        navTitle.setForeground(new Color(139, 148, 158));
        navTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(navTitle);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));

        boolean isAdmin = currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
        String viewResText = isAdmin ? "View Reservations (All)" : "View Reservations";

        btnNavDashboard = createSidebarButton("Dashboard");
        btnNavReservation = createSidebarButton("New Reservation");
        btnNavViewReservations = createSidebarButton(viewResText);
        btnNavCancellation = createSidebarButton("Cancel Reservation");
        btnNavLogout = createSidebarButton("Logout");

        sidebar.add(btnNavDashboard);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnNavReservation);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnNavViewReservations);
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(btnNavCancellation);
        sidebar.add(Box.createRigidArea(new Dimension(0, 25)));
        sidebar.add(new JSeparator(SwingConstants.HORIZONTAL));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(btnNavLogout);

        // Listeners
        btnNavDashboard.addActionListener(e -> showDashboard());
        btnNavReservation.addActionListener(e -> showReservation());
        btnNavViewReservations.addActionListener(e -> showViewReservations());
        btnNavCancellation.addActionListener(e -> showCancellation());
        btnNavLogout.addActionListener(e -> logout());

        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setMaximumSize(new Dimension(220, 42));
        btn.setPreferredSize(new Dimension(220, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setForeground(new Color(230, 237, 243));
        btn.setBackground(new Color(37, 43, 54));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void updateActiveButtonState(JButton activeBtn) {
        Color defaultBg = new Color(37, 43, 54);
        Color defaultFg = new Color(230, 237, 243);
        Color activeBg = new Color(56, 139, 253);
        Color activeFg = Color.WHITE;

        btnNavDashboard.setBackground(defaultBg); btnNavDashboard.setForeground(defaultFg);
        btnNavReservation.setBackground(defaultBg); btnNavReservation.setForeground(defaultFg);
        btnNavViewReservations.setBackground(defaultBg); btnNavViewReservations.setForeground(defaultFg);
        btnNavCancellation.setBackground(defaultBg); btnNavCancellation.setForeground(defaultFg);

        if (activeBtn != null) {
            activeBtn.setBackground(activeBg);
            activeBtn.setForeground(activeFg);
        }
    }

    public void showDashboard() {
        dashboardPanel.refreshMetrics();
        cardLayout.show(contentCardPanel, "DASHBOARD");
        updateActiveButtonState(btnNavDashboard);
    }

    public void showReservation() {
        reservationPanel.refreshTrainDropdown();
        cardLayout.show(contentCardPanel, "RESERVATION");
        updateActiveButtonState(btnNavReservation);
    }

    public void showViewReservations() {
        viewReservationsPanel.refreshTableData();
        cardLayout.show(contentCardPanel, "VIEW_RESERVATIONS");
        updateActiveButtonState(btnNavViewReservations);
    }

    public void showCancellation() {
        cancellationPanel.resetForm();
        cardLayout.show(contentCardPanel, "CANCELLATION");
        updateActiveButtonState(btnNavCancellation);
    }

    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out of the system?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose();
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
