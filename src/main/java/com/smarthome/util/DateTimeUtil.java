package com.smarthome.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations.
 * Provides methods to get formatted current date/time and generate timestamps for IDs.
 */
public class DateTimeUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter ID_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private DateTimeUtil() {
        // Prevent instantiation
    }

    /**
     * Returns the current date in YYYY-MM-DD format.
     */
    public static String getCurrentDate() {
        return LocalDateTime.now().format(DATE_FORMATTER);
    }

    /**
     * Returns the current time in HH:mm:ss format.
     */
    public static String getCurrentTime() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }

    /**
     * Checks if a given year is a leap year.
     *
     * @param year The year to check
     * @return true if leap year, false otherwise
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0));
    }

    /**
     * Returns the current date and time in YYYYMMDDHHMMSS format.
     * Useful for generating unique IDs.
     */
    public static String getCurrentDateTimeForID() {
        return LocalDateTime.now().format(ID_FORMATTER);
    }
}
