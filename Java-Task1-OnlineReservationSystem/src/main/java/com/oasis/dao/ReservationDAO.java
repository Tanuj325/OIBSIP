package com.oasis.dao;

import com.oasis.config.DatabaseConnection;
import com.oasis.model.Reservation;
import com.oasis.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Reservation entities with strict SQL-level authorization.
 */
public class ReservationDAO {

    private static final Logger LOGGER = Logger.getLogger(ReservationDAO.class.getName());

    /**
     * Insert a new reservation into MySQL.
     *
     * @param res Reservation object containing user_id
     * @return true if inserted successfully
     */
    public boolean insertReservation(Reservation res) {
        String sql = "INSERT INTO reservations (pnr, user_id, passenger_name, train_number, train_name, class_type, journey_date, source_station, destination_station, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, res.getPnr());
            stmt.setInt(2, res.getUserId());
            stmt.setString(3, res.getPassengerName());
            stmt.setInt(4, res.getTrainNumber());
            stmt.setString(5, res.getTrainName());
            stmt.setString(6, res.getClassType());
            stmt.setDate(7, res.getJourneyDate());
            stmt.setString(8, res.getSourceStation());
            stmt.setString(9, res.getDestinationStation());
            stmt.setString(10, res.getStatus() != null ? res.getStatus() : "CONFIRMED");

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to insert reservation into database", e);
            return false;
        }
    }

    /**
     * Check whether a PNR already exists.
     *
     * @param pnr PNR string
     * @return true if exists
     */
    public boolean existsByPnr(String pnr) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE pnr = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pnr);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking PNR existence: " + pnr, e);
        }
        return false;
    }

    /**
     * Fetch reservations for a specific normal user (USER role).
     *
     * @param userId user's database ID
     * @return List of Reservation objects owned by userId
     */
    public List<Reservation> getReservationsByUser(int userId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.id, r.pnr, r.user_id, r.passenger_name, r.train_number, r.train_name, r.class_type, " +
                     "r.journey_date, r.source_station, r.destination_station, r.created_at, r.status, u.username " +
                     "FROM reservations r JOIN users u ON r.user_id = u.id " +
                     "WHERE r.user_id = ? ORDER BY r.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching user reservations for userId: " + userId, e);
        }
        return list;
    }

    /**
     * Fetch ALL reservations across all users for ADMIN role.
     *
     * @return List of all Reservation objects
     */
    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT r.id, r.pnr, r.user_id, r.passenger_name, r.train_number, r.train_name, r.class_type, " +
                     "r.journey_date, r.source_station, r.destination_station, r.created_at, r.status, u.username " +
                     "FROM reservations r JOIN users u ON r.user_id = u.id " +
                     "ORDER BY r.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all reservations for ADMIN", e);
        }
        return list;
    }

    /**
     * Fetch reservation details by PNR enforcing role-based ownership in SQL.
     *
     * @param pnr PNR number
     * @param currentUser Logged-in authenticated user session
     * @return Reservation object if authorized and found, null otherwise
     */
    public Reservation getReservationByPnr(String pnr, User currentUser) {
        boolean isAdmin = currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
        String sql;

        if (isAdmin) {
            sql = "SELECT r.id, r.pnr, r.user_id, r.passenger_name, r.train_number, r.train_name, r.class_type, " +
                  "r.journey_date, r.source_station, r.destination_station, r.created_at, r.status, u.username " +
                  "FROM reservations r JOIN users u ON r.user_id = u.id WHERE r.pnr = ?";
        } else {
            sql = "SELECT r.id, r.pnr, r.user_id, r.passenger_name, r.train_number, r.train_name, r.class_type, " +
                  "r.journey_date, r.source_station, r.destination_station, r.created_at, r.status, u.username " +
                  "FROM reservations r JOIN users u ON r.user_id = u.id WHERE r.pnr = ? AND r.user_id = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pnr);
            if (!isAdmin) {
                stmt.setInt(2, currentUser != null ? currentUser.getId() : -1);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservation(rs);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching reservation by PNR: " + pnr, e);
        }
        return null;
    }

    /**
     * Delete reservation by PNR enforcing ownership in SQL.
     *
     * @param pnr PNR number
     * @param currentUser Logged-in authenticated user session
     * @return true if deleted from database
     */
    public boolean deleteReservationByPnr(String pnr, User currentUser) {
        boolean isAdmin = currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
        String sql;

        if (isAdmin) {
            sql = "DELETE FROM reservations WHERE pnr = ?";
        } else {
            sql = "DELETE FROM reservations WHERE pnr = ? AND user_id = ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, pnr);
            if (!isAdmin) {
                stmt.setInt(2, currentUser != null ? currentUser.getId() : -1);
            }

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting reservation by PNR: " + pnr, e);
            return false;
        }
    }

    /**
     * Search reservations by keyword enforcing role scoping in SQL.
     */
    public List<Reservation> searchReservations(String keyword, User currentUser) {
        List<Reservation> list = new ArrayList<>();
        boolean isAdmin = currentUser != null && "ADMIN".equalsIgnoreCase(currentUser.getRole());
        String searchPattern = "%" + keyword.trim() + "%";
        String sql;

        if (isAdmin) {
            sql = "SELECT r.id, r.pnr, r.user_id, r.passenger_name, r.train_number, r.train_name, r.class_type, " +
                  "r.journey_date, r.source_station, r.destination_station, r.created_at, r.status, u.username " +
                  "FROM reservations r JOIN users u ON r.user_id = u.id " +
                  "WHERE r.pnr LIKE ? OR r.passenger_name LIKE ? OR r.train_name LIKE ? OR u.username LIKE ? " +
                  "ORDER BY r.created_at DESC";
        } else {
            sql = "SELECT r.id, r.pnr, r.user_id, r.passenger_name, r.train_number, r.train_name, r.class_type, " +
                  "r.journey_date, r.source_station, r.destination_station, r.created_at, r.status, u.username " +
                  "FROM reservations r JOIN users u ON r.user_id = u.id " +
                  "WHERE r.user_id = ? AND (r.pnr LIKE ? OR r.passenger_name LIKE ? OR r.train_name LIKE ?) " +
                  "ORDER BY r.created_at DESC";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (isAdmin) {
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);
            } else {
                stmt.setInt(1, currentUser != null ? currentUser.getId() : -1);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching reservations with pattern: " + searchPattern, e);
        }
        return list;
    }

    public int getTotalReservationCount() {
        String sql = "SELECT COUNT(*) FROM reservations";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting total reservations", e);
        }
        return 0;
    }

    public int getUserReservationCount(int userId) {
        String sql = "SELECT COUNT(*) FROM reservations WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting user reservations for userId: " + userId, e);
        }
        return 0;
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getInt("id"),
                rs.getString("pnr"),
                rs.getInt("user_id"),
                rs.getString("passenger_name"),
                rs.getInt("train_number"),
                rs.getString("train_name"),
                rs.getString("class_type"),
                rs.getDate("journey_date"),
                rs.getString("source_station"),
                rs.getString("destination_station"),
                rs.getTimestamp("created_at"),
                rs.getString("username"),
                rs.getString("status")
        );
    }
}
