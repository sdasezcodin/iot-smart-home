package com.smarthome.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IdGenerator Tests")
class IdGeneratorTest {

    @Nested
    @DisplayName("Device ID Generation Tests")
    class DeviceIDGenerationTests {

        @Test
        @DisplayName("Should generate device ID in correct format")
        void shouldGenerateDeviceIDInCorrectFormat() {
            String deviceId = IdGenerator.generateID();

            assertNotNull(deviceId);
            assertTrue(deviceId.matches("\\d{14}"),
                    "Device ID should be 14 digits: " + deviceId);
            assertEquals(14, deviceId.length(), "Device ID should be exactly 14 characters");
        }

        @Test
        @DisplayName("Should generate device ID with current timestamp")
        void shouldGenerateDeviceIDWithCurrentTimestamp() {
            String deviceId = IdGenerator.generateID();

            // Compare with current timestamp (allow some tolerance)
            String expectedPrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String actualPrefix = deviceId.substring(0, 12); // First 12 digits (year, month, day, hour, minute)

            assertEquals(expectedPrefix, actualPrefix,
                    "Device ID should start with current timestamp");
        }

        @Test
        @DisplayName("Should contain valid date and time components")
        void shouldContainValidDateAndTimeComponents() {
            String deviceId = IdGenerator.generateID();

            // Extract components
            String year = deviceId.substring(0, 4);
            String month = deviceId.substring(4, 6);
            String day = deviceId.substring(6, 8);
            String hour = deviceId.substring(8, 10);
            String minute = deviceId.substring(10, 12);
            String second = deviceId.substring(12, 14);

            // Validate components
            int yearInt = Integer.parseInt(year);
            int monthInt = Integer.parseInt(month);
            int dayInt = Integer.parseInt(day);
            int hourInt = Integer.parseInt(hour);
            int minuteInt = Integer.parseInt(minute);
            int secondInt = Integer.parseInt(second);

            assertTrue(yearInt >= 1970 && yearInt <= 3000, "Year should be reasonable: " + yearInt);
            assertTrue(monthInt >= 1 && monthInt <= 12, "Month should be 1-12: " + monthInt);
            assertTrue(dayInt >= 1 && dayInt <= 31, "Day should be 1-31: " + dayInt);
            assertTrue(hourInt >= 0 && hourInt <= 23, "Hour should be 0-23: " + hourInt);
            assertTrue(minuteInt >= 0 && minuteInt <= 59, "Minute should be 0-59: " + minuteInt);
            assertTrue(secondInt >= 0 && secondInt <= 59, "Second should be 0-59: " + secondInt);
        }

        @Test
        @DisplayName("Should generate same ID when called in rapid succession")
        void shouldGenerateSameIDWhenCalledInRapidSuccession() {
            String id1 = IdGenerator.generateID();
            String id2 = IdGenerator.generateID();

            // Since both calls happen within the same second, IDs should be the same
            // This is expected behavior for device IDs based on timestamp
            assertEquals(id1, id2, "Device IDs generated in same second should be identical");
        }

        @Test
        @DisplayName("Should generate different IDs when called with time gap")
        void shouldGenerateDifferentIDsWhenCalledWithTimeGap() throws InterruptedException {
            String id1 = IdGenerator.generateID();
            Thread.sleep(1000); // Wait 1 second
            String id2 = IdGenerator.generateID();

            assertNotEquals(id1, id2, "Device IDs generated 1 second apart should be different");
        }

        @Test
        @DisplayName("Should generate consistent format across multiple calls")
        void shouldGenerateConsistentFormatAcrossMultipleCalls() {
            for (int i = 0; i < 100; i++) {
                String deviceId = IdGenerator.generateID();
                assertTrue(deviceId.matches("\\d{14}"),
                        "All device IDs should match format: " + deviceId);
                assertEquals(14, deviceId.length(),
                        "All device IDs should be 14 characters: " + deviceId);
            }
        }
    }

