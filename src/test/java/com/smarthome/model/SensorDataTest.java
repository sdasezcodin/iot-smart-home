package com.smarthome.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SensorData Tests")
class SensorDataTest {

    private SensorData sensorData;

    @BeforeEach
    void setUp() {
        sensorData = new SensorData.Builder()
                .id("SENSOR001")
                .deviceId("DEVICE001")
                .date("2024-01-15")
                .time("14:30:00")
                .data("25.5°C")
                .build();
    }

    @Nested
    @DisplayName("Constructor and Builder Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create sensor data with builder pattern")
        void shouldCreateSensorDataWithBuilder() {
            assertNotNull(sensorData);
            assertEquals("SENSOR001", sensorData.getId());
            assertEquals("DEVICE001", sensorData.getDeviceId());
            assertEquals("2024-01-15", sensorData.getDate());
            assertEquals("14:30:00", sensorData.getTime());
            assertEquals("25.5°C", sensorData.getData());
        }

        @Test
        @DisplayName("Should create sensor data with default constructor")
        void shouldCreateSensorDataWithDefaultConstructor() {
            SensorData defaultSensorData = new SensorData();
            assertNotNull(defaultSensorData);
            assertNull(defaultSensorData.getId());
            assertNull(defaultSensorData.getDeviceId());
            assertNull(defaultSensorData.getDate());
            assertNull(defaultSensorData.getTime());
            assertNull(defaultSensorData.getData());
        }

        @Test
        @DisplayName("Should create sensor data with different data types")
        void shouldCreateSensorDataWithDifferentDataTypes() {
            SensorData temperatureData = new SensorData.Builder()
                    .id("TEMP001")
                    .deviceId("AC001")
                    .date("2024-01-15")
                    .time("10:00:00")
                    .data("23.5°C")
                    .build();

            SensorData humidityData = new SensorData.Builder()
                    .id("HUM001")
                    .deviceId("HUM001")
                    .date("2024-01-15")
                    .time("10:00:00")
                    .data("65%")
                    .build();

            SensorData powerData = new SensorData.Builder()
                    .id("PWR001")
                    .deviceId("FAN001")
                    .date("2024-01-15")
                    .time("10:00:00")
                    .data("45W")
                    .build();

            assertEquals("23.5°C", temperatureData.getData());
            assertEquals("65%", humidityData.getData());
            assertEquals("45W", powerData.getData());
        }

        @Test
        @DisplayName("Should create sensor data with empty values")
        void shouldCreateSensorDataWithEmptyValues() {
            SensorData emptySensorData = new SensorData.Builder()
                    .id("")
                    .deviceId("")
                    .date("")
                    .time("")
                    .data("")
                    .build();

            assertNotNull(emptySensorData);
            assertEquals("", emptySensorData.getId());
            assertEquals("", emptySensorData.getDeviceId());
            assertEquals("", emptySensorData.getDate());
            assertEquals("", emptySensorData.getTime());
            assertEquals("", emptySensorData.getData());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set id correctly")
        void shouldGetAndSetId() {
            sensorData.setId("SENSOR002");
            assertEquals("SENSOR002", sensorData.getId());
        }

        @Test
        @DisplayName("Should get and set deviceId correctly")
        void shouldGetAndSetDeviceId() {
            sensorData.setDeviceId("DEVICE002");
            assertEquals("DEVICE002", sensorData.getDeviceId());
        }

        @Test
        @DisplayName("Should get and set date correctly")
        void shouldGetAndSetDate() {
            sensorData.setDate("2024-01-16");
            assertEquals("2024-01-16", sensorData.getDate());
        }

        @Test
        @DisplayName("Should get and set time correctly")
        void shouldGetAndSetTime() {
            sensorData.setTime("15:45:30");
            assertEquals("15:45:30", sensorData.getTime());
        }

        @Test
        @DisplayName("Should get and set data correctly")
        void shouldGetAndSetData() {
            sensorData.setData("28.3°C");
            assertEquals("28.3°C", sensorData.getData());
        }

        @Test
        @DisplayName("Should handle null values in setters")
        void shouldHandleNullValuesInSetters() {
            assertDoesNotThrow(() -> {
                sensorData.setId(null);
                sensorData.setDeviceId(null);
                sensorData.setDate(null);
                sensorData.setTime(null);
                sensorData.setData(null);
            });

            assertNull(sensorData.getId());
            assertNull(sensorData.getDeviceId());
            assertNull(sensorData.getDate());
            assertNull(sensorData.getTime());
            assertNull(sensorData.getData());
        }
    }

