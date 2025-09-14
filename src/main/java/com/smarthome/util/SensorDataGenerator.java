package com.smarthome.util;

import java.util.Random;

/**
 * A utility class for generating mock sensor data messages for various smart devices.
 * <p>
 * This class simulates real-world sensor readings such as power consumption,
 * temperature, fan speed, and noise levels, based on the device type and its
 * current operational level. It is used to generate test data for the
 * smart home simulation.
 */
public class SensorDataGenerator {

    private static final Random RANDOM = new Random();

    /**
     * Generates a formatted sensor data message string for a given device.
     * <p>
     * The method calculates a simulated power consumption and other metrics
     * based on the device's type and operational level, adding small random
     * fluctuations to make the data more realistic.
     *
     * @param deviceName  The name of the device (e.g., "Living Room AC").
     * @param type        The type of the device (e.g., "AC", "FAN", "SPEAKER").
     * @param level       The current operational level (e.g., temperature, speed, volume).
     * @param basePower   A fallback power value to use if the device type is unknown.
     * @return A formatted string containing the device's sensor data.
     */
    public static String generateMessage(String deviceName, String type, int level, double basePower) {
        double power;

        // Calculate a base power value based on the device type and its level.
        switch (type.toUpperCase()) {
            case "AC":
                // AC power usage is higher for lower temperatures (more cooling).
                power = 800 + (level - 17) * 50;
                break;

            case "FAN":
                // Fan power usage increases with speed.
                power = 20 + level * 15;
                break;

            case "SPEAKER":
                // Speaker power usage increases with volume.
                power = 30 + level * 2.5;
                break;

            default:
                // Use the provided base power as a fallback.
                power = basePower;
        }

        // Add small random fluctuations (±10%) to the power value to simulate variability.
        double randomizedPower = power * (0.9 + 0.2 * RANDOM.nextDouble());

        // Generate the final formatted message string with relevant metrics.
        switch (type.toUpperCase()) {
            case "AC":
                // For an AC, include temperature and power.
                double temp = level + (RANDOM.nextDouble() - 0.5) * 0.5;
                return String.format("%s → Cooling Level: %d | Temperature: %.1f°C | Power: %.2f W",
                        deviceName, level, temp, randomizedPower);

            case "FAN":
                // For a fan, include estimated RPM and power.
                int baseRPM = level * 200;
                double fluctuatingRPM = baseRPM + (RANDOM.nextDouble() * 20 - 10);
                return String.format("%s → Fan Speed Level: %d | RPM: %.0f | Power: %.2f W",
                        deviceName, level, fluctuatingRPM, randomizedPower);

            case "SPEAKER":
                // For a speaker, include simulated noise level and power.
                double baseNoise = level / 2.0;
                double fluctuatingNoise = baseNoise + (RANDOM.nextDouble() * 2 - 1);
                return String.format("%s → Volume Level: %d | Noise: %.1f dB | Power: %.2f W",
                        deviceName, level, fluctuatingNoise, randomizedPower);

            default:
                // For an unknown type, only report the power.
                return String.format("%s → Unknown device type | Power: %.2f W",
                        deviceName, randomizedPower);
        }
    }
}