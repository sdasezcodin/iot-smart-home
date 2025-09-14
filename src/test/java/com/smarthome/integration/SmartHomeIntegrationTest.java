package com.smarthome.integration;

import com.smarthome.command.Remote;
import com.smarthome.command.Simulate;
import com.smarthome.command.Toggle;
import com.smarthome.exception.UserException;
import com.smarthome.factory.HaierFactory;
import com.smarthome.factory.LGFactory;
import com.smarthome.factory.SonyFactory;
import com.smarthome.model.Appliance;
import com.smarthome.model.SensorData;
import com.smarthome.observer.Observer;
import com.smarthome.util.DateTimeUtil;
import com.smarthome.util.IdGenerator;
import com.smarthome.util.InputValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Smart Home Integration Tests")
class SmartHomeIntegrationTest {

    private List<Appliance> appliances;
    private Remote remote;
    private TestDashboard dashboard;

    @BeforeEach
    void setUp() {
        appliances = new ArrayList<>();
        remote = new Remote();
        dashboard = new TestDashboard();
    }

    @Nested
    @DisplayName("Factory and Command Integration Tests")
    class FactoryCommandIntegrationTests {

        @Test
        @DisplayName("Should create appliances from different factories and control them with commands")
        void shouldCreateAppliancesFromDifferentFactoriesAndControlWithCommands() {
            // Create appliances using different factories
            HaierFactory haierFactory = new HaierFactory();
            LGFactory lgFactory = new LGFactory();
            SonyFactory sonyFactory = new SonyFactory();

            Appliance haierAC = haierFactory.acBuilder()
                    .id(IdGenerator.generateID())
                    .name("Living Room AC")
                    .build();

            Appliance lgFan = lgFactory.fanBuilder()
                    .id(IdGenerator.generateID())
                    .name("Bedroom Fan")
                    .build();

            Appliance sonySpeaker = sonyFactory.speakerBuilder()
                    .id(IdGenerator.generateID())
                    .name("Kitchen Speaker")
                    .build();

            appliances.add(haierAC);
            appliances.add(lgFan);
            appliances.add(sonySpeaker);

            // Attach dashboard to all appliances
            for (Appliance appliance : appliances) {
                appliance.attach(dashboard);
            }

            // Control AC with remote
            remote.setCommand(new Toggle(haierAC));
            remote.pressButton(); // Turn ON
            assertTrue(haierAC.isOn());

            remote.setCommand(new Simulate(haierAC, 22));
            remote.pressButton(); // Set temperature to 22°C
            assertEquals(22, haierAC.getLevel());
            assertEquals(690, haierAC.getPowerUsage()); // AC power calculation: 50 + (30-22)*80 = 690

            // Control Fan with remote
            remote.setCommand(new Toggle(lgFan));
            remote.pressButton(); // Turn ON
            assertTrue(lgFan.isOn());

            remote.setCommand(new Simulate(lgFan, 4));
            remote.pressButton(); // Set speed to level 4
            assertEquals(4, lgFan.getLevel());
            assertEquals(45, lgFan.getPowerUsage()); // Fan power calculation: fixed value for speed 4

            // Control Speaker with remote
            remote.setCommand(new Toggle(sonySpeaker));
            remote.pressButton(); // Turn ON
            assertTrue(sonySpeaker.isOn());

            remote.setCommand(new Simulate(sonySpeaker, 60));
            remote.pressButton(); // Set volume to 60%
            assertEquals(60, sonySpeaker.getLevel());
            assertEquals(35, sonySpeaker.getPowerUsage()); // Speaker power calculation: 5 + (60 * 0.5) = 35

            // Verify all appliances are different brands but working
            assertEquals("Haier", haierAC.getBrand());
            assertEquals("LG", lgFan.getBrand());
            assertEquals("Sony", sonySpeaker.getBrand());

            // Dashboard should have received notifications
            assertTrue(dashboard.getNotificationCount() > 0);
        }

