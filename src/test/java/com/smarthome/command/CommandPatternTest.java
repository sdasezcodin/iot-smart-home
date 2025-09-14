package com.smarthome.command;

import com.smarthome.model.Appliance;
import com.smarthome.observer.Observer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Command Pattern Tests")
class CommandPatternTest {

    private Appliance testAppliance;
    private TestObserver testObserver;

    @BeforeEach
    void setUp() {
        testAppliance = new Appliance.Builder()
                .id("TEST_APPLIANCE")
                .type("AC")
                .name("Test AC")
                .brand("Test Brand")
                .level(25)
                .on(false)
                .online(false)
                .powerUsage(0)
                .build();

        testObserver = new TestObserver();
        testAppliance.attach(testObserver);
    }

    @Nested
    @DisplayName("Toggle Command Tests")
    class ToggleCommandTests {

        @Test
        @DisplayName("Should create Toggle command with appliance")
        void shouldCreateToggleCommandWithAppliance() {
            Toggle toggleCommand = new Toggle(testAppliance);
            assertNotNull(toggleCommand);
        }

        @Test
        @DisplayName("Should implement Command interface")
        void shouldImplementCommandInterface() {
            Toggle toggleCommand = new Toggle(testAppliance);
            assertTrue(toggleCommand instanceof Command);
        }