    @Nested
    @DisplayName("Builder Pattern Tests")
    class BuilderPatternTests {

        @Test
        @DisplayName("Should chain builder methods")
        void shouldChainBuilderMethods() {
            SensorData chainedSensorData = new SensorData.Builder()
                    .id("CHAIN001")
                    .deviceId("DEVICE_CHAIN")
                    .date("2024-02-01")
                    .time("12:00:00")
                    .data("30.0°C")
                    .build();

            assertEquals("CHAIN001", chainedSensorData.getId());
            assertEquals("DEVICE_CHAIN", chainedSensorData.getDeviceId());
            assertEquals("2024-02-01", chainedSensorData.getDate());
            assertEquals("12:00:00", chainedSensorData.getTime());
            assertEquals("30.0°C", chainedSensorData.getData());
        }

        @Test
        @DisplayName("Should build with partial data")
        void shouldBuildWithPartialData() {
            SensorData partialSensorData = new SensorData.Builder()
                    .id("PARTIAL001")
                    .data("22.1°C")
                    .build();

            assertEquals("PARTIAL001", partialSensorData.getId());
            assertNull(partialSensorData.getDeviceId());
            assertNull(partialSensorData.getDate());
            assertNull(partialSensorData.getTime());
            assertEquals("22.1°C", partialSensorData.getData());
        }

        @Test
        @DisplayName("Should create multiple instances with same builder pattern")
        void shouldCreateMultipleInstancesWithSameBuilderPattern() {
            SensorData.Builder builder = new SensorData.Builder()
                    .deviceId("SHARED_DEVICE")
                    .date("2024-01-15");

            SensorData reading1 = builder
                    .id("READ001")
                    .time("09:00:00")
                    .data("20.0°C")
                    .build();

            SensorData reading2 = builder
                    .id("READ002")
                    .time("09:15:00")
                    .data("20.5°C")
                    .build();

            assertEquals("READ001", reading1.getId());
            assertEquals("READ002", reading2.getId());
            assertEquals("SHARED_DEVICE", reading1.getDeviceId());
            assertEquals("SHARED_DEVICE", reading2.getDeviceId());
        }
    }

    @Nested
    @DisplayName("Data Format Tests")
    class DataFormatTests {

        @Test
        @DisplayName("Should handle different date formats")
        void shouldHandleDifferentDateFormats() {
            SensorData sensor1 = new SensorData.Builder()
                    .id("DATE001")
                    .date("2024-01-01")
                    .build();

            SensorData sensor2 = new SensorData.Builder()
                    .id("DATE002")
                    .date("2024-12-31")
                    .build();

            assertEquals("2024-01-01", sensor1.getDate());
            assertEquals("2024-12-31", sensor2.getDate());
        }

        @Test
        @DisplayName("Should handle different time formats")
        void shouldHandleDifferentTimeFormats() {
            SensorData sensor1 = new SensorData.Builder()
                    .id("TIME001")
                    .time("00:00:00")
                    .build();

            SensorData sensor2 = new SensorData.Builder()
                    .id("TIME002")
                    .time("23:59:59")
                    .build();

            assertEquals("00:00:00", sensor1.getTime());
            assertEquals("23:59:59", sensor2.getTime());
        }

