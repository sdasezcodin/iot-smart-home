package com.smarthome.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for generating unique IDs for devices and sensor readings.
 * Device IDs are based on timestamp, while Sensor Data IDs include microseconds
 * and random digits to ensure uniqueness.
 */
public class IdGenerator {

    // ---------------- Device ID ----------------

    /**
     * Generates a unique device ID using the current timestamp.
     * Format: yyyyMMddHHmmss (14 digits)
     * Example: 20250912173045
     * @return unique device ID
     */
    public static String generateID() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return now.format(formatter);
    }

    // ---------------- Sensor Data ID ----------------

    /**
     * Generates a unique sensor reading ID using timestamp + microseconds + random digits.
     * Format: yyyyMMddHHmmssSSSSSS + 3 random digits (total ~23 digits)
     * Example: 20250912173045123456042
     * @return unique sensor reading ID
     */
    public static String generateReadingDataID() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSS"); // microseconds
        String timestamp = now.format(formatter);

        int random = (int) (Math.random() * 1000); // 3 random digits (000-999)
        String randomStr = String.format("%03d", random); // pad with zeros if needed

        return timestamp + randomStr;
    }
}