        @Test
        @DisplayName("Should handle complete appliance lifecycle")
        void shouldHandleCompleteApplianceLifecycle() {
            // Create appliance
            HaierFactory factory = new HaierFactory();
            Appliance ac = factory.acBuilder()
                    .id(IdGenerator.generateID())
                    .name("Test AC")
                    .build();

            ac.attach(dashboard);
            dashboard.reset();

            // Initial state
            assertFalse(ac.isOn());
            assertFalse(ac.isOnline());
            assertEquals(24, ac.getLevel()); // Default AC temperature
            assertEquals(0, ac.getPowerUsage());

            // Turn on
            remote.setCommand(new Toggle(ac));
            remote.pressButton();
            assertTrue(ac.isOn());
            assertTrue(ac.getPowerUsage() > 0);

            // Simulate different temperatures
            int[] temperatures = {17, 20, 25, 30};
            for (int temp : temperatures) {
                remote.setCommand(new Simulate(ac, temp));
                remote.pressButton();
                assertEquals(temp, ac.getLevel());
                
                // Verify power calculation changes with temperature: 50 + (30-temp)*80
                int expectedPower = 50 + Math.max(0, (30 - temp) * 80);
                assertEquals(expectedPower, ac.getPowerUsage());
            }

            // Turn off
            remote.setCommand(new Toggle(ac));
            remote.pressButton();
            assertFalse(ac.isOn());
            assertEquals(0, ac.getPowerUsage());

            // Verify dashboard received all notifications
            assertTrue(dashboard.getNotificationCount() >= 6); // 1 toggle on + 4 simulates + 1 toggle off
        }
    }

    @Nested
    @DisplayName("Validation Integration Tests")
    class ValidationIntegrationTests {

        @Test
        @DisplayName("Should integrate validation with appliance control")
        void shouldIntegrateValidationWithApplianceControl() {
            HaierFactory factory = new HaierFactory();
            Appliance ac = factory.acBuilder()
                    .id(IdGenerator.generateID())
                    .name("Validation Test AC")
                    .build();

            ac.attach(dashboard);

            // Test valid AC temperature range
            String[] validTemperatures = {"17", "20", "25", "30"};
            for (String tempStr : validTemperatures) {
                assertDoesNotThrow(() -> {
                    InputValidator.checkTemperatureRange(tempStr);
                    int temp = Integer.parseInt(tempStr.trim());
                    
                    ac.setOn(true);
                    remote.setCommand(new Simulate(ac, temp));
                    remote.pressButton();
                    
                    assertEquals(temp, ac.getLevel());
                });
            }

            // Test invalid AC temperature range
            String[] invalidTemperatures = {"16", "31", "50", "-5"};
            for (String tempStr : invalidTemperatures) {
                assertThrows(UserException.class, () -> {
                    InputValidator.checkTemperatureRange(tempStr);
                });
            }
        }

        @Test
        @DisplayName("Should validate fan speed ranges correctly")
        void shouldValidateFanSpeedRangesCorrectly() {
            LGFactory factory = new LGFactory();
            Appliance fan = factory.fanBuilder()
                    .id(IdGenerator.generateID())
                    .name("Speed Test Fan")
                    .build();

            fan.attach(dashboard);
            fan.setOn(true);

            // Test valid fan speed range
            String[] validSpeeds = {"1", "2", "3", "4", "5"};
            for (String speedStr : validSpeeds) {
                assertDoesNotThrow(() -> {
                    InputValidator.checkSpeedRange(speedStr);
                    int speed = Integer.parseInt(speedStr.trim());
                    
                    remote.setCommand(new Simulate(fan, speed));
                    remote.pressButton();
                    
                    assertEquals(speed, fan.getLevel());
                    // Fan power calculation: fixed values for each speed
                    int expectedPower = switch (speed) {
                        case 1 -> 15;
                        case 2 -> 25;
                        case 3 -> 35;
                        case 4 -> 45;
                        case 5 -> 55;
                        default -> 0;
                    };
                    assertEquals(expectedPower, fan.getPowerUsage());
                });
            }

            // Test invalid fan speed range
            String[] invalidSpeeds = {"0", "6", "10", "-1"};
            for (String speedStr : invalidSpeeds) {
                assertThrows(UserException.class, () -> {
                    InputValidator.checkSpeedRange(speedStr);
                });
            }
        }

