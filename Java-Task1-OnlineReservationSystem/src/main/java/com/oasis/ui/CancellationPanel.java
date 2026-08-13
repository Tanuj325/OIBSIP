package com.oasis.ui;

import com.oasis.model.Reservation;
import com.oasis.model.User;
import com.oasis.service.ReservationService;
import com.oasis.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Cancellation Panel with role-based SQL ownership checks and high contrast UI.
 */
public class CancellationPanel extends JPanel {

    private final User currentUser;
    private final MainFrame mainFrame;
    private final ReservationService reservationService;

    private JTextField txtPnrInput;
    private JButton btnFetch;
    private JButton btnConfirmCancel;
    private JButton btnClear;

    private JTextField txtPnrDisplay;
    private JTextField txtPassengerName;
    private JTextField txtTrainNumber;
    private JTextField txtTrainName;
    private JTextField txtClassType;
    private JTextField txtJourneyDate;
    private JTextField txtSourceStation;
    private JTextField txtDestinationStation;

    private Reservation currentFetchedReservation = null;

    public CancellationPanel(User currentUser, MainFrame mainFrame) {
        this.currentUser = currentUser;
        this.mainFrame = mainFrame;
        this.reservationService = new ReservationService();
        initUI();
    }

    public CancellationPanel(MainFrame mainFrame) {
        this(mainFrame.getCurrentUser(), mainFrame);
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Cancel Reservation");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(240, 246, 252));

        JLabel subtitleLabel = new JLabel("Fetch booking by PNR number to review details and request cancellation");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(139, 148, 158));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // PNR Lookup Search Section
        JPanel pnrSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        pnrSearchPanel.setOpaque(false);
        pnrSearchPanel.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(48, 54, 61), 1),
                        "Search Reservation",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        new Color(56, 139, 253)
                ),
                new EmptyBorder(8, 15, 8, 15)
        ));
        pnrSearchPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPnr = new JLabel("PNR Number:");
        lblPnr.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPnr.setForeground(new Color(201, 209, 217));

        txtPnrInput = new JTextField(18);
        txtPnrInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPnrInput.setPreferredSize(new Dimension(200, 38));
        txtPnrInput.setBackground(new Color(37, 43, 54));
        txtPnrInput.setForeground(new Color(240, 246, 252));
        txtPnrInput.setCaretColor(Color.WHITE);
        txtPnrInput.setBorder(new CompoundBorder(
                new LineBorder(new Color(68, 76, 89), 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));

        btnFetch = new JButton("Fetch Booking");
        btnFetch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnFetch.setPreferredSize(new Dimension(140, 38));
        btnFetch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        pnrSearchPanel.add(lblPnr);
        pnrSearchPanel.add(txtPnrInput);
        pnrSearchPanel.add(btnFetch);

        contentPanel.add(pnrSearchPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Booking Details Section (Read-only)
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(48, 54, 61), 1),
                        "Reservation Details",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        new Color(56, 139, 253)
                ),
                new EmptyBorder(15, 20, 15, 20)
        ));
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addDetailRow(detailsPanel, gbc, row++, "PNR Number:", txtPnrDisplay = createReadOnlyTextField());
        addDetailRow(detailsPanel, gbc, row++, "Passenger Name:", txtPassengerName = createReadOnlyTextField());
        addDetailRow(detailsPanel, gbc, row++, "Train Number:", txtTrainNumber = createReadOnlyTextField());
        addDetailRow(detailsPanel, gbc, row++, "Train Name:", txtTrainName = createReadOnlyTextField());
        addDetailRow(detailsPanel, gbc, row++, "Class Type:", txtClassType = createReadOnlyTextField());
        addDetailRow(detailsPanel, gbc, row++, "Journey Date:", txtJourneyDate = createReadOnlyTextField());
        addDetailRow(detailsPanel, gbc, row++, "Source Station:", txtSourceStation = createReadOnlyTextField());
        addDetailRow(detailsPanel, gbc, row++, "Destination Station:", txtDestinationStation = createReadOnlyTextField());

        contentPanel.add(detailsPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Button Bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonBar.setOpaque(false);
        buttonBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnClear.setPreferredSize(new Dimension(110, 42));
        btnClear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnConfirmCancel = new JButton("Cancel Reservation");
        btnConfirmCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnConfirmCancel.setPreferredSize(new Dimension(190, 42));
        btnConfirmCancel.setBackground(new Color(218, 54, 51));
        btnConfirmCancel.setForeground(Color.WHITE);
        btnConfirmCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConfirmCancel.setEnabled(false);

        buttonBar.add(btnClear);
        buttonBar.add(btnConfirmCancel);

        contentPanel.add(buttonBar);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(28, 33, 40));

        add(scrollPane, BorderLayout.CENTER);

        // Listeners
        btnFetch.addActionListener(e -> fetchBooking());
        txtPnrInput.addActionListener(e -> fetchBooking());
        btnConfirmCancel.addActionListener(e -> confirmAndCancelBooking());
        btnClear.addActionListener(e -> resetForm());
    }

    private JTextField createReadOnlyTextField() {
        JTextField tf = new JTextField(22);
        tf.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tf.setEditable(false);
        tf.setBackground(new Color(45, 51, 59));
        tf.setForeground(new Color(240, 246, 252));
        tf.setBorder(new CompoundBorder(
                new LineBorder(new Color(68, 76, 89), 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JTextField tf) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.25;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(201, 209, 217));
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.75;
        panel.add(tf, gbc);
    }

    private void fetchBooking() {
        String pnr = txtPnrInput.getText().trim();

        if (ValidationUtil.isNullOrEmpty(pnr)) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid PNR Number.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        btnFetch.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            // Enforces ownership in SQL query
            Reservation res = reservationService.getReservationByPnr(pnr, currentUser);

            if (res != null) {
                currentFetchedReservation = res;
                displayBookingDetails(res);
                btnConfirmCancel.setEnabled(true);
            } else {
                clearBookingDetails();
                btnConfirmCancel.setEnabled(false);
                JOptionPane.showMessageDialog(this,
                        "Booking not found or you do not have permission to access this PNR.",
                        "PNR Lookup Result",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Database query error: " + ex.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            btnFetch.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void displayBookingDetails(Reservation res) {
        txtPnrDisplay.setText(res.getPnr());
        txtPassengerName.setText(res.getPassengerName());
        txtTrainNumber.setText(String.valueOf(res.getTrainNumber()));
        txtTrainName.setText(res.getTrainName());
        txtClassType.setText(res.getClassType());
        txtJourneyDate.setText(res.getJourneyDate().toString());
        txtSourceStation.setText(res.getSourceStation());
        txtDestinationStation.setText(res.getDestinationStation());
    }

    private void confirmAndCancelBooking() {
        if (currentFetchedReservation == null) {
            JOptionPane.showMessageDialog(this,
                    "No valid booking selected for cancellation.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel this reservation?\nPNR: " + currentFetchedReservation.getPnr(),
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        btnConfirmCancel.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            // Enforces ownership in SQL query
            boolean success = reservationService.cancelReservation(currentFetchedReservation.getPnr(), currentUser);

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Reservation cancelled successfully.",
                        "Cancellation Confirmed",
                        JOptionPane.INFORMATION_MESSAGE);

                resetForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to cancel reservation. It may have already been cancelled.",
                        "Cancellation Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error during cancellation: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void clearBookingDetails() {
        currentFetchedReservation = null;
        txtPnrDisplay.setText("");
        txtPassengerName.setText("");
        txtTrainNumber.setText("");
        txtTrainName.setText("");
        txtClassType.setText("");
        txtJourneyDate.setText("");
        txtSourceStation.setText("");
        txtDestinationStation.setText("");
    }

    public void resetForm() {
        txtPnrInput.setText("");
        clearBookingDetails();
        btnConfirmCancel.setEnabled(false);
    }

    public void clearForm() {
        resetForm();
    }
}