    @Nested
    @DisplayName("Sensor Reading ID Generation Tests")
    class SensorReadingIDGenerationTests {

        @Test
        @DisplayName("Should generate reading ID in correct format")
        void shouldGenerateReadingIDInCorrectFormat() {
            String readingId = IdGenerator.generateReadingDataID();

            assertNotNull(readingId);
            assertTrue(readingId.matches("\\d+"),
                    "Reading ID should contain only digits: " + readingId);
            assertTrue(readingId.length() >= 17, // minimum expected length
                    "Reading ID should be at least 17 characters: " + readingId);
        }


        @Test
        @DisplayName("Should contain timestamp component")
        void shouldContainTimestampComponent() {
            String readingId = IdGenerator.generateReadingDataID();

            // First 14 digits should represent timestamp (yyyyMMddHHmmss)
            String timestampPart = readingId.substring(0, 14);
            assertTrue(timestampPart.matches("\\d{14}"),
                    "First 14 digits should be timestamp: " + timestampPart);

            // Compare with current timestamp (allow some tolerance)
            String expectedPrefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm"));
            String actualPrefix = timestampPart.substring(0, 12);
            assertEquals(expectedPrefix, actualPrefix,
                    "Reading ID should start with current timestamp");
        }

        @Test
        @DisplayName("Should contain random component")
        void shouldContainRandomComponent() {
            String readingId1 = IdGenerator.generateReadingDataID();
            String readingId2 = IdGenerator.generateReadingDataID();

            // IDs should be different due to random component
            assertNotEquals(readingId1, readingId2,
                    "Reading IDs should differ due to random component");
        }

        @Test
        @DisplayName("Should have consistent length pattern")
        void shouldHaveConsistentLengthPattern() {
            Set<Integer> lengths = new HashSet<>();

            for (int i = 0; i < 100; i++) {
                String readingId = IdGenerator.generateReadingDataID();
                lengths.add(readingId.length());
            }

            // All IDs should have the same length (due to consistent format)
            assertTrue(lengths.size() <= 2, // Allow for some variation due to microseconds
                    "Reading IDs should have consistent length patterns: " + lengths);
        }

        @Test
        @DisplayName("Should end with 3-digit random number")
        void shouldEndWithThreeDigitRandomNumber() {
            for (int i = 0; i < 50; i++) {
                String readingId = IdGenerator.generateReadingDataID();
                String lastThreeDigits = readingId.substring(readingId.length() - 3);

                assertTrue(lastThreeDigits.matches("\\d{3}"),
                        "Last 3 digits should be numeric: " + lastThreeDigits);

                int randomPart = Integer.parseInt(lastThreeDigits);
                assertTrue(randomPart >= 0 && randomPart <= 999,
                        "Random part should be 000-999: " + randomPart);
            }
        }

        @Nested
        @DisplayName("Comparison Between ID Types Tests")
        class ComparisonBetweenIDTypesTests {

            @Test
            @DisplayName("Should generate different types of IDs with different formats")
            void shouldGenerateDifferentTypesOfIDsWithDifferentFormats() {
                String deviceId = IdGenerator.generateID();
                String readingId = IdGenerator.generateReadingDataID();

                assertNotEquals(deviceId, readingId,
                        "Device ID and Reading ID should be different");

                assertEquals(14, deviceId.length(),
                        "Device ID should be 14 characters");
                assertTrue(readingId.length() > deviceId.length(),
                        "Reading ID should be longer than Device ID");
            }

            @Test
            @DisplayName("Should have consistent timestamp prefix for both ID types")
            void shouldHaveConsistentTimestampPrefixForBothIDTypes() {
                String deviceId = IdGenerator.generateID();
                String readingId = IdGenerator.generateReadingDataID();

                // Both should have similar timestamp prefix (first 12 characters for minute precision)
                String devicePrefix = deviceId.substring(0, 12);
                String readingPrefix = readingId.substring(0, 12);

                assertEquals(devicePrefix, readingPrefix,
                        "Both ID types should have same timestamp prefix when generated simultaneously");
            }
        }

