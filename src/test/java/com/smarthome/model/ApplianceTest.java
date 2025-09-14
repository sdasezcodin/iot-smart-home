package com.smarthome.model;

import com.smarthome.observer.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Appliance Tests")
class ApplianceTest {

    private Appliance appliance;
    private TestObserver testObserver;

    @BeforeEach
    void setUp() {
        appliance = new Appliance.Builder()
                .id("TEST001")
                .type("AC")
                .name("Living Room AC")
                .brand("Samsung")
                .on(false)
                .online(false)
                .level(25)
                .powerUsage(0)
                .build();

        testObserver = new TestObserver();
    }

    @Nested
    @DisplayName("Constructor and Builder Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create appliance with builder pattern")
        void shouldCreateApplianceWithBuilder() {
            assertNotNull(appliance);
            assertEquals("TEST001", appliance.getId());
            assertEquals("AC", appliance.getType());
            assertEquals("Living Room AC", appliance.getName());
            assertEquals("Samsung", appliance.getBrand());
            assertFalse(appliance.isOn());
            assertFalse(appliance.isOnline());
            assertEquals(25, appliance.getLevel());
            assertEquals(0, appliance.getPowerUsage());
        }

        @Test
        @DisplayName("Should create appliance with default constructor")
        void shouldCreateApplianceWithDefaultConstructor() {
            Appliance defaultAppliance = new Appliance();
            assertNotNull(defaultAppliance);
            assertNull(defaultAppliance.getId());
            assertNull(defaultAppliance.getType());
            assertNull(defaultAppliance.getName());
            assertNull(defaultAppliance.getBrand());
        }

        @Test
        @DisplayName("Should create different appliance types")
        void shouldCreateDifferentApplianceTypes() {
            Appliance fan = new Appliance.Builder()
                    .id("FAN001")
                    .type("FAN")
                    .name("Ceiling Fan")
                    .brand("LG")
                    .level(3)
                    .build();

            Appliance speaker = new Appliance.Builder()
                    .id("SPEAKER001")
                    .type("SPEAKER")
                    .name("Bluetooth Speaker")
                    .brand("Sony")
                    .level(50)
                    .build();

            assertEquals("FAN", fan.getType());
            assertEquals("SPEAKER", speaker.getType());
        }

        @Test
        @DisplayName("Should create appliance with method chaining")
        void shouldCreateApplianceWithMethodChaining() {
            Appliance chainedAppliance = new Appliance.Builder()
                    .id("CHAIN001")
                    .type("AC")
                    .name("Chained AC")
                    .brand("Samsung")
                    .on(true)
                    .online(true)
                    .level(22)
                    .powerUsage(100)
                    .build();

            assertNotNull(chainedAppliance);
            assertEquals("CHAIN001", chainedAppliance.getId());
            assertEquals("AC", chainedAppliance.getType());
            assertEquals("Chained AC", chainedAppliance.getName());
            assertEquals("Samsung", chainedAppliance.getBrand());
            assertTrue(chainedAppliance.isOn());
            assertTrue(chainedAppliance.isOnline());
            assertEquals(22, chainedAppliance.getLevel());
            assertEquals(100, chainedAppliance.getPowerUsage());
        }

        @Test
        @DisplayName("Should create appliance with partial builder configuration")
        void shouldCreateApplianceWithPartialBuilderConfiguration() {
            Appliance partialAppliance = new Appliance.Builder()
                    .id("PARTIAL001")
                    .name("Partial Appliance")
                    .build();

            assertNotNull(partialAppliance);
            assertEquals("PARTIAL001", partialAppliance.getId());
            assertEquals("Partial Appliance", partialAppliance.getName());
            assertNull(partialAppliance.getType());
            assertNull(partialAppliance.getBrand());
            assertFalse(partialAppliance.isOn()); // Default value
            assertFalse(partialAppliance.isOnline()); // Default value
            assertEquals(0, partialAppliance.getLevel()); // Default value
            assertEquals(0, partialAppliance.getPowerUsage()); // Default value
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set id correctly")
        void shouldGetAndSetId() {
            appliance.setId("NEW001");
            assertEquals("NEW001", appliance.getId());
        }

        @Test
        @DisplayName("Should get and set type correctly")
        void shouldGetAndSetType() {
            appliance.setType("FAN");
            assertEquals("FAN", appliance.getType());
        }

        @Test
        @DisplayName("Should get and set name correctly")
        void shouldGetAndSetName() {
            appliance.setName("Bedroom AC");
            assertEquals("Bedroom AC", appliance.getName());
        }

        @Test
        @DisplayName("Should get and set brand correctly")
        void shouldGetAndSetBrand() {
            appliance.setBrand("LG");
            assertEquals("LG", appliance.getBrand());
        }

        @Test
        @DisplayName("Should get and set on status correctly")
        void shouldGetAndSetOnStatus() {
            appliance.setOn(true);
            assertTrue(appliance.isOn());
            
            appliance.setOn(false);
            assertFalse(appliance.isOn());
        }

        @Test
        @DisplayName("Should get and set online status correctly")
        void shouldGetAndSetOnlineStatus() {
            appliance.setOnline(true);
            assertTrue(appliance.isOnline());
            
            appliance.setOnline(false);
            assertFalse(appliance.isOnline());
        }

        @Test
        @DisplayName("Should get and set level correctly")
        void shouldGetAndSetLevel() {
            appliance.setLevel(30);
            assertEquals(30, appliance.getLevel());
        }

        @Test
        @DisplayName("Should get and set power usage correctly")
        void shouldGetAndSetPowerUsage() {
            appliance.setPowerUsage(500);
            assertEquals(500, appliance.getPowerUsage());
        }
    }