        @Test
        @DisplayName("Should validate speaker volume ranges correctly")
        void shouldValidateSpeakerVolumeRangesCorrectly() {
            SonyFactory factory = new SonyFactory();
            Appliance speaker = factory.speakerBuilder()
                    .id(IdGenerator.generateID())
                    .name("Volume Test Speaker")
                    .build();

            speaker.attach(dashboard);
            speaker.setOn(true);

            // Test valid speaker volume range
            String[] validVolumes = {"1", "25", "50", "75", "100"};
            for (String volumeStr : validVolumes) {
                assertDoesNotThrow(() -> {
                    InputValidator.checkVolumeRange(volumeStr);
                    int volume = Integer.parseInt(volumeStr.trim());
                    
                    remote.setCommand(new Simulate(speaker, volume));
                    remote.pressButton();
                    
                    assertEquals(volume, speaker.getLevel());
                    assertEquals(5 + (int)(volume * 0.5), speaker.getPowerUsage()); // Speaker power calculation: 5 + (volume * 0.5)
                });
            }

            // Test invalid speaker volume range
            String[] invalidVolumes = {"0", "101", "150", "-10"};
            for (String volumeStr : invalidVolumes) {
                assertThrows(UserException.class, () -> {
                    InputValidator.checkVolumeRange(volumeStr);
                });
            }
        }
    }

    @Nested
    @DisplayName("Data Model Integration Tests")
    class DataModelIntegrationTests {

        @Test
        @DisplayName("Should create complete sensor data entries")
        void shouldCreateCompleteSensorDataEntries() {
            // Create sensor data with current date/time utilities
            String currentDate = DateTimeUtil.getCurrentDate();
            String currentTime = DateTimeUtil.getCurrentTime();
            String sensorId = IdGenerator.generateReadingDataID();
            String deviceId = IdGenerator.generateID();

            SensorData sensorData = new SensorData.Builder()
                    .id(sensorId)
                    .deviceId(deviceId)
                    .date(currentDate)
                    .time(currentTime)
                    .data("25.5°C")
                    .build();

            // Verify structure
            assertNotNull(sensorData);
            assertEquals(sensorId, sensorData.getId());
            assertEquals(deviceId, sensorData.getDeviceId());
            assertEquals(currentDate, sensorData.getDate());
            assertEquals(currentTime, sensorData.getTime());
            assertEquals("25.5°C", sensorData.getData());

            // Verify date format
            assertTrue(currentDate.matches("\\d{4}-\\d{2}-\\d{2}"));
            assertTrue(currentTime.matches("\\d{2}:\\d{2}:\\d{2}"));

            // Verify IDs are unique
            String anotherId = IdGenerator.generateReadingDataID();
            assertNotEquals(sensorId, anotherId);
        }

