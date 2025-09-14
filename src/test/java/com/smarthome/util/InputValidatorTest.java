package com.smarthome.util;

import com.smarthome.exception.UserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InputValidator Tests")
class InputValidatorTest {

    @Nested
    @DisplayName("Volume Range Validation Tests")
    class VolumeRangeTests {

        @Test
        @DisplayName("Should accept valid volume values")
        void shouldAcceptValidVolumeValues() {
            String[] validVolumes = {"1", "50", "100", "25", "75"};

            for (String volume : validVolumes) {
                assertDoesNotThrow(() -> InputValidator.checkVolumeRange(volume),
                        "Volume " + volume + " should be valid");
            }
        }

        @Test
        @DisplayName("Should reject volume below minimum")
        void shouldRejectVolumeBelowMinimum() {
            String[] invalidVolumes = {"0", "-1", "-10"};

            for (String volume : invalidVolumes) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.checkVolumeRange(volume));
                assertTrue(exception.getMessage().contains("Volume must be between 1 and 100"));
            }
        }

        @Test
        @DisplayName("Should reject volume above maximum")
        void shouldRejectVolumeAboveMaximum() {
            String[] invalidVolumes = {"101", "150", "1000"};

            for (String volume : invalidVolumes) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.checkVolumeRange(volume));
                assertTrue(exception.getMessage().contains("Volume must be between 1 and 100"));
            }
        }

        @Test
        @DisplayName("Should reject null volume input")
        void shouldRejectNullVolumeInput() {
            UserException exception = assertThrows(UserException.class,
                    () -> InputValidator.checkVolumeRange(null));
            assertTrue(exception.getMessage().contains("Input cannot be empty"));
        }

        @Test
        @DisplayName("Should reject empty volume input")
        void shouldRejectEmptyVolumeInput() {
            String[] emptyInputs = {"", "  ", "\t", "\n"};

            for (String input : emptyInputs) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.checkVolumeRange(input));
                assertTrue(exception.getMessage().contains("Input cannot be empty"));
            }
        }

        @Test
        @DisplayName("Should reject non-numeric volume input")
        void shouldRejectNonNumericVolumeInput() {
            String[] invalidInputs = {"abc", "50a", "1.5", "fifty", "10%"};

            for (String input : invalidInputs) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.checkVolumeRange(input));
                assertTrue(exception.getMessage().contains("Input must be a valid number"));
            }
        }

        @Test
        @DisplayName("Should handle whitespace in volume input")
        void shouldHandleWhitespaceInVolumeInput() {
            String[] validInputsWithWhitespace = {"  50  ", "\t75\t", " 100 "};

            for (String input : validInputsWithWhitespace) {
                assertDoesNotThrow(() -> InputValidator.checkVolumeRange(input),
                        "Input '" + input + "' with whitespace should be valid");
            }
        }
    }

    @Nested
    @DisplayName("Speed Range Validation Tests")
    class SpeedRangeTests {

        @Test
        @DisplayName("Should accept valid speed values")
        void shouldAcceptValidSpeedValues() {
            String[] validSpeeds = {"1", "2", "3", "4", "5"};

            for (String speed : validSpeeds) {
                assertDoesNotThrow(() -> InputValidator.checkSpeedRange(speed),
                        "Speed " + speed + " should be valid");
            }
        }

        @Test
        @DisplayName("Should reject speed below minimum")
        void shouldRejectSpeedBelowMinimum() {
            String[] invalidSpeeds = {"0", "-1", "-5"};

            for (String speed : invalidSpeeds) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.checkSpeedRange(speed));
                assertTrue(exception.getMessage().contains("Speed must be between 1 and 5"));
            }
        }

        @Test
        @DisplayName("Should reject speed above maximum")
        void shouldRejectSpeedAboveMaximum() {
            String[] invalidSpeeds = {"6", "10", "100"};

            for (String speed : invalidSpeeds) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.checkSpeedRange(speed));
                assertTrue(exception.getMessage().contains("Speed must be between 1 and 5"));
            }
        }

        @Test
        @DisplayName("Should reject null speed input")
        void shouldRejectNullSpeedInput() {
            UserException exception = assertThrows(UserException.class,
                    () -> InputValidator.checkSpeedRange(null));
            assertTrue(exception.getMessage().contains("Input cannot be empty"));
        }

        @Test
        @DisplayName("Should reject empty speed input")
        void shouldRejectEmptySpeedInput() {
            String[] emptyInputs = {"", "  ", "\t"};

            for (String input : emptyInputs) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.checkSpeedRange(input));
                assertTrue(exception.getMessage().contains("Input cannot be empty"));
            }
        }

        @Test
        @DisplayName("Should reject non-numeric speed input")
        void shouldRejectNonNumericSpeedInput() {
            String[] invalidInputs = {"high", "3.5", "medium", "fast"};

            for (String input : invalidInputs) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.checkSpeedRange(input));
                assertTrue(exception.getMessage().contains("Input must be a valid number"));
            }
        }
    }

    @Nested
    @DisplayName("Temperature Range Validation Tests")
    class TemperatureRangeTests {

        @Test
        @DisplayName("Should accept valid temperature values")
        void shouldAcceptValidTemperatureValues() {
            String[] validTemperatures = {"17", "20", "25", "28", "30"};

            for (String temp : validTemperatures) {
                assertDoesNotThrow(() -> InputValidator.checkTemperatureRange(temp),
                        "Temperature " + temp + " should be valid");
            }
        }

        @Test
        @DisplayName("Should reject temperature below minimum")
        void shouldRejectTemperatureBelowMinimum() {
            String[] invalidTemperatures = {"16", "10", "0", "-5"};

            for (String temp : invalidTemperatures) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.checkTemperatureRange(temp));
                assertTrue(exception.getMessage().contains("Temperature must be between 17 and 30"));
            }
        }

        @Test
        @DisplayName("Should reject temperature above maximum")
        void shouldRejectTemperatureAboveMaximum() {
            String[] invalidTemperatures = {"31", "35", "50", "100"};

            for (String temp : invalidTemperatures) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.checkTemperatureRange(temp));
                assertTrue(exception.getMessage().contains("Temperature must be between 17 and 30"));
            }
        }

        @Test
        @DisplayName("Should reject null temperature input")
        void shouldRejectNullTemperatureInput() {
            UserException exception = assertThrows(UserException.class,
                    () -> InputValidator.checkTemperatureRange(null));
            assertTrue(exception.getMessage().contains("Input cannot be empty"));
        }

        @Test
        @DisplayName("Should reject non-numeric temperature input")
        void shouldRejectNonNumericTemperatureInput() {
            String[] invalidInputs = {"cold", "25.5", "hot", "25°C"};

            for (String input : invalidInputs) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.checkTemperatureRange(input));
                assertTrue(exception.getMessage().contains("Input must be a valid number"));
            }
        }
    }

    @Test
    @DisplayName("Should accept valid date formats")
    void shouldAcceptValidDateFormats() {
        String[] validDates = {
                "2024-01-01", "2024-02-29", "2024-12-31",
                "2023-06-15", "2025-11-30"
        };

        for (String date : validDates) {
            assertDoesNotThrow(() -> InputValidator.checkValidDate(date),
                    "Date " + date + " should be valid");
        }
    }

    @Test
    @DisplayName("Should reject invalid date formats")
    void shouldRejectInvalidDateFormats() {
        String[] invalidFormats = {
                "2024/01/01", "01-01-2024", "2024-1-1",
                "24-01-01", "2024-Jan-01", "1/1/2024"
        };

        for (String date : invalidFormats) {
            UserException exception = assertThrows(UserException.class,
                    () -> InputValidator.checkValidDate(date));
            assertTrue(exception.getMessage().contains("Date must follow format YYYY-MM-DD"));
        }
    }

    @Test
    @DisplayName("Should reject invalid month values")
    void shouldRejectInvalidMonthValues() {
        String[] invalidMonths = {"2024-00-15", "2024-13-15", "2024-99-15"};

        for (String date : invalidMonths) {
            UserException exception = assertThrows(UserException.class,
                    () -> InputValidator.checkValidDate(date));
            assertTrue(exception.getMessage().contains("Invalid month value"));
        }
    }

    @Test
    @DisplayName("Should reject invalid day values")
    void shouldRejectInvalidDayValues() {
        String[] invalidDays = {
                "2024-01-00", "2024-01-32", "2024-02-30",
                "2024-04-31", "2024-11-31"
        };

        for (String date : invalidDays) {
            UserException exception = assertThrows(UserException.class,
                    () -> InputValidator.checkValidDate(date));
            assertTrue(exception.getMessage().contains("Invalid day value"));
        }
    }

    @Test
    @DisplayName("Should validate leap year correctly")
    void shouldValidateLeapYearCorrectly() {
        // Leap years
        assertDoesNotThrow(() -> InputValidator.checkValidDate("2024-02-29"));
        assertDoesNotThrow(() -> InputValidator.checkValidDate("2020-02-29"));

        // Non-leap years
        UserException exception = assertThrows(UserException.class,
                () -> InputValidator.checkValidDate("2023-02-29"));
        assertTrue(exception.getMessage().contains("Invalid day value"));
    }

    @Test
    @DisplayName("Should reject null date input")
    void shouldRejectNullDateInput() {
        UserException exception = assertThrows(UserException.class,
                () -> InputValidator.checkValidDate(null));
        assertTrue(exception.getMessage().contains("Input cannot be empty"));
    }

    @Test
    @DisplayName("Should reject empty date input")
    void shouldRejectEmptyDateInput() {
        String[] emptyInputs = {"", "  ", "\t"};

        for (String input : emptyInputs) {
            UserException exception = assertThrows(UserException.class,
                    () -> InputValidator.checkValidDate(input));
            assertTrue(exception.getMessage().contains("Input cannot be empty"));
        }
    }


    @Nested
    @DisplayName("Date Range Validation Tests")
    class DateRangeValidationTests {

        @Test
        @DisplayName("Should accept valid date ranges")
        void shouldAcceptValidDateRanges() {
            String[][] validRanges = {
                    {"2024-01-01", "2024-01-31"},
                    {"2024-01-15", "2024-01-15"}, // Same date
                    {"2023-12-31", "2024-01-01"},
                    {"2024-01-01", "2024-12-31"}
            };

            for (String[] range : validRanges) {
                assertDoesNotThrow(() -> InputValidator.validDateRange(range[0], range[1]),
                        "Date range " + range[0] + " to " + range[1] + " should be valid");
            }
        }

        @Test
        @DisplayName("Should reject invalid date ranges")
        void shouldRejectInvalidDateRanges() {
            String[][] invalidRanges = {
                    {"2024-01-31", "2024-01-01"},
                    {"2024-02-15", "2024-01-15"},
                    {"2024-12-31", "2024-01-01"}
            };

            for (String[] range : invalidRanges) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.validDateRange(range[0], range[1]));
                assertTrue(exception.getMessage().contains("Start date cannot be after end date"));
            }
        }

        @Test
        @DisplayName("Should reject date range with invalid dates")
        void shouldRejectDateRangeWithInvalidDates() {
            UserException exception = assertThrows(UserException.class,
                    () -> InputValidator.validDateRange("2024-13-01", "2024-12-31"));
            assertTrue(exception.getMessage().contains("Invalid month value"));
        }
    }

    @Nested
    @DisplayName("Sensor Data Fetch Limit Validation Tests")
    class SensorDataLimitTests {

        @Test
        @DisplayName("Should accept valid fetch limits")
        void shouldAcceptValidFetchLimits() {
            String[] validLimits = {"1", "5", "10"};

            for (String limit : validLimits) {
                assertDoesNotThrow(() -> InputValidator.validateSensorDataFetchLimit(limit),
                        "Limit " + limit + " should be valid");
            }
        }

        @Test
        @DisplayName("Should reject limits below minimum")
        void shouldRejectLimitsBelowMinimum() {
            String[] invalidLimits = {"0", "-1", "-5"};

            for (String limit : invalidLimits) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.validateSensorDataFetchLimit(limit));
                assertTrue(exception.getMessage().contains("Sensor data fetch limit must be between 1 and 10"));
            }
        }

        @Test
        @DisplayName("Should reject limits above maximum")
        void shouldRejectLimitsAboveMaximum() {
            String[] invalidLimits = {"11", "15", "100"};

            for (String limit : invalidLimits) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.validateSensorDataFetchLimit(limit));
                assertTrue(exception.getMessage().contains("Sensor data fetch limit must be between 1 and 10"));
            }
        }

        @Test
        @DisplayName("Should reject null limit input")
        void shouldRejectNullLimitInput() {
            UserException exception = assertThrows(UserException.class,
                    () -> InputValidator.validateSensorDataFetchLimit(null));
            assertTrue(exception.getMessage().contains("Input cannot be empty"));
        }

        @Test
        @DisplayName("Should reject non-numeric limit input")
        void shouldRejectNonNumericLimitInput() {
            String[] invalidInputs = {"all", "5.5", "ten", "max"};

            for (String input : invalidInputs) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.validateSensorDataFetchLimit(input));
                assertTrue(exception.getMessage().contains("Input must be a valid number"));
            }
        }
    }

    @Nested
    @DisplayName("Name Validation Tests")
    class NameValidationTests {

        @Test
        @DisplayName("Should accept valid names")
        void shouldAcceptValidNames() {
            String[] validNames = {
                    "Living Room AC", "Kitchen Fan", "Bedroom Speaker",
                    "Master AC", "Hall Light", "John Doe", "A", "AB CD EF"
            };

            for (String name : validNames) {
                assertDoesNotThrow(() -> InputValidator.validateName(name),
                        "Name '" + name + "' should be valid");
            }
        }

        @Test
        @DisplayName("Should reject names with numbers")
        void shouldRejectNamesWithNumbers() {
            String[] invalidNames = {"AC1", "Fan2", "Device123", "Room 101"};

            for (String name : invalidNames) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.validateName(name));
                assertTrue(exception.getMessage().contains("Name can only contain letters and spaces"));
            }
        }

        @Test
        @DisplayName("Should reject names with special characters")
        void shouldRejectNamesWithSpecialCharacters() {
            String[] invalidNames = {"AC@Home", "Fan-1", "Device_001", "Room#1", "AC!", "Device?"};

            for (String name : invalidNames) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.validateName(name));
                assertTrue(exception.getMessage().contains("Name can only contain letters and spaces"));
            }
        }

        @Test
        @DisplayName("Should reject null name input")
        void shouldRejectNullNameInput() {
            UserException exception = assertThrows(UserException.class,
                    () -> InputValidator.validateName(null));
            assertTrue(exception.getMessage().contains("Input cannot be empty"));
        }

        @Test
        @DisplayName("Should reject empty name input")
        void shouldRejectEmptyNameInput() {
            String[] emptyInputs = {"", "  ", "\t", "\n"};

            for (String input : emptyInputs) {
                UserException exception = assertThrows(UserException.class,
                        () -> InputValidator.validateName(input));
                assertTrue(exception.getMessage().contains("Input cannot be empty"));
            }
        }

        @Test
        @DisplayName("Should accept names with multiple spaces")
        void shouldAcceptNamesWithMultipleSpaces() {
            String[] validNames = {"Living  Room", "A   B   C", "Name with    spaces"};

            for (String name : validNames) {
                assertDoesNotThrow(() -> InputValidator.validateName(name),
                        "Name '" + name + "' with multiple spaces should be valid");
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle boundary values correctly")
        void shouldHandleBoundaryValuesCorrectly() {
            // Volume boundaries
            assertDoesNotThrow(() -> InputValidator.checkVolumeRange("1"));
            assertDoesNotThrow(() -> InputValidator.checkVolumeRange("100"));

            // Speed boundaries
            assertDoesNotThrow(() -> InputValidator.checkSpeedRange("1"));
            assertDoesNotThrow(() -> InputValidator.checkSpeedRange("5"));

            // Temperature boundaries
            assertDoesNotThrow(() -> InputValidator.checkTemperatureRange("17"));
            assertDoesNotThrow(() -> InputValidator.checkTemperatureRange("30"));

            // Sensor limit boundaries
            assertDoesNotThrow(() -> InputValidator.validateSensorDataFetchLimit("1"));
            assertDoesNotThrow(() -> InputValidator.validateSensorDataFetchLimit("10"));
        }

        @Test
        @DisplayName("Should handle large numbers")
        void shouldHandleLargeNumbers() {
            String largeNumber = "999999999";

            UserException volumeException = assertThrows(UserException.class,
                    () -> InputValidator.checkVolumeRange(largeNumber));
            assertTrue(volumeException.getMessage().contains("Volume must be between 1 and 100"));

            UserException speedException = assertThrows(UserException.class,
                    () -> InputValidator.checkSpeedRange(largeNumber));
            assertTrue(speedException.getMessage().contains("Speed must be between 1 and 5"));

            UserException tempException = assertThrows(UserException.class,
                    () -> InputValidator.checkTemperatureRange(largeNumber));
            assertTrue(tempException.getMessage().contains("Temperature must be between 17 and 30"));
        }

        @Test
        @DisplayName("Should handle integer overflow gracefully")
        void shouldHandleIntegerOverflowGracefully() {
            String overflowNumber = "999999999999999999999";

            assertThrows(UserException.class,
                    () -> InputValidator.checkVolumeRange(overflowNumber));
            assertThrows(UserException.class,
                    () -> InputValidator.checkSpeedRange(overflowNumber));
            assertThrows(UserException.class,
                    () -> InputValidator.checkTemperatureRange(overflowNumber));
            assertThrows(UserException.class,
                    () -> InputValidator.validateSensorDataFetchLimit(overflowNumber));
        }

        @Test
        @DisplayName("Should handle various whitespace combinations")
        void shouldHandleVariousWhitespaceCombinations() {
            String[] whitespaceVariations = {" 50 ", "\t50\t", "\n50\n", " \t50\t "};

            for (String input : whitespaceVariations) {
                assertDoesNotThrow(() -> InputValidator.checkVolumeRange(input),
                        "Volume input with whitespace '" + input + "' should be valid");
            }
        }
    }
}
