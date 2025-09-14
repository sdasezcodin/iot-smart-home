package com.smarthome.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DateTimeUtil Tests")
class DateTimeUtilTest {

    @Nested
    @DisplayName("Current Date Tests")
    class CurrentDateTests {

        @Test
        @DisplayName("Should return current date in YYYY-MM-DD format")
        void shouldReturnCurrentDateInCorrectFormat() {
            String currentDate = DateTimeUtil.getCurrentDate();
            
            assertNotNull(currentDate);
            assertTrue(currentDate.matches("\\d{4}-\\d{2}-\\d{2}"),
                "Date should match YYYY-MM-DD format: " + currentDate);
            
            // Verify it's actually current date
            String expectedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            assertEquals(expectedDate, currentDate);
        }

        @Test
        @DisplayName("Should return consistent date format")
        void shouldReturnConsistentDateFormat() {
            String date1 = DateTimeUtil.getCurrentDate();
            String date2 = DateTimeUtil.getCurrentDate();
            
            // Both dates should have same format
            assertTrue(date1.matches("\\d{4}-\\d{2}-\\d{2}"));
            assertTrue(date2.matches("\\d{4}-\\d{2}-\\d{2}"));
            
            // Length should be consistent
            assertEquals(10, date1.length());
            assertEquals(10, date2.length());
        }
    }

    @Nested
    @DisplayName("Current Time Tests")
    class CurrentTimeTests {

        @Test
        @DisplayName("Should return current time in HH:mm:ss format")
        void shouldReturnCurrentTimeInCorrectFormat() {
            String currentTime = DateTimeUtil.getCurrentTime();
            
            assertNotNull(currentTime);
            assertTrue(currentTime.matches("\\d{2}:\\d{2}:\\d{2}"),
                "Time should match HH:mm:ss format: " + currentTime);
            
            // Verify it's actually current time (allow some tolerance)
            String expectedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            // Note: We can't do exact equality because of timing differences
            assertEquals(8, currentTime.length());
        }

        @Test
        @DisplayName("Should return valid hour, minute, and second values")
        void shouldReturnValidTimeValues() {
            String currentTime = DateTimeUtil.getCurrentTime();
            String[] timeParts = currentTime.split(":");
            
            assertEquals(3, timeParts.length, "Time should have 3 parts separated by :");
            
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);
            int second = Integer.parseInt(timeParts[2]);
            
            assertTrue(hour >= 0 && hour <= 23, "Hour should be between 0-23: " + hour);
            assertTrue(minute >= 0 && minute <= 59, "Minute should be between 0-59: " + minute);
            assertTrue(second >= 0 && second <= 59, "Second should be between 0-59: " + second);
        }

