-- Database Migration Script for Online Reservation System
USE online_reservation;

-- 1. Ensure user_id column exists in reservations table
SET @dbname = DATABASE();
SET @tablename = "reservations";
SET @columnname = "user_id";

SET @preparedStatement = (SELECT IF(
    (
        SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
        WHERE
            TABLE_SCHEMA = @dbname
            AND TABLE_NAME = @tablename
            AND COLUMN_NAME = @columnname
    ) > 0,
    "SELECT 1",
    "ALTER TABLE reservations ADD COLUMN user_id INT NOT NULL DEFAULT 1"
));

PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- 2. Populate user_id from username column if username column existed
UPDATE reservations r 
JOIN users u ON r.username = u.username 
SET r.user_id = u.id 
WHERE r.user_id = 1 OR r.user_id IS NULL;

-- 3. Drop legacy username column from reservations if present
SET @columnname = "username";
SET @preparedStatement = (SELECT IF(
    (
        SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
        WHERE
            TABLE_SCHEMA = @dbname
            AND TABLE_NAME = @tablename
            AND COLUMN_NAME = @columnname
    ) > 0,
    "ALTER TABLE reservations DROP COLUMN username",
    "SELECT 1"
));

PREPARE dropIfExists FROM @preparedStatement;
EXECUTE dropIfExists;
DEALLOCATE PREPARE dropIfExists;
