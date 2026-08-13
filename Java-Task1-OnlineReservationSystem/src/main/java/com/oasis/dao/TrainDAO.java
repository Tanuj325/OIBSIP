package com.oasis.dao;

import com.oasis.config.DatabaseConnection;
import com.oasis.model.Train;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for Train entities.
 */
public class TrainDAO {

    private static final Logger LOGGER = Logger.getLogger(TrainDAO.class.getName());

    /**
     * Fetch train by train number using PreparedStatement.
     *
     * @param trainNumber integer train number
     * @return Train object if found, null otherwise
     */
    public Train getTrainByNumber(int trainNumber) {
        String sql = "SELECT id, train_number, train_name, source_station, destination_station FROM trains WHERE train_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, trainNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Train(
                        rs.getInt("id"),
                        rs.getInt("train_number"),
                        rs.getString("train_name"),
                        rs.getString("source_station"),
                        rs.getString("destination_station")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching train by train number: " + trainNumber, e);
        }
        return null;
    }

    /**
     * Fetch all available trains from MySQL.
     *
     * @return List of Train objects
     */
    public List<Train> getAllTrains() {
        List<Train> list = new ArrayList<>();
        String sql = "SELECT id, train_number, train_name, source_station, destination_station FROM trains ORDER BY train_number ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Train(
                    rs.getInt("id"),
                    rs.getInt("train_number"),
                    rs.getString("train_name"),
                    rs.getString("source_station"),
                    rs.getString("destination_station")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error fetching all trains from database", e);
        }
        return list;
    }

    /**
     * Get total count of trains in MySQL database.
     *
     * @return count of trains
     */
    public int getTrainCount() {
        String sql = "SELECT COUNT(*) FROM trains";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting trains from database", e);
        }
        return 0;
    }
}
