package com.oasis.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Utility class for validating reservation form inputs.
 */
public class ValidationUtil {

    private static final DateTimeFormatter DATE_FORMATTER = 
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private ValidationUtil() {
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNumeric(String str) {
        if (isNullOrEmpty(str)) {
            return false;
        }
        return str.trim().matches("\\d+");
    }

    /**
     * Validates date string in strict yyyy-MM-dd format and ensures date is not in the past.
     *
     * @param dateStr String input date
     * @return ValidationResult containing success flag and error message
     */
    public static ValidationResult validateJourneyDate(String dateStr) {
        if (isNullOrEmpty(dateStr)) {
            return new ValidationResult(false, "Journey Date is required.");
        }

        try {
            LocalDate date = LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
            LocalDate today = LocalDate.now();

            if (date.isBefore(today)) {
                return new ValidationResult(false, "Journey date cannot be in the past (" + dateStr + ").");
            }
            return new ValidationResult(true, "Valid date.");
        } catch (DateTimeParseException e) {
            return new ValidationResult(false, "Invalid date format or non-existent date. Please use yyyy-MM-dd format (e.g. 2026-08-20).");
        }
    }

    /**
     * Complete validation for reservation form.
     *
     * @param passengerName passenger name
     * @param trainNumberStr train number string
     * @param trainName train name
     * @param classType selected class type
     * @param journeyDateStr date string
     * @param sourceStation source station
     * @param destStation destination station
     * @return ValidationResult object
     */
    public static ValidationResult validateReservationForm(
            String passengerName, 
            String trainNumberStr, 
            String trainName, 
            String classType, 
            String journeyDateStr, 
            String sourceStation, 
            String destStation) {

        if (isNullOrEmpty(passengerName)) {
            return new ValidationResult(false, "Passenger Name is required.");
        }
        if (passengerName.trim().length() < 2 || passengerName.trim().length() > 150) {
            return new ValidationResult(false, "Passenger Name must be between 2 and 150 characters.");
        }

        if (isNullOrEmpty(trainNumberStr)) {
            return new ValidationResult(false, "Train Number is required.");
        }
        if (!isNumeric(trainNumberStr)) {
            return new ValidationResult(false, "Train Number must be numeric.");
        }

        if (isNullOrEmpty(trainName)) {
            return new ValidationResult(false, "Invalid Train Number or Train not found.");
        }

        if (isNullOrEmpty(classType) || classType.equalsIgnoreCase("Select Class")) {
            return new ValidationResult(false, "Class Type must be selected.");
        }

        ValidationResult dateValidation = validateJourneyDate(journeyDateStr);
        if (!dateValidation.isValid()) {
            return dateValidation;
        }

        if (isNullOrEmpty(sourceStation)) {
            return new ValidationResult(false, "Source Station is required.");
        }

        if (isNullOrEmpty(destStation)) {
            return new ValidationResult(false, "Destination Station is required.");
        }

        if (sourceStation.trim().equalsIgnoreCase(destStation.trim())) {
            return new ValidationResult(false, "Source and Destination stations cannot be identical.");
        }

        return new ValidationResult(true, "Validation successful.");
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
