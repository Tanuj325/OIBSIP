package com.oasis.ui;

import com.oasis.model.Reservation;
import com.oasis.model.User;
import com.oasis.service.ReservationService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

/**
 * View Reservations Panel featuring dynamic JTable and role-based data visibility.
 */
public class ViewReservationsPanel extends JPanel {

    private final User currentUser;
    private final MainFrame mainFrame;
    private final ReservationService reservationService;

    private JLabel lblTitle;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnRefresh;

    private JTable tableReservations;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;
    private JLabel lblEmptyState;

    public ViewReservationsPanel(User currentUser, MainFrame mainFrame) {
        this.currentUser = currentUser;
        this.mainFrame = mainFrame;
        this.reservationService = new ReservationService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);

        boolean isAdmin = currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        String titleText = isAdmin ? "All Reservations" : "My Reservations";
        lblTitle = new JLabel(titleText);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(240, 246, 252));

        String subtitleText = isAdmin 
                ? "Manage and review all passenger ticket reservations across system users" 
                : "Review your active train ticket bookings";
        JLabel lblSubtitle = new JLabel(subtitleText);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(139, 148, 158));

        headerPanel.add(lblTitle, BorderLayout.NORTH);
        headerPanel.add(lblSubtitle, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Center Container
        JPanel centerContainer = new JPanel(new BorderLayout(15, 15));
        centerContainer.setOpaque(false);

        // Search & Refresh Action Bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        searchBar.setOpaque(false);
        searchBar.setBorder(new CompoundBorder(
                new LineBorder(new Color(48, 54, 61), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSearch.setForeground(new Color(201, 209, 217));

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(220, 34));
        txtSearch.setToolTipText(isAdmin ? "Search by PNR, Username, Passenger, or Train" : "Search by PNR, Passenger, or Train");

        btnSearch = new JButton("Search");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSearch.setPreferredSize(new Dimension(100, 34));
        btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JButton btnClearSearch = new JButton("Clear");
        btnClearSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnClearSearch.setPreferredSize(new Dimension(90, 34));
        btnClearSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefresh.setPreferredSize(new Dimension(100, 34));
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        searchBar.add(lblSearch);
        searchBar.add(txtSearch);
        searchBar.add(btnSearch);
        searchBar.add(btnClearSearch);
        searchBar.add(btnRefresh);

        btnClearSearch.addActionListener(e -> refreshTableData());

        centerContainer.add(searchBar, BorderLayout.NORTH);

        // Table Setup
        String[] columnNames;
        if (isAdmin) {
            columnNames = new String[]{
                    "PNR", "Username", "Passenger Name", "Train No.", "Train Name", 
                    "Class", "Journey Date", "Source", "Destination", "Booking Date"
            };
        } else {
            columnNames = new String[]{
                    "PNR", "Passenger Name", "Train", "Class", 
                    "Journey Date", "Source", "Destination", "Booking Date"
            };
        }

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only cells
            }
        };

        tableReservations = new JTable(tableModel);
        tableReservations.setRowHeight(34);
        tableReservations.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // High Contrast Table Colors & Header Styling
        tableReservations.setBackground(new Color(28, 33, 40));
        tableReservations.setForeground(new Color(230, 237, 243));
        tableReservations.setSelectionBackground(new Color(56, 139, 253));
        tableReservations.setSelectionForeground(Color.WHITE);
        tableReservations.setGridColor(new Color(48, 54, 61));

        JTableHeader header = tableReservations.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(37, 43, 54));
        header.setForeground(new Color(240, 246, 252));
        header.setPreferredSize(new Dimension(0, 38));

        // Center align text in specific columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableReservations.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(56, 139, 253));
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(28, 33, 40) : new Color(33, 38, 45));
                    c.setForeground(new Color(230, 237, 243));
                }
                return c;
            }
        });

        tableScrollPane = new JScrollPane(tableReservations);
        tableScrollPane.setBorder(new LineBorder(new Color(48, 54, 61), 1));
        tableScrollPane.getViewport().setBackground(new Color(28, 33, 40));

        // Empty State Label
        String emptyMessage = isAdmin ? "No reservations have been made yet." : "No reservations found.";
        lblEmptyState = new JLabel(emptyMessage, SwingConstants.CENTER);
        lblEmptyState.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblEmptyState.setForeground(new Color(139, 148, 158));
        lblEmptyState.setVisible(false);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.add(tableScrollPane, BorderLayout.CENTER);
        tableContainer.add(lblEmptyState, BorderLayout.SOUTH);

        centerContainer.add(tableContainer, BorderLayout.CENTER);

        add(centerContainer, BorderLayout.CENTER);

        // Listeners
        btnSearch.addActionListener(e -> performSearch());
        txtSearch.addActionListener(e -> performSearch());
        btnRefresh.addActionListener(e -> refreshTableData());

        // Load data on creation
        refreshTableData();
    }

    /**
     * Reload data from MySQL database using role-based query scoping.
     */
    public void refreshTableData() {
        txtSearch.setText("");
        loadReservations(reservationService.getReservationsForUser(currentUser));
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        List<Reservation> results = reservationService.searchReservations(keyword, currentUser);
        loadReservations(results);
    }

    private void loadReservations(List<Reservation> list) {
        tableModel.setRowCount(0);

        boolean isAdmin = currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());

        if (list == null || list.isEmpty()) {
            lblEmptyState.setVisible(true);
            tableScrollPane.setVisible(false);
            return;
        }

        lblEmptyState.setVisible(false);
        tableScrollPane.setVisible(true);

        for (Reservation r : list) {
            String bookingDateStr = (r.getCreatedAt() != null) 
                    ? r.getCreatedAt().toString().substring(0, 16) : "N/A";

            if (isAdmin) {
                tableModel.addRow(new Object[]{
                        r.getPnr(),
                        r.getUsername() != null ? r.getUsername() : "N/A",
                        r.getPassengerName(),
                        r.getTrainNumber(),
                        r.getTrainName(),
                        r.getClassType(),
                        r.getJourneyDate().toString(),
                        r.getSourceStation(),
                        r.getDestinationStation(),
                        bookingDateStr
                });
            } else {
                tableModel.addRow(new Object[]{
                        r.getPnr(),
                        r.getPassengerName(),
                        r.getTrainNumber() + " - " + r.getTrainName(),
                        r.getClassType(),
                        r.getJourneyDate().toString(),
                        r.getSourceStation(),
                        r.getDestinationStation(),
                        bookingDateStr
                });
            }
        }
    }
}