    @Nested
    @DisplayName("Device Interface Tests")
    class DeviceInterfaceTests {

        @Test
        @DisplayName("Should toggle device from off to on")
        void shouldToggleDeviceFromOffToOn() {
            appliance.setOn(false);
            appliance.setLevel(20);
            appliance.attach(testObserver);

            appliance.toggleOnOff();

            assertTrue(appliance.isOn());
            assertEquals(850, appliance.getPowerUsage()); // AC: 50 + (30-20)*80 = 850
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should toggle device from on to off")
        void shouldToggleDeviceFromOnToOff() {
            appliance.setOn(true);
            appliance.setPowerUsage(500);
            appliance.attach(testObserver);

            appliance.toggleOnOff();

            assertFalse(appliance.isOn());
            assertEquals(0, appliance.getPowerUsage());
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should simulate AC temperature correctly")
        void shouldSimulateACTemperatureCorrectly() {
            appliance.setType("AC");
            appliance.setOn(true);
            appliance.attach(testObserver);

            appliance.simulate(22);

            assertEquals(22, appliance.getLevel());
            assertEquals(690, appliance.getPowerUsage()); // 50 + (30-22)*80 = 690
            assertTrue(testObserver.wasNotified());
            assertTrue(testObserver.getLastMessage().contains("temperature set to 22°C"));
        }

        @Test
        @DisplayName("Should simulate Fan speed correctly")
        void shouldSimulateFanSpeedCorrectly() {
            appliance.setType("FAN");
            appliance.setOn(true);
            appliance.attach(testObserver);

            appliance.simulate(4);

            assertEquals(4, appliance.getLevel());
            assertEquals(45, appliance.getPowerUsage()); // Fixed value for speed 4
            assertTrue(testObserver.wasNotified());
            assertTrue(testObserver.getLastMessage().contains("speed set to level 4"));
        }

        @Test
        @DisplayName("Should simulate Speaker volume correctly")
        void shouldSimulateSpeakerVolumeCorrectly() {
            appliance.setType("SPEAKER");
            appliance.setOn(true);
            appliance.attach(testObserver);

            appliance.simulate(80);

            assertEquals(80, appliance.getLevel());
            assertEquals(45, appliance.getPowerUsage()); // 5 + (80 * 0.5) = 45
            assertTrue(testObserver.wasNotified());
            assertTrue(testObserver.getLastMessage().contains("volume set to 80%"));
        }

        @Test
        @DisplayName("Should not calculate power when device is off during simulate")
        void shouldNotCalculatePowerWhenDeviceIsOffDuringSimulate() {
            appliance.setOn(false);
            appliance.setType("AC");
            appliance.attach(testObserver);

            appliance.simulate(25);

            assertEquals(25, appliance.getLevel());
            assertEquals(0, appliance.getPowerUsage()); // Should remain 0 when off
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should handle unknown device type in simulate")
        void shouldHandleUnknownDeviceTypeInSimulate() {
            appliance.setType("UNKNOWN");
            appliance.setOn(true);
            appliance.attach(testObserver);

            appliance.simulate(50);

            assertEquals(50, appliance.getLevel());
            assertEquals(0, appliance.getPowerUsage()); // Unknown type returns 0
            assertTrue(testObserver.wasNotified());
            assertTrue(testObserver.getLastMessage().contains("level adjusted to 50"));
        }
    }

    @Nested
    @DisplayName("Observer Pattern Tests")
    class ObserverPatternTests {

        @Test
        @DisplayName("Should attach observer successfully")
        void shouldAttachObserverSuccessfully() {
            appliance.attach(testObserver);
            
            appliance.toggleOnOff();
            
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should detach observer successfully")
        void shouldDetachObserverSuccessfully() {
            appliance.attach(testObserver);
            appliance.detach(testObserver);
            
            appliance.toggleOnOff();
            
            assertFalse(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should notify multiple observers")
        void shouldNotifyMultipleObservers() {
            TestObserver observer1 = new TestObserver();
            TestObserver observer2 = new TestObserver();
            
            appliance.attach(observer1);
            appliance.attach(observer2);
            
            appliance.toggleOnOff();
            
            assertTrue(observer1.wasNotified());
            assertTrue(observer2.wasNotified());
        }

        @Test
        @DisplayName("Should handle null observer gracefully")
        void shouldHandleNullObserverGracefully() {
            assertDoesNotThrow(() -> {
                appliance.attach(null);
                appliance.detach(null);
                appliance.toggleOnOff();
            });
        }

        @Test
        @DisplayName("Should notify observers with correct message format")
        void shouldNotifyObserversWithCorrectMessageFormat() {
            appliance.attach(testObserver);

            // Test toggle message
            appliance.toggleOnOff();
            String toggleMessage = testObserver.getLastMessage();
            assertTrue(toggleMessage.contains(appliance.getName()));
            assertTrue(toggleMessage.contains("ON") || toggleMessage.contains("OFF"));

            // Test AC simulation message
            appliance.setType("AC");
            appliance.setOn(true);
            testObserver = new TestObserver(); // Reset
            appliance.attach(testObserver);
            
            appliance.simulate(25);
            String acMessage = testObserver.getLastMessage();
            assertTrue(acMessage.contains(appliance.getName()));
            assertTrue(acMessage.contains("temperature set to 25°C"));

            // Test Fan simulation message
            appliance.setType("FAN");
            testObserver = new TestObserver(); // Reset
            appliance.attach(testObserver);
            
            appliance.simulate(3);
            String fanMessage = testObserver.getLastMessage();
            assertTrue(fanMessage.contains(appliance.getName()));
            assertTrue(fanMessage.contains("speed set to level 3"));

            // Test Speaker simulation message
            appliance.setType("SPEAKER");
            testObserver = new TestObserver(); // Reset
            appliance.attach(testObserver);
            
            appliance.simulate(75);
            String speakerMessage = testObserver.getLastMessage();
            assertTrue(speakerMessage.contains(appliance.getName()));
            assertTrue(speakerMessage.contains("volume set to 75%"));
        }
    }

    @Nested
    @DisplayName("Power Calculation Tests")
    class PowerCalculationTests {

        @Test
        @DisplayName("Should calculate AC power correctly for different temperatures")
        void shouldCalculateACPowerCorrectlyForDifferentTemperatures() {
            appliance.setType("AC");
            appliance.setOn(true);

            // Test minimum temperature (17°C)
            appliance.simulate(17);
            assertEquals(1090, appliance.getPowerUsage()); // 50 + (30-17)*80 = 1090

            // Test maximum temperature (30°C)
            appliance.simulate(30);
            assertEquals(50, appliance.getPowerUsage()); // 50 + (30-30)*80 = 50
        }

        @Test
        @DisplayName("Should calculate Fan power correctly for different speeds")
        void shouldCalculateFanPowerCorrectlyForDifferentSpeeds() {
            appliance.setType("FAN");
            appliance.setOn(true);

            // Test minimum speed (1)
            appliance.simulate(1);
            assertEquals(15, appliance.getPowerUsage()); // Fixed value for speed 1

            // Test maximum speed (5)
            appliance.simulate(5);
            assertEquals(55, appliance.getPowerUsage()); // Fixed value for speed 5
        }

        @Test
        @DisplayName("Should calculate Speaker power correctly for different volumes")
        void shouldCalculateSpeakerPowerCorrectlyForDifferentVolumes() {
            appliance.setType("SPEAKER");
            appliance.setOn(true);

            // Test minimum volume (1)
            appliance.simulate(1);
            assertEquals(5, appliance.getPowerUsage()); // 5 + (1 * 0.5) = 5

            // Test maximum volume (100)
            appliance.simulate(100);
            assertEquals(55, appliance.getPowerUsage()); // 5 + (100 * 0.5) = 55
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCasesAndBoundaryTests {

        @Test
        @DisplayName("Should handle power calculation with extreme temperature values")
        void shouldHandlePowerCalculationWithExtremeTemperatureValues() {
            appliance.setType("AC");
            appliance.setOn(true);

            // Test temperature below minimum (15°C) - should still calculate properly
            appliance.simulate(15);
            assertEquals(15, appliance.getLevel());
            assertEquals(1250, appliance.getPowerUsage()); // 50 + (30-15)*80 = 1250

            // Test temperature above maximum (35°C) - Math.max should prevent negative
            appliance.simulate(35);
            assertEquals(35, appliance.getLevel());
            assertEquals(50, appliance.getPowerUsage()); // 50 + Math.max(0, (30-35)*80) = 50
        }

        @Test
        @DisplayName("Should handle fan speed boundaries correctly")
        void shouldHandleFanSpeedBoundariesCorrectly() {
            appliance.setType("FAN");
            appliance.setOn(true);

            // Test all valid fan speeds
            int[] speeds = {1, 2, 3, 4, 5};
            int[] expectedPower = {15, 25, 35, 45, 55};

            for (int i = 0; i < speeds.length; i++) {
                appliance.simulate(speeds[i]);
                assertEquals(speeds[i], appliance.getLevel());
                assertEquals(expectedPower[i], appliance.getPowerUsage());
            }

            // Test invalid speed (should return 0)
            appliance.simulate(10);
            assertEquals(10, appliance.getLevel());
            assertEquals(0, appliance.getPowerUsage()); // Invalid speed returns 0
        }

        @Test
        @DisplayName("Should handle speaker volume boundaries correctly")
        void shouldHandleSpeakerVolumeBoundariesCorrectly() {
            appliance.setType("SPEAKER");
            appliance.setOn(true);

            // Test volume 0
            appliance.simulate(0);
            assertEquals(0, appliance.getLevel());
            assertEquals(5, appliance.getPowerUsage()); // 5 + (0 * 0.5) = 5

            // Test very high volume
            appliance.simulate(200);
            assertEquals(200, appliance.getLevel());
            assertEquals(105, appliance.getPowerUsage()); // 5 + (200 * 0.5) = 105
        }

        @Test
        @DisplayName("Should handle null and empty string values gracefully")
        void shouldHandleNullAndEmptyStringValuesGracefully() {
            Appliance testAppliance = new Appliance();

            // Test setting null values
            assertDoesNotThrow(() -> {
                testAppliance.setId(null);
                testAppliance.setName(null);
                testAppliance.setBrand(null);
                testAppliance.setType(null);
            });

            // Test getting null values
            assertNull(testAppliance.getId());
            assertNull(testAppliance.getName());
            assertNull(testAppliance.getBrand());
            assertNull(testAppliance.getType());

            // Test empty strings
            assertDoesNotThrow(() -> {
                testAppliance.setId("");
                testAppliance.setName("");
                testAppliance.setBrand("");
                testAppliance.setType("");
            });
        }

    }

    @Nested
    @DisplayName("Thread Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("Should handle concurrent access to on status")
        void shouldHandleConcurrentAccessToOnStatus() throws InterruptedException {
            Thread[] threads = new Thread[10];
            
            for (int i = 0; i < 10; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 100; j++) {
                        appliance.setOn(!appliance.isOn());
                    }
                });
            }
            
            for (Thread thread : threads) {
                thread.start();
            }
            
            for (Thread thread : threads) {
                thread.join();
            }
            
            // Test should complete without throwing any exceptions
            assertNotNull(appliance);
        }

        @Test
        @DisplayName("Should handle concurrent toggleOnOff operations")
        void shouldHandleConcurrentToggleOnOffOperations() throws InterruptedException {
            Thread[] threads = new Thread[5];
            
            for (int i = 0; i < 5; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 10; j++) {
                        appliance.toggleOnOff();
                    }
                });
            }
            
            for (Thread thread : threads) {
                thread.start();
            }
            
            for (Thread thread : threads) {
                thread.join();
            }
            
            // Test should complete without throwing any exceptions
            assertNotNull(appliance);
        }
    }

    @Test
    @DisplayName("Should generate correct toString representation")
    void shouldGenerateCorrectToStringRepresentation() {
        String toStringResult = appliance.toString();
        
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("TEST001"));
        assertTrue(toStringResult.contains("Living Room AC"));
        assertTrue(toStringResult.contains("Samsung"));
        assertTrue(toStringResult.contains("AC"));
        assertTrue(toStringResult.contains("online=false"));
        assertTrue(toStringResult.contains("on=false"));
        assertTrue(toStringResult.contains("level=25"));
        assertTrue(toStringResult.contains("powerUsage=0"));
    }

    // Test Observer implementation for testing
    private static class TestObserver implements Observer {
        private boolean notified = false;
        private String lastMessage = "";

        @Override
        public void update(String message) {
            this.notified = true;
            this.lastMessage = message;
        }

        public boolean wasNotified() {
            return notified;
        }

        public String getLastMessage() {
            return lastMessage;
        }
    }
}