        @Nested
        @DisplayName("Edge Cases and Robustness Tests")
        class EdgeCasesAndRobustnessTests {

            @Test
            @DisplayName("Should maintain format integrity under stress")
            void shouldMaintainFormatIntegrityUnderStress() {
                // Generate many IDs and verify format consistency
                for (int i = 0; i < 10000; i++) {
                    String deviceId = IdGenerator.generateID();
                    String readingId = IdGenerator.generateReadingDataID();

                    assertTrue(deviceId.matches("\\d{14}"),
                            "Device ID format should remain consistent: " + deviceId);
                    assertTrue(readingId.matches("\\d+"),
                            "Reading ID should contain only digits: " + readingId);
                    assertTrue(readingId.length() >= 17,
                            "Reading ID should maintain minimum length: " + readingId);
                }
            }

            @Test
            @DisplayName("Should generate valid IDs across different time periods")
            void shouldGenerateValidIDsAcrossDifferentTimePeriods() throws InterruptedException {
                String id1 = IdGenerator.generateID();
                String readingId1 = IdGenerator.generateReadingDataID();

                Thread.sleep(10); // Small delay

                String id2 = IdGenerator.generateID();
                String readingId2 = IdGenerator.generateReadingDataID();

                // All IDs should be valid format
                assertTrue(id1.matches("\\d{14}"));
                assertTrue(id2.matches("\\d{14}"));
                assertTrue(readingId1.matches("\\d+"));
                assertTrue(readingId2.matches("\\d+"));

                // Reading IDs should be different
                assertNotEquals(readingId1, readingId2);
            }
        }

        @Nested
        @DisplayName("Performance Tests")
        class PerformanceTests {

            @Test
            @DisplayName("Should generate IDs efficiently")
            void shouldGenerateIDsEfficiently() {
                long startTime = System.currentTimeMillis();

                // Generate many IDs
                for (int i = 0; i < 10000; i++) {
                    IdGenerator.generateID();
                    IdGenerator.generateReadingDataID();
                }

                long endTime = System.currentTimeMillis();
                long duration = endTime - startTime;

                // Should complete within reasonable time
                assertTrue(duration < 5000, // 5 seconds for 20000 IDs
                        "ID generation should be efficient, took: " + duration + "ms for 20000 IDs");
            }

            @Test
            @DisplayName("Should maintain performance consistency")
            void shouldMaintainPerformanceConsistency() {
                long[] times = new long[5];

                for (int run = 0; run < 5; run++) {
                    long startTime = System.currentTimeMillis();

                    for (int i = 0; i < 1000; i++) {
                        IdGenerator.generateID();
                        IdGenerator.generateReadingDataID();
                    }

                    times[run] = System.currentTimeMillis() - startTime;
                }

                // Check that performance doesn't degrade significantly
                long maxTime = times[0];
                long minTime = times[0];
                for (long time : times) {
                    maxTime = Math.max(maxTime, time);
                    minTime = Math.min(minTime, time);
                }

                assertTrue(maxTime - minTime < 1000, // Less than 1 second variation
                        "Performance should be consistent across runs");
            }
        }

        @Nested
        @DisplayName("Utility Class Properties Tests")
        class UtilityClassPropertiesTests {

            @Test
            @DisplayName("Should have all static methods")
            void shouldHaveAllStaticMethods() throws NoSuchMethodException {
                // Verify all public methods are static
                assertTrue(java.lang.reflect.Modifier.isStatic(
                        IdGenerator.class.getMethod("generateID").getModifiers()));
                assertTrue(java.lang.reflect.Modifier.isStatic(
                        IdGenerator.class.getMethod("generateReadingDataID").getModifiers()));
            }

            @Test
            @DisplayName("Should not require instantiation")
            void shouldNotRequireInstantiation() {
                // Should be able to call methods without instantiation
                assertDoesNotThrow(() -> {
                    IdGenerator.generateID();
                    IdGenerator.generateReadingDataID();
                });
            }
        }
    }
}