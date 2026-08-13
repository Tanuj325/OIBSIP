-- Online Reservation System Database Schema
-- Database: online_reservation

CREATE DATABASE IF NOT EXISTS online_reservation;
USE online_reservation;

-- Table: users
CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) DEFAULT 'User',
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: trains
CREATE TABLE IF NOT EXISTS trains (
    id INT PRIMARY KEY AUTO_INCREMENT,
    train_number INT NOT NULL UNIQUE,
    train_name VARCHAR(200) NOT NULL,
    source_station VARCHAR(100),
    destination_station VARCHAR(100)
);

-- Table: reservations (Linked to users.id via user_id foreign key)
CREATE TABLE IF NOT EXISTS reservations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    pnr VARCHAR(20) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    passenger_name VARCHAR(150) NOT NULL,
    train_number INT NOT NULL,
    train_name VARCHAR(200) NOT NULL,
    class_type VARCHAR(50) NOT NULL,
    journey_date DATE NOT NULL,
    source_station VARCHAR(150) NOT NULL,
    destination_station VARCHAR(150) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'CONFIRMED',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert Default Test Users (if not existing)
INSERT INTO users (username, password, full_name, role)
VALUES 
    ('admin', 'admin123', 'System Administrator', 'ADMIN'),
    ('user', 'user123', 'Passenger User', 'USER'),
    ('tanuj', 'tanuj123', 'Tanuj Pratap Singh', 'USER')
ON DUPLICATE KEY UPDATE username=username;

-- Insert Sample Trains (if not existing)
INSERT INTO trains (train_number, train_name, source_station, destination_station)
VALUES 
    (12301, 'Rajdhani Express', 'Howrah Junction (HWH)', 'New Delhi (NDLS)'),
    (12951, 'Mumbai Rajdhani', 'Mumbai Central (MMCT)', 'New Delhi (NDLS)'),
    (12002, 'Bhopal Shatabdi', 'New Delhi (NDLS)', 'Rani Kamlapati (RKMP)'),
    (12430, 'New Delhi Rajdhani', 'Hazrat Nizamuddin (NZM)', 'Lucknow Charbagh (LKO)'),
    (12259, 'Duronto Express', 'Sealdah (SDAH)', 'Bikaner (BKN)'),
    (12626, 'Kerala Express', 'New Delhi (NDLS)', 'Trivandrum Central (TVC)'),
    (22436, 'Vande Bharat Express', 'New Delhi (NDLS)', 'Varanasi Junction (BSB)')
ON DUPLICATE KEY UPDATE train_number=train_number;
