package com.smarthome.util;

import java.util.Random;

public class SensorDataGenerator {

    private static final Random RANDOM = new Random();

    public static String generateMessage(String deviceName, String type, int level, double basePower) {
        double power;

        switch (type.toUpperCase()) {
            case "AC":
                // AC: ~800–2000W depending on cooling level
                power = 800 + (level - 17) * 50; // higher level = more cooling power
                break;

            case "FAN":
                // Fan: ~30–100W depending on speed
                power = 20 + level * 15;
                break;

            case "SPEAKER":
                // Speaker: ~50–300W depending on volume
                power = 30 + level * 2.5;
                break;

            default:
                power = basePower; // fallback
        }

        // add small fluctuations ±10%
        double randomizedPower = power * (0.9 + 0.2 * RANDOM.nextDouble());

        switch (type.toUpperCase()) {
            case "AC":
                double temp = level + (RANDOM.nextDouble() - 0.5) * 0.5;
                return String.format("%s → Cooling Level: %d | Temperature: %.1f°C | Power: %.2f W",
                        deviceName, level, temp, randomizedPower);

            case "FAN":
                int baseRPM = level * 200;
                double fluctuatingRPM = baseRPM + (RANDOM.nextDouble() * 20 - 10);
                return String.format("%s → Fan Speed Level: %d | RPM: %.0f | Power: %.2f W",
                        deviceName, level, fluctuatingRPM, randomizedPower);

            case "SPEAKER":
                double baseNoise = level / 2.0;
                double fluctuatingNoise = baseNoise + (RANDOM.nextDouble() * 2 - 1);
                return String.format("%s → Volume Level: %d | Noise: %.1f dB | Power: %.2f W",
                        deviceName, level, fluctuatingNoise, randomizedPower);

            default:
                return String.format("%s → Unknown device type | Power: %.2f W",
                        deviceName, randomizedPower);
        }
    }
}