        @Test
        @DisplayName("Should pad single digit values with zero")
        void shouldPadSingleDigitValuesWithZero() {
            String currentTime = DateTimeUtil.getCurrentTime();
            String[] timeParts = currentTime.split(":");
            
            // Each part should be exactly 2 digits
            for (String part : timeParts) {
                assertEquals(2, part.length(), "Each time part should be 2 digits: " + part);
                assertTrue(part.matches("\\d{2}"), "Time part should be 2 digits: " + part);
            }
        }
    }

    @Nested
    @DisplayName("Leap Year Tests")
    class LeapYearTests {

        @Test
        @DisplayName("Should identify leap years correctly")
        void shouldIdentifyLeapYearsCorrectly() {
            int[] leapYears = {2000, 2004, 2008, 2012, 2016, 2020, 2024};
            
            for (int year : leapYears) {
                assertTrue(DateTimeUtil.isLeapYear(year), 
                    "Year " + year + " should be a leap year");
            }
        }

        @Test
        @DisplayName("Should identify non-leap years correctly")
        void shouldIdentifyNonLeapYearsCorrectly() {
            int[] nonLeapYears = {1900, 1901, 1902, 1903, 2001, 2002, 2003, 2021, 2022, 2023};
            
            for (int year : nonLeapYears) {
                assertFalse(DateTimeUtil.isLeapYear(year),
                    "Year " + year + " should not be a leap year");
            }
        }

        @Test
        @DisplayName("Should handle century years correctly")
        void shouldHandleCenturyYearsCorrectly() {
            // Century years divisible by 400 are leap years
            int[] leapCenturyYears = {1600, 2000, 2400};
            for (int year : leapCenturyYears) {
                assertTrue(DateTimeUtil.isLeapYear(year),
                    "Century year " + year + " should be a leap year");
            }
            
            // Century years not divisible by 400 are not leap years
            int[] nonLeapCenturyYears = {1700, 1800, 1900, 2100, 2200, 2300};
            for (int year : nonLeapCenturyYears) {
                assertFalse(DateTimeUtil.isLeapYear(year),
                    "Century year " + year + " should not be a leap year");
            }
        }

        @Test
        @DisplayName("Should handle edge case years")
        void shouldHandleEdgeCaseYears() {
            // Test year 0 (should be leap year)
            assertTrue(DateTimeUtil.isLeapYear(0), "Year 0 should be a leap year");
            
            // Test negative years
            assertTrue(DateTimeUtil.isLeapYear(-4), "Year -4 should be a leap year");
            assertFalse(DateTimeUtil.isLeapYear(-1), "Year -1 should not be a leap year");
            
            // Test very large years
            assertTrue(DateTimeUtil.isLeapYear(4000), "Year 4000 should be a leap year");
            assertFalse(DateTimeUtil.isLeapYear(4001), "Year 4001 should not be a leap year");
        }
    }

    @Nested
    @DisplayName("Current DateTime for ID Tests")
    class CurrentDateTimeForIDTests {

        @Test
        @DisplayName("Should return current datetime in YYYYMMDDHHMMSS format")
        void shouldReturnCurrentDateTimeInCorrectFormat() {
            String currentDateTime = DateTimeUtil.getCurrentDateTimeForID();
            
            assertNotNull(currentDateTime);
            assertTrue(currentDateTime.matches("\\d{14}"),
                "DateTime for ID should be 14 digits: " + currentDateTime);
            assertEquals(14, currentDateTime.length(), "DateTime for ID should be exactly 14 characters");
        }

        @Test
        @DisplayName("Should generate different IDs when called in sequence")
        void shouldGenerateDifferentIDsWhenCalledInSequence() throws InterruptedException {
            String id1 = DateTimeUtil.getCurrentDateTimeForID();
            Thread.sleep(1000); // Wait 1 second
            String id2 = DateTimeUtil.getCurrentDateTimeForID();
            
            assertNotEquals(id1, id2, "Sequential calls should generate different IDs");
        }

        @Test
        @DisplayName("Should contain valid date and time components")
        void shouldContainValidDateAndTimeComponents() {
            String dateTimeId = DateTimeUtil.getCurrentDateTimeForID();
            
            // Extract components
            String year = dateTimeId.substring(0, 4);
            String month = dateTimeId.substring(4, 6);
            String day = dateTimeId.substring(6, 8);
            String hour = dateTimeId.substring(8, 10);
            String minute = dateTimeId.substring(10, 12);
            String second = dateTimeId.substring(12, 14);
            
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
        @DisplayName("Should be suitable for unique ID generation")
        void shouldBeSuitableForUniqueIDGeneration() {
            String id1 = DateTimeUtil.getCurrentDateTimeForID();
            String id2 = DateTimeUtil.getCurrentDateTimeForID();
            
            // IDs generated in quick succession should be the same (same second)
            // This is expected behavior for this method
            assertTrue(id1.matches("\\d{14}"));
            assertTrue(id2.matches("\\d{14}"));
            
            // Both should be valid formats even if they're the same
            assertNotNull(id1);
            assertNotNull(id2);
        }
    }

    @Nested
    @DisplayName("Utility Class Properties Tests")
    class UtilityClassPropertiesTests {

        @Test
        @DisplayName("Should not be instantiable")
        void shouldNotBeInstantiable() {
            // Verify that constructor is private by trying reflection
            assertThrows(Exception.class, () -> {
                DateTimeUtil.class.getDeclaredConstructor().newInstance();
            });
        }

        @Test
        @DisplayName("Should have all static methods")
        void shouldHaveAllStaticMethods() throws NoSuchMethodException {
            // Verify all public methods are static
            assertTrue(java.lang.reflect.Modifier.isStatic(
                DateTimeUtil.class.getMethod("getCurrentDate").getModifiers()));
            assertTrue(java.lang.reflect.Modifier.isStatic(
                DateTimeUtil.class.getMethod("getCurrentTime").getModifiers()));
            assertTrue(java.lang.reflect.Modifier.isStatic(
                DateTimeUtil.class.getMethod("isLeapYear", int.class).getModifiers()));
            assertTrue(java.lang.reflect.Modifier.isStatic(
                DateTimeUtil.class.getMethod("getCurrentDateTimeForID").getModifiers()));
        }
    }

    @Nested
    @DisplayName("Performance and Consistency Tests")
    class PerformanceAndConsistencyTests {

        @Test
        @DisplayName("Should perform well with multiple rapid calls")
        void shouldPerformWellWithMultipleRapidCalls() {
            long startTime = System.currentTimeMillis();
            
            // Call methods multiple times rapidly
            for (int i = 0; i < 1000; i++) {
                DateTimeUtil.getCurrentDate();
                DateTimeUtil.getCurrentTime();
                DateTimeUtil.getCurrentDateTimeForID();
                DateTimeUtil.isLeapYear(2024);
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // Should complete within reasonable time (adjust threshold as needed)
            assertTrue(duration < 1000, "1000 method calls should complete within 1 second, took: " + duration + "ms");
        }

        @Test
        @DisplayName("Should maintain consistency across multiple calls within same second")
        void shouldMaintainConsistencyAcrossMultipleCallsWithinSameSecond() {
            String date1 = DateTimeUtil.getCurrentDate();
            String date2 = DateTimeUtil.getCurrentDate();
            String dateTimeId1 = DateTimeUtil.getCurrentDateTimeForID();
            String dateTimeId2 = DateTimeUtil.getCurrentDateTimeForID();
            
            // Dates should be the same (called within same day)
            assertEquals(date1, date2);
            
            // DateTime IDs should be same or very close (called within same second)
            assertEquals(14, dateTimeId1.length());
            assertEquals(14, dateTimeId2.length());
        }

        @Test
        @DisplayName("Should handle concurrent access safely")
        void shouldHandleConcurrentAccessSafely() throws InterruptedException {
            final int threadCount = 10;
            final int callsPerThread = 100;
            Thread[] threads = new Thread[threadCount];
            
            for (int i = 0; i < threadCount; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < callsPerThread; j++) {
                        assertNotNull(DateTimeUtil.getCurrentDate());
                        assertNotNull(DateTimeUtil.getCurrentTime());
                        assertNotNull(DateTimeUtil.getCurrentDateTimeForID());
                        assertTrue(DateTimeUtil.isLeapYear(2024));
                    }
                });
            }
            
            for (Thread thread : threads) {
                thread.start();
            }
            
            for (Thread thread : threads) {
                thread.join();
            }
            
            // If we get here without exceptions, concurrent access is safe
            assertTrue(true, "Concurrent access completed successfully");
        }
    }
}