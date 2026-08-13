package com.oasis.ui;

import com.oasis.model.Reservation;
import com.oasis.model.Train;
import com.oasis.model.User;
import com.oasis.service.ReservationService;
import com.oasis.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Reservation Panel featuring dynamic database-driven Train JComboBox selection and high UI contrast.
 */
public class ReservationPanel extends JPanel {

    private final User currentUser;
    private final MainFrame mainFrame;
    private final ReservationService reservationService;

    private JTextField txtPassengerName;
    private JComboBox<Train> comboTrainNumber;
    private JTextField txtTrainName;
    private JComboBox<String> comboClassType;
    private JTextField txtJourneyDate;
    private JTextField txtSourceStation;
    private JTextField txtDestinationStation;

    private JButton btnBook;
    private JButton btnClear;

    public ReservationPanel(User currentUser, MainFrame mainFrame) {
        this.currentUser = currentUser;
        this.mainFrame = mainFrame;
        this.reservationService = new ReservationService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(20, 20));
        setOpaque(false);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Book Your Journey");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(240, 246, 252));

        JLabel subtitleLabel = new JLabel("Select train, class, and journey date to reserve tickets");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(139, 148, 158));

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Main Form Container Panel
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);

        // Section 1: Passenger Information
        JPanel panelPassenger = createFormSectionPanel("1. Passenger Details");
        JPanel gridPassenger = new JPanel(new GridBagLayout());
        gridPassenger.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addLabel(gridPassenger, gbc, 0, 0, "Passenger Full Name *:");
        txtPassengerName = createStyledTextField(25);
        if (currentUser != null && currentUser.getFullName() != null) {
            txtPassengerName.setText(currentUser.getFullName());
        }
        addControl(gridPassenger, gbc, 0, 1, txtPassengerName);

        panelPassenger.add(gridPassenger);
        formContainer.add(panelPassenger);
        formContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Section 2: Journey Information
        JPanel panelJourney = createFormSectionPanel("2. Train & Journey Details");
        JPanel gridJourney = new JPanel(new GridBagLayout());
        gridJourney.setOpaque(false);
        GridBagConstraints gbcJ = new GridBagConstraints();
        gbcJ.insets = new Insets(8, 12, 8, 12);
        gbcJ.fill = GridBagConstraints.HORIZONTAL;

        // Train Number JComboBox (Dynamic from MySQL!)
        addLabel(gridJourney, gbcJ, 0, 0, "Train Number *:");
        comboTrainNumber = new JComboBox<>();
        comboTrainNumber.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboTrainNumber.setBackground(new Color(37, 43, 54));
        comboTrainNumber.setForeground(new Color(240, 246, 252));
        addControl(gridJourney, gbcJ, 0, 1, comboTrainNumber);

        // Train Name (Read-only)
        addLabel(gridJourney, gbcJ, 0, 2, "Train Name:");
        txtTrainName = createStyledTextField(20);
        txtTrainName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtTrainName.setEditable(false);
        txtTrainName.setBackground(new Color(45, 51, 59));
        txtTrainName.setForeground(new Color(56, 139, 253));
        addControl(gridJourney, gbcJ, 0, 3, txtTrainName);

        // Class Type JComboBox
        addLabel(gridJourney, gbcJ, 1, 0, "Class Type *:");
        String[] classOptions = {"Select Class", "First AC", "Second AC", "Third AC", "Sleeper", "Chair Car"};
        comboClassType = new JComboBox<>(classOptions);
        comboClassType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboClassType.setBackground(new Color(37, 43, 54));
        comboClassType.setForeground(new Color(240, 246, 252));
        addControl(gridJourney, gbcJ, 1, 1, comboClassType);

        // Journey Date
        addLabel(gridJourney, gbcJ, 1, 2, "Date of Journey *:");
        String defaultTomorrowStr = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        txtJourneyDate = createStyledTextField(15);
        txtJourneyDate.setText(defaultTomorrowStr);
        txtJourneyDate.setToolTipText("Format: yyyy-MM-dd");
        addControl(gridJourney, gbcJ, 1, 3, txtJourneyDate);

        panelJourney.add(gridJourney);
        formContainer.add(panelJourney);
        formContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // Section 3: Route Information
        JPanel panelRoute = createFormSectionPanel("3. Station Information");
        JPanel gridRoute = new JPanel(new GridBagLayout());
        gridRoute.setOpaque(false);
        GridBagConstraints gbcR = new GridBagConstraints();
        gbcR.insets = new Insets(8, 12, 8, 12);
        gbcR.fill = GridBagConstraints.HORIZONTAL;

        addLabel(gridRoute, gbcR, 0, 0, "Source Station *:");
        txtSourceStation = createStyledTextField(20);
        addControl(gridRoute, gbcR, 0, 1, txtSourceStation);

        addLabel(gridRoute, gbcR, 0, 2, "Destination Station *:");
        txtDestinationStation = createStyledTextField(20);
        addControl(gridRoute, gbcR, 0, 3, txtDestinationStation);

        panelRoute.add(gridRoute);
        formContainer.add(panelRoute);
        formContainer.add(Box.createRigidArea(new Dimension(0, 20)));

        // Button Bar
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonBar.setOpaque(false);
        buttonBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnClear = new JButton("Clear Form");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnClear.setPreferredSize(new Dimension(130, 42));
        btnClear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnBook = new JButton("Book Ticket");
        btnBook.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBook.setPreferredSize(new Dimension(160, 42));
        btnBook.setBackground(new Color(46, 160, 67));
        btnBook.setForeground(Color.WHITE);
        btnBook.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        buttonBar.add(btnClear);
        buttonBar.add(btnBook);

        formContainer.add(buttonBar);

        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(28, 33, 40));

        add(scrollPane, BorderLayout.CENTER);

        // Selection Listener for Train Dropdown
        comboTrainNumber.addActionListener(e -> onTrainSelected());

        btnBook.addActionListener(e -> performBooking());
        btnClear.addActionListener(e -> clearForm());

        // Load dynamic trains from MySQL
        refreshTrainDropdown();
    }

    private JTextField createStyledTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(0, 36));
        tf.setBackground(new Color(37, 43, 54));
        tf.setForeground(new Color(240, 246, 252));
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(new CompoundBorder(
                new LineBorder(new Color(68, 76, 89), 1, true),
                new EmptyBorder(5, 8, 5, 8)
        ));
        return tf;
    }

    private JPanel createFormSectionPanel(String sectionTitle) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new CompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(48, 54, 61), 1),
                        sectionTitle,
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        new Color(56, 139, 253)
                ),
                new EmptyBorder(12, 15, 12, 15)
        ));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private void addLabel(JPanel panel, GridBagConstraints gbc, int row, int col, String text) {
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.weightx = 0.15;
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(201, 209, 217));
        panel.add(lbl, gbc);
    }

    private void addControl(JPanel panel, GridBagConstraints gbc, int row, int col, JComponent comp) {
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        panel.add(comp, gbc);
    }

    public void refreshTrainDropdown() {
        comboTrainNumber.removeAllItems();
        List<Train> trains = reservationService.getAllTrains();

        if (trains == null || trains.isEmpty()) {
            txtTrainName.setText("No trains available in DB");
            return;
        }

        for (Train t : trains) {
            comboTrainNumber.addItem(t);
        }

        if (comboTrainNumber.getItemCount() > 0) {
            comboTrainNumber.setSelectedIndex(0);
            onTrainSelected();
        }
    }

    private void onTrainSelected() {
        Train selectedTrain = (Train) comboTrainNumber.getSelectedItem();
        if (selectedTrain != null) {
            txtTrainName.setText(selectedTrain.getTrainName());
            if (selectedTrain.getSourceStation() != null && !selectedTrain.getSourceStation().isEmpty()) {
                txtSourceStation.setText(selectedTrain.getSourceStation());
            }
            if (selectedTrain.getDestinationStation() != null && !selectedTrain.getDestinationStation().isEmpty()) {
                txtDestinationStation.setText(selectedTrain.getDestinationStation());
            }
        } else {
            txtTrainName.setText("");
        }
    }

    private void performBooking() {
        String passengerName = txtPassengerName.getText();
        Train selectedTrain = (Train) comboTrainNumber.getSelectedItem();

        if (selectedTrain == null) {
            JOptionPane.showMessageDialog(this,
                    "No valid train selected.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String trainNumberStr = String.valueOf(selectedTrain.getTrainNumber());
        String classType = (String) comboClassType.getSelectedItem();
        String journeyDateStr = txtJourneyDate.getText();
        String sourceStation = txtSourceStation.getText();
        String destStation = txtDestinationStation.getText();

        ValidationUtil.ValidationResult validation = ValidationUtil.validateReservationForm(
                passengerName, trainNumberStr, selectedTrain.getTrainName(), classType, journeyDateStr, sourceStation, destStation);

        if (!validation.isValid()) {
            JOptionPane.showMessageDialog(this,
                    validation.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        btnBook.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            Reservation res = reservationService.bookTicket(
                    passengerName, trainNumberStr, classType, journeyDateStr, sourceStation, destStation, currentUser);

            showBookingConfirmationDialog(res);
            clearForm();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Booking failed due to database error: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            btnBook.setEnabled(true);
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void showBookingConfirmationDialog(Reservation res) {
        String details = String.format(
                "==========================================\n" +
                "           BOOKING CONFIRMED              \n" +
                "==========================================\n\n" +
                "PNR Number          : %s\n" +
                "Passenger Name      : %s\n" +
                "Train Number        : %d\n" +
                "Train Name          : %s\n" +
                "Class Type          : %s\n" +
                "Journey Date        : %s\n" +
                "Source Station      : %s\n" +
                "Destination Station : %s\n\n" +
                "==========================================",
                res.getPnr(),
                res.getPassengerName(),
                res.getTrainNumber(),
                res.getTrainName(),
                res.getClassType(),
                res.getJourneyDate().toString(),
                res.getSourceStation(),
                res.getDestinationStation()
        );

        JTextArea textArea = new JTextArea(details);
        textArea.setFont(new Font("Monospaced", Font.BOLD, 14));
        textArea.setEditable(false);
        textArea.setBackground(new Color(245, 248, 250));
        textArea.setForeground(new Color(20, 24, 30));

        JOptionPane.showMessageDialog(this,
                new JScrollPane(textArea),
                "Booking Confirmed",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearForm() {
        txtPassengerName.setText("");
        if (comboTrainNumber.getItemCount() > 0) {
            comboTrainNumber.setSelectedIndex(0);
        }
        txtTrainName.setText("");
        comboClassType.setSelectedIndex(0);
        txtJourneyDate.setText(LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        txtSourceStation.setText("");
        txtDestinationStation.setText("");
    }
}