        @Test
        @DisplayName("Should execute toggle command successfully")
        void shouldExecuteToggleCommandSuccessfully() {
            Toggle toggleCommand = new Toggle(testAppliance);
            
            // Initially off
            assertFalse(testAppliance.isOn());
            
            // Execute toggle command
            toggleCommand.execute();
            
            // Should now be on
            assertTrue(testAppliance.isOn());
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should toggle appliance from on to off")
        void shouldToggleApplianceFromOnToOff() {
            // Set appliance to on state
            testAppliance.setOn(true);
            testAppliance.setPowerUsage(500);
            testObserver.reset();
            
            Toggle toggleCommand = new Toggle(testAppliance);
            
            assertTrue(testAppliance.isOn());
            assertEquals(500, testAppliance.getPowerUsage());
            
            // Execute toggle command
            toggleCommand.execute();
            
            // Should now be off
            assertFalse(testAppliance.isOn());
            assertEquals(0, testAppliance.getPowerUsage());
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should toggle appliance from off to on")
        void shouldToggleApplianceFromOffToOn() {
            Toggle toggleCommand = new Toggle(testAppliance);
            
            assertFalse(testAppliance.isOn());
            assertEquals(0, testAppliance.getPowerUsage());
            
            // Execute toggle command
            toggleCommand.execute();
            
            // Should now be on with calculated power usage
            assertTrue(testAppliance.isOn());
            assertTrue(testAppliance.getPowerUsage() > 0); // AC should calculate power
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should work with multiple toggle executions")
        void shouldWorkWithMultipleToggleExecutions() {
            Toggle toggleCommand = new Toggle(testAppliance);
            
            // Start: OFF
            assertFalse(testAppliance.isOn());
            
            // First toggle: ON
            toggleCommand.execute();
            assertTrue(testAppliance.isOn());
            
            // Second toggle: OFF
            testObserver.reset();
            toggleCommand.execute();
            assertFalse(testAppliance.isOn());
            assertTrue(testObserver.wasNotified());
            
            // Third toggle: ON
            testObserver.reset();
            toggleCommand.execute();
            assertTrue(testAppliance.isOn());
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should handle null appliance gracefully")
        void shouldHandleNullApplianceGracefully() {
            Toggle toggleCommand = new Toggle(null);
            
            // Should throw exception when trying to execute with null appliance
            assertThrows(NullPointerException.class, () -> {
                toggleCommand.execute();
            });
        }
    }

    @Nested
    @DisplayName("Simulate Command Tests")
    class SimulateCommandTests {

        @Test
        @DisplayName("Should create Simulate command with appliance and level")
        void shouldCreateSimulateCommandWithApplianceAndLevel() {
            Simulate simulateCommand = new Simulate(testAppliance, 30);
            assertNotNull(simulateCommand);
        }

        @Test
        @DisplayName("Should implement Command interface")
        void shouldImplementCommandInterface() {
            Simulate simulateCommand = new Simulate(testAppliance, 30);
            assertTrue(simulateCommand instanceof Command);
        }

        @Test
        @DisplayName("Should execute simulate command successfully")
        void shouldExecuteSimulateCommandSuccessfully() {
            Simulate simulateCommand = new Simulate(testAppliance, 28);
            
            assertEquals(25, testAppliance.getLevel()); // Initial level
            
            // Execute simulate command
            simulateCommand.execute();
            
            assertEquals(28, testAppliance.getLevel()); // Should be updated
            assertTrue(testObserver.wasNotified());
        }


        @Test
        @DisplayName("Should work with different level values")
        void shouldWorkWithDifferentLevelValues() {
            testAppliance.setOn(true);
            
            int[] testLevels = {1, 15, 50, 75, 100};
            
            for (int level : testLevels) {
                testObserver.reset();
                Simulate simulateCommand = new Simulate(testAppliance, level);
                simulateCommand.execute();
                
                assertEquals(level, testAppliance.getLevel());
                assertTrue(testObserver.wasNotified());
            }
        }

        @Test
        @DisplayName("Should work when appliance is off")
        void shouldWorkWhenApplianceIsOff() {
            assertFalse(testAppliance.isOn());
            
            Simulate simulateCommand = new Simulate(testAppliance, 30);
            simulateCommand.execute();
            
            assertEquals(30, testAppliance.getLevel()); // Level should be updated
            assertEquals(0, testAppliance.getPowerUsage()); // But no power usage when off
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should handle negative level values")
        void shouldHandleNegativeLevelValues() {
            Simulate simulateCommand = new Simulate(testAppliance, -10);
            
            assertDoesNotThrow(() -> {
                simulateCommand.execute();
            });
            
            assertEquals(-10, testAppliance.getLevel());
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should handle zero level value")
        void shouldHandleZeroLevelValue() {
            Simulate simulateCommand = new Simulate(testAppliance, 0);
            simulateCommand.execute();
            
            assertEquals(0, testAppliance.getLevel());
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should handle very large level values")
        void shouldHandleVeryLargeLevelValues() {
            Simulate simulateCommand = new Simulate(testAppliance, Integer.MAX_VALUE);
            
            assertDoesNotThrow(() -> {
                simulateCommand.execute();
            });
            
            assertEquals(Integer.MAX_VALUE, testAppliance.getLevel());
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should handle null appliance gracefully")
        void shouldHandleNullApplianceGracefully() {
            Simulate simulateCommand = new Simulate(null, 25);
            
            assertThrows(NullPointerException.class, () -> {
                simulateCommand.execute();
            });
        }
    }

    @Nested
    @DisplayName("Remote Control Tests")
    class RemoteControlTests {

        private Remote remote;

        @BeforeEach
        void setUp() {
            remote = new Remote();
        }

        @Test
        @DisplayName("Should create remote control")
        void shouldCreateRemoteControl() {
            assertNotNull(remote);
        }

        @Test
        @DisplayName("Should set and execute Toggle command")
        void shouldSetAndExecuteToggleCommand() {
            Toggle toggleCommand = new Toggle(testAppliance);
            remote.setCommand(toggleCommand);
            
            assertFalse(testAppliance.isOn());
            
            remote.pressButton();
            
            assertTrue(testAppliance.isOn());
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should set and execute Simulate command")
        void shouldSetAndExecuteSimulateCommand() {
            Simulate simulateCommand = new Simulate(testAppliance, 35);
            remote.setCommand(simulateCommand);
            
            assertEquals(25, testAppliance.getLevel());
            
            remote.pressButton();
            
            assertEquals(35, testAppliance.getLevel());
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should execute different commands sequentially")
        void shouldExecuteDifferentCommandsSequentially() {
            // First command: Toggle ON
            Toggle toggleCommand = new Toggle(testAppliance);
            remote.setCommand(toggleCommand);
            remote.pressButton();
            
            assertTrue(testAppliance.isOn());
            testObserver.reset();
            
            // Second command: Simulate
            Simulate simulateCommand = new Simulate(testAppliance, 28);
            remote.setCommand(simulateCommand);
            remote.pressButton();
            
            assertEquals(28, testAppliance.getLevel());
            assertTrue(testObserver.wasNotified());
            testObserver.reset();
            
            // Third command: Toggle OFF
            remote.setCommand(toggleCommand);
            remote.pressButton();
            
            assertFalse(testAppliance.isOn());
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should handle multiple button presses with same command")
        void shouldHandleMultipleButtonPressesWithSameCommand() {
            Toggle toggleCommand = new Toggle(testAppliance);
            remote.setCommand(toggleCommand);
            
            // Multiple presses should toggle state each time
            assertFalse(testAppliance.isOn());
            
            remote.pressButton(); // OFF -> ON
            assertTrue(testAppliance.isOn());
            
            remote.pressButton(); // ON -> OFF
            assertFalse(testAppliance.isOn());
            
            remote.pressButton(); // OFF -> ON
            assertTrue(testAppliance.isOn());
        }

        @Test
        @DisplayName("Should handle null command gracefully")
        void shouldHandleNullCommandGracefully() {
            remote.setCommand(null);
            
            // Should not throw exception when pressing button with null command
            assertDoesNotThrow(() -> {
                remote.pressButton();
            });
            
            // Appliance state should remain unchanged
            assertFalse(testAppliance.isOn());
            assertEquals(25, testAppliance.getLevel());
        }

        @Test
        @DisplayName("Should handle pressing button without setting command")
        void shouldHandlePressButtonWithoutSettingCommand() {
            // Remote starts with no command set
            assertDoesNotThrow(() -> {
                remote.pressButton();
            });
            
            // Appliance state should remain unchanged
            assertFalse(testAppliance.isOn());
            assertEquals(25, testAppliance.getLevel());
        }

        @Test
        @DisplayName("Should allow command replacement")
        void shouldAllowCommandReplacement() {
            // Set initial command
            Toggle toggleCommand = new Toggle(testAppliance);
            remote.setCommand(toggleCommand);
            remote.pressButton();
            assertTrue(testAppliance.isOn());
            testObserver.reset();
            
            // Replace with different command
            Simulate simulateCommand = new Simulate(testAppliance, 30);
            remote.setCommand(simulateCommand);
            remote.pressButton();
            
            // Should execute new command, not the old one
            assertEquals(30, testAppliance.getLevel());
            assertTrue(testObserver.wasNotified());
            // State should still be on (simulate doesn't toggle)
            assertTrue(testAppliance.isOn());
        }
    }

    @Nested
    @DisplayName("Command Pattern Integration Tests")
    class CommandPatternIntegrationTests {

        @Test
        @DisplayName("Should support complex command sequences")
        void shouldSupportComplexCommandSequences() {
            Remote remote = new Remote();
            
            // Sequence: Toggle ON -> Simulate -> Toggle OFF -> Toggle ON -> Simulate
            Command[] sequence = {
                new Toggle(testAppliance),      // Turn ON
                new Simulate(testAppliance, 30), // Set level to 30
                new Toggle(testAppliance),      // Turn OFF
                new Toggle(testAppliance),      // Turn ON again
                new Simulate(testAppliance, 20)  // Set level to 20
            };
            
            // Execute sequence
            for (Command command : sequence) {
                remote.setCommand(command);
                remote.pressButton();
            }
            
            // Final state should be: ON, level 20
            assertTrue(testAppliance.isOn());
            assertEquals(20, testAppliance.getLevel());
            assertTrue(testObserver.wasNotified());
        }

        @Test
        @DisplayName("Should work with multiple appliances")
        void shouldWorkWithMultipleAppliances() {
            // Create additional appliances
            Appliance fan = new Appliance.Builder()
                    .id("FAN001")
                    .type("FAN")
                    .name("Test Fan")
                    .brand("Test")
                    .level(3)
                    .build();

            Appliance speaker = new Appliance.Builder()
                    .id("SPEAKER001")
                    .type("SPEAKER")
                    .name("Test Speaker")
                    .brand("Test")
                    .level(50)
                    .build();

            TestObserver fanObserver = new TestObserver();
            TestObserver speakerObserver = new TestObserver();
            fan.attach(fanObserver);
            speaker.attach(speakerObserver);

            Remote remote = new Remote();

            // Control AC
            remote.setCommand(new Toggle(testAppliance));
            remote.pressButton();
            assertTrue(testAppliance.isOn());

            // Control Fan
            remote.setCommand(new Toggle(fan));
            remote.pressButton();
            assertTrue(fan.isOn());

            // Control Speaker
            remote.setCommand(new Simulate(speaker, 75));
            remote.pressButton();
            assertEquals(75, speaker.getLevel());

            // All observers should have been notified
            assertTrue(testObserver.wasNotified());
            assertTrue(fanObserver.wasNotified());
            assertTrue(speakerObserver.wasNotified());
        }

        @Test
        @DisplayName("Should support polymorphic command usage")
        void shouldSupportPolymorphicCommandUsage() {
            Remote remote = new Remote();
            
            // Store commands in array as Command interface
            Command[] commands = {
                new Toggle(testAppliance),
                new Simulate(testAppliance, 25)
            };
            
            // Execute all commands polymorphically
            for (Command command : commands) {
                remote.setCommand(command);
                remote.pressButton();
            }
            
            assertTrue(testAppliance.isOn());
            assertEquals(25, testAppliance.getLevel());
        }

        @Test
        @DisplayName("Should maintain command immutability")
        void shouldMaintainCommandImmutability() {
            Toggle toggle1 = new Toggle(testAppliance);
            Toggle toggle2 = new Toggle(testAppliance);
            Simulate simulate1 = new Simulate(testAppliance, 30);
            Simulate simulate2 = new Simulate(testAppliance, 30);
            
            // Commands with same parameters should be different instances
            assertNotSame(toggle1, toggle2);
            assertNotSame(simulate1, simulate2);
            
            // But should produce same effects
            Remote remote = new Remote();
            
            remote.setCommand(toggle1);
            remote.pressButton();
            boolean stateAfterToggle1 = testAppliance.isOn();
            
            testAppliance.setOn(false); // Reset
            
            remote.setCommand(toggle2);
            remote.pressButton();
            boolean stateAfterToggle2 = testAppliance.isOn();
            
            assertEquals(stateAfterToggle1, stateAfterToggle2);
        }
    }

    @Nested
    @DisplayName("Command Pattern Edge Cases")
    class CommandPatternEdgeCasesTests {

        @Test
        @DisplayName("Should handle rapid command execution")
        void shouldHandleRapidCommandExecution() {
            Remote remote = new Remote();
            Toggle toggleCommand = new Toggle(testAppliance);
            remote.setCommand(toggleCommand);
            
            // Execute many commands rapidly
            for (int i = 0; i < 1000; i++) {
                remote.pressButton();
            }
            
            // Final state should be consistent (even number of toggles = OFF)
            assertFalse(testAppliance.isOn());
        }

        @Test
        @DisplayName("Should handle concurrent command execution")
        void shouldHandleConcurrentCommandExecution() throws InterruptedException {
            Remote remote = new Remote();
            Toggle toggleCommand = new Toggle(testAppliance);
            
            Thread[] threads = new Thread[10];
            
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < 10; j++) {
                        remote.setCommand(toggleCommand);
                        remote.pressButton();
                    }
                });
            }
            
            for (Thread thread : threads) {
                thread.start();
            }
            
            for (Thread thread : threads) {
                thread.join();
            }
            
            // Should complete without exceptions
            assertNotNull(testAppliance);
        }

        @Test
        @DisplayName("Should handle extreme simulation values")
        void shouldHandleExtremeSimulationValues() {
            Remote remote = new Remote();
            
            int[] extremeValues = {
                Integer.MIN_VALUE, -1000000, -1, 0, 1, 
                1000000, Integer.MAX_VALUE
            };
            
            for (int value : extremeValues) {
                Simulate simulateCommand = new Simulate(testAppliance, value);
                remote.setCommand(simulateCommand);
                
                assertDoesNotThrow(() -> {
                    remote.pressButton();
                });
                
                assertEquals(value, testAppliance.getLevel());
            }
        }
    }

    // Test Observer implementation
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

        public void reset() {
            this.notified = false;
            this.lastMessage = "";
        }
    }
}