        @Test
        @DisplayName("Should handle different sensor data formats")
        void shouldHandleDifferentSensorDataFormats() {
            SensorData[] sensors = {
                new SensorData.Builder().id("TEMP001").data("25.5°C").build(),
                new SensorData.Builder().id("HUM001").data("60%").build(),
                new SensorData.Builder().id("POWER001").data("150W").build(),
                new SensorData.Builder().id("SPEED001").data("Level 3").build(),
                new SensorData.Builder().id("VOL001").data("75%").build(),
                new SensorData.Builder().id("STATUS001").data("ON").build(),
                new SensorData.Builder().id("ERROR001").data("Error: Connection Lost").build()
            };

            assertEquals("25.5°C", sensors[0].getData());
            assertEquals("60%", sensors[1].getData());
            assertEquals("150W", sensors[2].getData());
            assertEquals("Level 3", sensors[3].getData());
            assertEquals("75%", sensors[4].getData());
            assertEquals("ON", sensors[5].getData());
            assertEquals("Error: Connection Lost", sensors[6].getData());
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should accept valid date format YYYY-MM-DD")
        void shouldAcceptValidDateFormat() {
            String[] validDates = {
                "2024-01-01", "2024-02-29", "2024-12-31",
                "2023-01-01", "2025-12-31"
            };

            for (String date : validDates) {
                assertDoesNotThrow(() -> {
                    SensorData sensor = new SensorData.Builder()
                            .id("VALID_DATE")
                            .date(date)
                            .build();
                    assertEquals(date, sensor.getDate());
                });
            }
        }

        @Test
        @DisplayName("Should accept valid time format HH:MM:SS")
        void shouldAcceptValidTimeFormat() {
            String[] validTimes = {
                "00:00:00", "12:00:00", "23:59:59",
                "01:30:45", "18:22:33"
            };

            for (String time : validTimes) {
                assertDoesNotThrow(() -> {
                    SensorData sensor = new SensorData.Builder()
                            .id("VALID_TIME")
                            .time(time)
                            .build();
                    assertEquals(time, sensor.getTime());
                });
            }
        }

        @Test
        @DisplayName("Should accept various data values")
        void shouldAcceptVariousDataValues() {
            String[] validDataValues = {
                "25.5", "100", "-10.2", "OFF", "ERROR", 
                "25.5°C", "60%", "150W", "Level 5", "Status: OK"
            };

            for (String data : validDataValues) {
                assertDoesNotThrow(() -> {
                    SensorData sensor = new SensorData.Builder()
                            .id("VALID_DATA")
                            .data(data)
                            .build();
                    assertEquals(data, sensor.getData());
                });
            }
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle very long strings")
        void shouldHandleVeryLongStrings() {
            String longString = "A".repeat(1000);
            
            SensorData longSensorData = new SensorData.Builder()
                    .id(longString)
                    .deviceId(longString)
                    .date("2024-01-15")
                    .time("12:00:00")
                    .data(longString)
                    .build();

            assertEquals(longString, longSensorData.getId());
            assertEquals(longString, longSensorData.getDeviceId());
            assertEquals(longString, longSensorData.getData());
        }

        @Test
        @DisplayName("Should handle special characters")
        void shouldHandleSpecialCharacters() {
            String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
            
            SensorData specialSensorData = new SensorData.Builder()
                    .id("SPECIAL001")
                    .deviceId("Device_" + specialChars)
                    .data("Value: " + specialChars)
                    .build();

            assertEquals("Device_" + specialChars, specialSensorData.getDeviceId());
            assertEquals("Value: " + specialChars, specialSensorData.getData());
        }

        @Test
        @DisplayName("Should handle unicode characters")
        void shouldHandleUnicodeCharacters() {
            String unicode = "温度: 25°C 湿度: 60% ❄️";
            
            SensorData unicodeSensorData = new SensorData.Builder()
                    .id("UNICODE001")
                    .data(unicode)
                    .build();

            assertEquals(unicode, unicodeSensorData.getData());
        }
    }

    @Test
    @DisplayName("Should generate correct toString representation")
    void shouldGenerateCorrectToStringRepresentation() {
        String toStringResult = sensorData.toString();
        
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("SENSOR001"));
        assertTrue(toStringResult.contains("DEVICE001"));
        assertTrue(toStringResult.contains("2024-01-15"));
        assertTrue(toStringResult.contains("14:30:00"));
        assertTrue(toStringResult.contains("25.5°C"));
        assertTrue(toStringResult.startsWith("SensorData{"));
        assertTrue(toStringResult.endsWith("}"));
    }

    @Test
    @DisplayName("Should generate toString for null values")
    void shouldGenerateToStringForNullValues() {
        SensorData nullSensorData = new SensorData();
        String toStringResult = nullSensorData.toString();
        
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("id='null'"));
        assertTrue(toStringResult.contains("deviceId='null'"));
        assertTrue(toStringResult.contains("date='null'"));
        assertTrue(toStringResult.contains("time='null'"));
        assertTrue(toStringResult.contains("data='null'"));
    }

    @Test
    @DisplayName("Should generate toString for empty values")
    void shouldGenerateToStringForEmptyValues() {
        SensorData emptySensorData = new SensorData.Builder()
                .id("")
                .deviceId("")
                .date("")
                .time("")
                .data("")
                .build();
        
        String toStringResult = emptySensorData.toString();
        
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("id=''"));
        assertTrue(toStringResult.contains("deviceId=''"));
        assertTrue(toStringResult.contains("date=''"));
        assertTrue(toStringResult.contains("time=''"));
        assertTrue(toStringResult.contains("data=''"));
    }
}