        @Test
        @DisplayName("Should create appliances with sensor data simulation")
        void shouldCreateAppliancesWithSensorDataSimulation() {
            // Create multiple appliances
            HaierFactory haierFactory = new HaierFactory();
            LGFactory lgFactory = new LGFactory();
            SonyFactory sonyFactory = new SonyFactory();

            Appliance[] testAppliances = {
                haierFactory.acBuilder().id(IdGenerator.generateID()).name("AC1").build(),
                lgFactory.fanBuilder().id(IdGenerator.generateID()).name("Fan1").build(),
                sonyFactory.speakerBuilder().id(IdGenerator.generateID()).name("Speaker1").build()
            };

            List<SensorData> sensorReadings = new ArrayList<>();

            // Simulate sensor data collection for each appliance
            for (Appliance appliance : testAppliances) {
                appliance.attach(dashboard);
                appliance.toggleOnOff(); // Turn on

                // Simulate different levels and collect "sensor readings"
                for (int i = 0; i < 3; i++) {
                    int level = (i + 1) * 10;
                    appliance.simulate(level);

                    // Create sensor data entry
                    SensorData reading = new SensorData.Builder()
                            .id(IdGenerator.generateReadingDataID())
                            .deviceId(appliance.getId())
                            .date(DateTimeUtil.getCurrentDate())
                            .time(DateTimeUtil.getCurrentTime())
                            .data(generateSensorDataForAppliance(appliance))
                            .build();

                    sensorReadings.add(reading);
                }
            }

            // Verify we collected sensor data for all appliances
            assertEquals(9, sensorReadings.size()); // 3 appliances × 3 readings each
            
            // Verify each reading has valid structure
            for (SensorData reading : sensorReadings) {
                assertNotNull(reading.getId());
                assertNotNull(reading.getDeviceId());
                assertNotNull(reading.getDate());
                assertNotNull(reading.getTime());
                assertNotNull(reading.getData());
                assertTrue(reading.getDate().matches("\\d{4}-\\d{2}-\\d{2}"));
                assertTrue(reading.getTime().matches("\\d{2}:\\d{2}:\\d{2}"));
            }

            // Verify dashboard received all notifications
            assertTrue(dashboard.getNotificationCount() >= 12); // 3 toggles + 9 simulations
        }
    }

    @Nested
    @DisplayName("Observer Pattern Integration Tests")
    class ObserverPatternIntegrationTests {

        @Test
        @DisplayName("Should coordinate multiple appliances with central dashboard")
        void shouldCoordinateMultipleAppliancesWithCentralDashboard() {
            // Create multiple appliances from different factories
            HaierFactory haierFactory = new HaierFactory();
            LGFactory lgFactory = new LGFactory();
            SonyFactory sonyFactory = new SonyFactory();

            Appliance[] devices = {
                haierFactory.acBuilder().id("AC001").name("Living Room AC").build(),
                haierFactory.acBuilder().id("AC002").name("Bedroom AC").build(),
                lgFactory.fanBuilder().id("FAN001").name("Ceiling Fan").build(),
                lgFactory.fanBuilder().id("FAN002").name("Desk Fan").build(),
                sonyFactory.speakerBuilder().id("SPK001").name("Main Speaker").build(),
                sonyFactory.speakerBuilder().id("SPK002").name("Bathroom Speaker").build()
            };

            // Attach all devices to central dashboard
            for (Appliance device : devices) {
                device.attach(dashboard);
            }

            dashboard.reset();

            // Control all devices sequentially
            for (Appliance device : devices) {
                // Turn on each device
                remote.setCommand(new Toggle(device));
                remote.pressButton();
                assertTrue(device.isOn());

                // Simulate appropriate level for each device type
                int level = getAppropriateLevel(device.getType());
                remote.setCommand(new Simulate(device, level));
                remote.pressButton();
                assertEquals(level, device.getLevel());
            }

            // Verify dashboard received notifications from all devices
            assertTrue(dashboard.getNotificationCount() >= 12); // 6 toggles + 6 simulations
            
            // Verify all devices are on and configured
            for (Appliance device : devices) {
                assertTrue(device.isOn());
                assertTrue(device.getPowerUsage() > 0);
            }
        }

