package com.oasis.util;

import com.oasis.dao.ReservationDAO;

import java.security.SecureRandom;

/**
 * Utility for generating unique 10-digit PNR numbers.
 */
public class PNRGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private PNRGenerator() {
    }

    /**
     * Generates a 10-digit numeric PNR string.
     *
     * @return 10-digit string (e.g. 7845129630)
     */
    public static String generateRandomPnr() {
        long number = 1000000000L + (long) (RANDOM.nextDouble() * 9000000000L);
        return String.valueOf(number);
    }

    /**
     * Generates a unique PNR that does not exist in the MySQL database.
     *
     * @param reservationDAO DAO instance to check uniqueness
     * @return Unique PNR string
     */
    public static String generateUniquePnr(ReservationDAO reservationDAO) {
        int maxAttempts = 10;
        for (int i = 0; i < maxAttempts; i++) {
            String pnr = generateRandomPnr();
            if (!reservationDAO.existsByPnr(pnr)) {
                return pnr;
            }
        }
        // Fallback using timestamp if random collisions occur
        return String.valueOf(System.currentTimeMillis()).substring(3);
    }
}
