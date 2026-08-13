package com.oasis.service;

import com.oasis.dao.ReservationDAO;
import com.oasis.dao.TrainDAO;
import com.oasis.model.Reservation;
import com.oasis.model.Train;
import com.oasis.model.User;
import com.oasis.util.PNRGenerator;
import com.oasis.util.ValidationUtil;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Service class handling train lookup, booking, reservation list, search, and cancellation logic with authorization.
 */
public class ReservationService {

    private final TrainDAO trainDAO;
    private final ReservationDAO reservationDAO;

    public ReservationService() {
        this.trainDAO = new TrainDAO();
        this.reservationDAO = new ReservationDAO();
    }

    public ReservationService(TrainDAO trainDAO, ReservationDAO reservationDAO) {
        this.trainDAO = trainDAO;
        this.reservationDAO = reservationDAO;
    }

    public Train getTrainByNumber(int trainNumber) {
        return trainDAO.getTrainByNumber(trainNumber);
    }

    public List<Train> getAllTrains() {
        return trainDAO.getAllTrains();
    }

    /**
     * Create a new ticket booking for authenticated user session.
     */
    public Reservation bookTicket(
            String passengerName, 
            String trainNumberStr, 
            String classType, 
            String journeyDateStr, 
            String sourceStation, 
            String destStation, 
            User currentUser) {

        if (currentUser == null) {
            throw new IllegalArgumentException("User session is required to perform booking.");
        }

        int trainNumber = Integer.parseInt(trainNumberStr.trim());
        Train train = getTrainByNumber(trainNumber);
        String trainName = (train != null) ? train.getTrainName() : null;

        ValidationUtil.ValidationResult validation = ValidationUtil.validateReservationForm(
                passengerName, trainNumberStr, trainName, classType, journeyDateStr, sourceStation, destStation);

        if (!validation.isValid()) {
            throw new IllegalArgumentException(validation.getMessage());
        }

        // Generate unique PNR
        String pnr = PNRGenerator.generateUniquePnr(reservationDAO);
        Date journeyDate = Date.valueOf(LocalDate.parse(journeyDateStr.trim()));

        Reservation res = new Reservation(
                pnr,
                currentUser.getId(),
                passengerName.trim(),
                trainNumber,
                trainName,
                classType.trim(),
                journeyDate,
                sourceStation.trim(),
                destStation.trim()
        );

        boolean inserted = reservationDAO.insertReservation(res);
        if (!inserted) {
            throw new RuntimeException("Database error: Could not complete ticket reservation.");
        }

        res.setUsername(currentUser.getUsername());
        return res;
    }

    /**
     * Fetch reservations based on logged-in user role (USER = own reservations, ADMIN = all reservations).
     */
    public List<Reservation> getReservationsForUser(User currentUser) {
        if (currentUser == null) {
            return Collections.emptyList();
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return reservationDAO.getAllReservations();
        } else {
            return reservationDAO.getReservationsByUser(currentUser.getId());
        }
    }

    /**
     * Search reservations by keyword with SQL-level role enforcement.
     */
    public List<Reservation> searchReservations(String keyword, User currentUser) {
        if (currentUser == null) {
            return Collections.emptyList();
        }
        if (ValidationUtil.isNullOrEmpty(keyword)) {
            return getReservationsForUser(currentUser);
        }
        return reservationDAO.searchReservations(keyword.trim(), currentUser);
    }

    /**
     * Fetch reservation details by PNR with ownership validation.
     */
    public Reservation getReservationByPnr(String pnr, User currentUser) {
        if (ValidationUtil.isNullOrEmpty(pnr)) {
            throw new IllegalArgumentException("PNR Number cannot be empty.");
        }
        return reservationDAO.getReservationByPnr(pnr.trim(), currentUser);
    }

    /**
     * Cancel reservation by PNR with ownership validation.
     */
    public boolean cancelReservation(String pnr, User currentUser) {
        if (ValidationUtil.isNullOrEmpty(pnr)) {
            throw new IllegalArgumentException("PNR Number cannot be empty.");
        }
        return reservationDAO.deleteReservationByPnr(pnr.trim(), currentUser);
    }

    public int getTrainCount() {
        return trainDAO.getTrainCount();
    }

    public int getTotalReservationCount() {
        return reservationDAO.getTotalReservationCount();
    }

    public int getUserReservationCount(User currentUser) {
        if (currentUser == null) {
            return 0;
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return reservationDAO.getTotalReservationCount();
        } else {
            return reservationDAO.getUserReservationCount(currentUser.getId());
        }
    }
}