        @Test
        @DisplayName("Should handle observer attachment and detachment during operation")
        void shouldHandleObserverAttachmentAndDetachmentDuringOperation() {
            HaierFactory factory = new HaierFactory();
            Appliance ac = factory.acBuilder()
                    .id(IdGenerator.generateID())
                    .name("Dynamic Observer AC")
                    .build();

            TestDashboard dashboard1 = new TestDashboard();
            TestDashboard dashboard2 = new TestDashboard();

            // Start with no observers
            remote.setCommand(new Toggle(ac));
            remote.pressButton();
            assertTrue(ac.isOn());
            assertEquals(0, dashboard1.getNotificationCount());
            assertEquals(0, dashboard2.getNotificationCount());

            // Attach first observer
            ac.attach(dashboard1);
            remote.setCommand(new Simulate(ac, 25));
            remote.pressButton();
            assertEquals(1, dashboard1.getNotificationCount());
            assertEquals(0, dashboard2.getNotificationCount());

            // Attach second observer
            ac.attach(dashboard2);
            remote.setCommand(new Simulate(ac, 20));
            remote.pressButton();
            assertEquals(2, dashboard1.getNotificationCount());
            assertEquals(1, dashboard2.getNotificationCount());

            // Detach first observer
            ac.detach(dashboard1);
            remote.setCommand(new Simulate(ac, 30));
            remote.pressButton();
            assertEquals(2, dashboard1.getNotificationCount()); // No change
            assertEquals(2, dashboard2.getNotificationCount()); // Incremented
        }
    }

    @Nested
    @DisplayName("Concurrent Operations Integration Tests")
    class ConcurrentOperationsIntegrationTests {

        @Test
        @DisplayName("Should handle concurrent appliance operations safely")
        void shouldHandleConcurrentApplianceOperationsSafely() throws InterruptedException {
            HaierFactory factory = new HaierFactory();
            Appliance ac = factory.acBuilder()
                    .id(IdGenerator.generateID())
                    .name("Concurrent Test AC")
                    .build();
            
            ac.attach(dashboard);
            dashboard.reset();
            
            // Create multiple threads to simulate concurrent access
            Thread thread1 = new Thread(() -> {
                for (int i = 0; i < 5; i++) {
                    ac.toggleOnOff();
                    try { Thread.sleep(10); } catch (InterruptedException ignored) {}
                }
            });
            
            Thread thread2 = new Thread(() -> {
                for (int temp = 17; temp <= 30; temp++) {
                    ac.simulate(temp);
                    try { Thread.sleep(15); } catch (InterruptedException ignored) {}
                }
            });
            
            // Start threads and wait for completion
            thread1.start();
            thread2.start();
            thread1.join();
            thread2.join();
            
            // Verify the appliance is still in a consistent state
            assertNotNull(ac.getId());
            assertNotNull(ac.getName());
            assertTrue(ac.getLevel() >= 17 && ac.getLevel() <= 30);
            assertTrue(dashboard.getNotificationCount() > 0);
        }
    }

    @Nested
    @DisplayName("Error Handling Integration Tests")
    class ErrorHandlingIntegrationTests {

        @Test
        @DisplayName("Should handle validation errors gracefully in integrated workflow")
        void shouldHandleValidationErrorsGracefullyInIntegratedWorkflow() {
            HaierFactory factory = new HaierFactory();
            Appliance ac = factory.acBuilder()
                    .id(IdGenerator.generateID())
                    .name("Error Test AC")
                    .build();

            ac.attach(dashboard);
            dashboard.reset();

            // Valid operation should work
            assertDoesNotThrow(() -> {
                InputValidator.checkTemperatureRange("25");
                ac.setOn(true);
                remote.setCommand(new Simulate(ac, 25));
                remote.pressButton();
            });

            assertEquals(25, ac.getLevel());
            assertTrue(dashboard.getNotificationCount() > 0);

            // Invalid validation should throw exception but not affect appliance
            assertThrows(UserException.class, () -> {
                InputValidator.checkTemperatureRange("50"); // Invalid temperature
            });

            // Appliance should still be in previous valid state
            assertEquals(25, ac.getLevel());
            assertTrue(ac.isOn());
        }

