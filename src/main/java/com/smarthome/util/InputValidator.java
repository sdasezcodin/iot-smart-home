package com.smarthome.util;

import com.smarthome.exception.*;

public class InputValidator {

    // Checks if input is null or empty
    private static void isEmpty(String input) throws UserException {
        if (input == null || input.trim().isEmpty()) {
            throw new UserException("Input cannot be empty.");
        }
    }

    // Checks if input is a valid integer
    private static void isNumber(String input) throws UserException {
        isEmpty(input);
        try {
            Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            throw new UserException("Input must be a valid number: " + input);
        }
    }

    // Validates volume (1-100)
    public static void checkVolumeRange(String input) throws UserException {
        isNumber(input);
        int volume = Integer.parseInt(input.trim());
        if (volume < 1 || volume > 100) {
            throw new UserException("Volume must be between 1 and 100. Provided: " + volume);
        }
    }

    // Validates fan speed (1-5)
    public static void checkSpeedRange(String input) throws UserException {
        isNumber(input);
        int speed = Integer.parseInt(input.trim());
        if (speed < 1 || speed > 5) {
            throw new UserException("Speed must be between 1 and 5. Provided: " + speed);
        }
    }

    // Validates AC temperature (17-30°C)
    public static void checkTemperatureRange(String input) throws UserException {
        isNumber(input);
        int temp = Integer.parseInt(input.trim());
        if (temp < 17 || temp > 30) {
            throw new UserException("Temperature must be between 17 and 30. Provided: " + temp);
        }
    }

    // Validates date in YYYY-MM-DD format with proper ranges and leap year check
    public static void checkValidDate(String input) throws UserException {
        isEmpty(input);

        if (!input.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new UserException("Date must follow format YYYY-MM-DD. Provided: " + input);
        }

        String numericDate = input.replace("-", "").trim();
        if (!numericDate.matches("\\d+")) {
            throw new UserException("Date must contain only numeric characters and hyphens. Provided: " + input);
        }

        String[] parts = input.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        if (parts[0].length() != 4) {
            throw new UserException("Year must be a 4-digit integer. Provided: " + parts[0]);
        }

        if (month < 1 || month > 12) {
            throw new UserException("Invalid month value: " + month + ". Must be 1-12.");
        }

        int maxDay;
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            maxDay = 31;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            maxDay = 30;
        } else {
            maxDay = DateTimeUtil.isLeapYear(year) ? 29 : 28;
        }

        if (day < 1 || day > maxDay) {
            throw new UserException("Invalid day value: " + day + " for month " + month + " and year " + year);
        }
    }

    // Validates a date range (start <= end)
    public static void validDateRange(String startDate, String endDate) throws UserException {
        checkValidDate(startDate);
        checkValidDate(endDate);

        if (startDate.compareTo(endDate) > 0) {
            throw new UserException(
                    "Start date cannot be after end date. Provided: start=" + startDate + ", end=" + endDate
            );
        }
    }

    // Validates sensor fetch limit (1-10 records)
    public static void validateSensorDataFetchLimit(String input) throws UserException {
        isNumber(input);
        int limit = Integer.parseInt(input.trim());
        if (limit < 1 || limit > 10) {
            throw new UserException("Sensor data fetch limit must be between 1 and 10 records. Provided: " + limit);
        }
    }

    // Validates that names contain only letters and spaces
    public static void validateName(String input) throws UserException {
        isEmpty(input);
        if (!input.matches("^[A-Za-z\\s]+$")) {
            throw new UserException("Name can only contain letters and spaces. Provided: " + input);
        }
    }
}