        @Test
        @DisplayName("Should handle edge cases and boundary conditions")
        void shouldHandleEdgeCasesAndBoundaryConditions() {
            // Test with different appliance types and their boundary values
            HaierFactory haierFactory = new HaierFactory();
            LGFactory lgFactory = new LGFactory();
            SonyFactory sonyFactory = new SonyFactory();
            
            // Test AC with temperature boundaries
            Appliance ac = haierFactory.acBuilder()
                    .id(IdGenerator.generateID())
                    .name("Boundary Test AC")
                    .build();
            ac.attach(dashboard);
            ac.setOn(true);
            
            // Test minimum and maximum temperatures
            ac.simulate(17); // Minimum temp
            assertEquals(17, ac.getLevel());
            assertEquals(50 + (30-17)*80, ac.getPowerUsage()); // 1090 watts
            
            ac.simulate(30); // Maximum temp
            assertEquals(30, ac.getLevel());
            assertEquals(50, ac.getPowerUsage()); // Base power only
            
            // Test Fan with speed boundaries
            Appliance fan = lgFactory.fanBuilder()
                    .id(IdGenerator.generateID())
                    .name("Boundary Test Fan")
                    .build();
            fan.attach(dashboard);
            fan.setOn(true);
            
            fan.simulate(1); // Minimum speed
            assertEquals(1, fan.getLevel());
            assertEquals(15, fan.getPowerUsage());
            
            fan.simulate(5); // Maximum speed
            assertEquals(5, fan.getLevel());
            assertEquals(55, fan.getPowerUsage());
            
            // Test Speaker with volume boundaries
            Appliance speaker = sonyFactory.speakerBuilder()
                    .id(IdGenerator.generateID())
                    .name("Boundary Test Speaker")
                    .build();
            speaker.attach(dashboard);
            speaker.setOn(true);
            
            speaker.simulate(1); // Minimum volume
            assertEquals(1, speaker.getLevel());
            assertEquals(5 + (int)(1 * 0.5), speaker.getPowerUsage()); // 5 watts
            
            speaker.simulate(100); // Maximum volume
            assertEquals(100, speaker.getLevel());
            assertEquals(5 + (int)(100 * 0.5), speaker.getPowerUsage()); // 55 watts
        }

        @Test
        @DisplayName("Should handle null values gracefully in integrated components")
        void shouldHandleNullValuesGracefullyInIntegratedComponents() {
            // Test null handling in different components
            assertThrows(UserException.class, () -> {
                InputValidator.validateName(null);
            });

            assertThrows(UserException.class, () -> {
                InputValidator.checkValidDate(null);
            });

            // SensorData should handle null values
            SensorData nullData = new SensorData.Builder()
                    .id(null)
                    .deviceId(null)
                    .date(null)
                    .time(null)
                    .data(null)
                    .build();

            assertNotNull(nullData);
            assertNull(nullData.getId());
        }
    }

    // Helper methods
    private String generateSensorDataForAppliance(Appliance appliance) {
        return switch (appliance.getType().toUpperCase()) {
            case "AC" -> appliance.getLevel() + "°C";
            case "FAN" -> "Speed Level " + appliance.getLevel();
            case "SPEAKER" -> appliance.getLevel() + "% Volume";
            default -> "Level " + appliance.getLevel();
        };
    }

    private int getAppropriateLevel(String type) {
        return switch (type.toUpperCase()) {
            case "AC" -> 22; // 22°C
            case "FAN" -> 4;  // Speed level 4
            case "SPEAKER" -> 60; // 60% volume
            default -> 50;
        };
    }

    // Test Dashboard Observer implementation
    private static class TestDashboard implements Observer {
        private int notificationCount = 0;
        private String lastMessage = "";

        @Override
        public void update(String message) {
            this.notificationCount++;
            this.lastMessage = message;
        }

        public int getNotificationCount() {
            return notificationCount;
        }

        public String getLastMessage() {
            return lastMessage;
        }

        public void reset() {
            this.notificationCount = 0;
            this.lastMessage = "";
        }
    }
}