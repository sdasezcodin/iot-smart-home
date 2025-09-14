package com.smarthome.factory;

import com.smarthome.model.Appliance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Appliance Factory Tests")
class ApplianceFactoryTest {

    @Nested
    @DisplayName("HaierFactory Tests")
    class HaierFactoryTests {
        
        private HaierFactory haierFactory;

        @BeforeEach
        void setUp() {
            haierFactory = new HaierFactory();
        }

        @Test
        @DisplayName("Should create AC builder with Haier defaults")
        void shouldCreateACBuilderWithHaierDefaults() {
            Appliance.Builder builder = haierFactory.acBuilder();
            assertNotNull(builder);

            Appliance ac = builder.id("TEST_AC").name("Test AC").build();
            
            assertEquals("AC", ac.getType());
            assertEquals("Haier", ac.getBrand());
            assertEquals(24, ac.getLevel()); // Default AC temperature
            assertEquals("TEST_AC", ac.getId());
            assertEquals("Test AC", ac.getName());
            assertFalse(ac.isOn()); // Default state
            assertFalse(ac.isOnline()); // Default state
        }

        @Test
        @DisplayName("Should create Fan builder with Haier defaults")
        void shouldCreateFanBuilderWithHaierDefaults() {
            Appliance.Builder builder = haierFactory.fanBuilder();
            assertNotNull(builder);

            Appliance fan = builder.id("TEST_FAN").name("Test Fan").build();
            
            assertEquals("Fan", fan.getType());
            assertEquals("Haier", fan.getBrand());
            assertEquals(3, fan.getLevel()); // Default fan speed
            assertEquals("TEST_FAN", fan.getId());
            assertEquals("Test Fan", fan.getName());
            assertFalse(fan.isOn());
            assertFalse(fan.isOnline());
        }

        @Test
        @DisplayName("Should create Speaker builder with Haier defaults")
        void shouldCreateSpeakerBuilderWithHaierDefaults() {
            Appliance.Builder builder = haierFactory.speakerBuilder();
            assertNotNull(builder);

            Appliance speaker = builder.id("TEST_SPEAKER").name("Test Speaker").build();
            
            assertEquals("Speaker", speaker.getType());
            assertEquals("Haier", speaker.getBrand());
            assertEquals(10, speaker.getLevel()); // Default speaker volume
            assertEquals("TEST_SPEAKER", speaker.getId());
            assertEquals("Test Speaker", speaker.getName());
            assertFalse(speaker.isOn());
            assertFalse(speaker.isOnline());
        }

        @Test
        @DisplayName("Should allow customization of builder properties")
        void shouldAllowCustomizationOfBuilderProperties() {
            Appliance customAC = haierFactory.acBuilder()
                    .id("CUSTOM_AC")
                    .name("Custom AC")
                    .level(22)
                    .on(true)
                    .online(true)
                    .powerUsage(500)
                    .build();
            
            assertEquals("AC", customAC.getType());
            assertEquals("Haier", customAC.getBrand());
            assertEquals(22, customAC.getLevel()); // Overridden
            assertEquals("CUSTOM_AC", customAC.getId());
            assertEquals("Custom AC", customAC.getName());
            assertTrue(customAC.isOn()); // Overridden
            assertTrue(customAC.isOnline()); // Overridden
            assertEquals(500, customAC.getPowerUsage()); // Custom
        }

        @Test
        @DisplayName("Should create multiple different appliances")
        void shouldCreateMultipleDifferentAppliances() {
            Appliance ac = haierFactory.acBuilder().id("AC1").name("Living Room AC").build();
            Appliance fan = haierFactory.fanBuilder().id("FAN1").name("Bedroom Fan").build();
            Appliance speaker = haierFactory.speakerBuilder().id("SPK1").name("Kitchen Speaker").build();

            // All should be Haier brand
            assertEquals("Haier", ac.getBrand());
            assertEquals("Haier", fan.getBrand());
            assertEquals("Haier", speaker.getBrand());

            // But different types
            assertEquals("AC", ac.getType());
            assertEquals("Fan", fan.getType());
            assertEquals("Speaker", speaker.getType());

            // And different default levels
            assertEquals(24, ac.getLevel());
            assertEquals(3, fan.getLevel());
            assertEquals(10, speaker.getLevel());
        }

        @Test
        @DisplayName("Should implement ApplianceFactory interface")
        void shouldImplementApplianceFactoryInterface() {
            assertTrue(haierFactory instanceof ApplianceFactory);
            
            // Should be able to use as ApplianceFactory
            ApplianceFactory factory = haierFactory;
            assertNotNull(factory.acBuilder());
            assertNotNull(factory.fanBuilder());
            assertNotNull(factory.speakerBuilder());
        }
    }

    @Nested
    @DisplayName("LGFactory Tests")
    class LGFactoryTests {
        
        private LGFactory lgFactory;

        @BeforeEach
        void setUp() {
            lgFactory = new LGFactory();
        }

        @Test
        @DisplayName("Should create AC builder with LG defaults")
        void shouldCreateACBuilderWithLGDefaults() {
            Appliance.Builder builder = lgFactory.acBuilder();
            assertNotNull(builder);

            Appliance ac = builder.id("TEST_AC").name("Test AC").build();
            
            assertEquals("AC", ac.getType());
            assertEquals("LG", ac.getBrand());
            assertEquals(24, ac.getLevel());
            assertEquals("TEST_AC", ac.getId());
            assertEquals("Test AC", ac.getName());
            assertFalse(ac.isOn());
            assertFalse(ac.isOnline());
        }

        @Test
        @DisplayName("Should create Fan builder with LG defaults")
        void shouldCreateFanBuilderWithLGDefaults() {
            Appliance.Builder builder = lgFactory.fanBuilder();
            assertNotNull(builder);

            Appliance fan = builder.id("TEST_FAN").name("Test Fan").build();
            
            assertEquals("Fan", fan.getType());
            assertEquals("LG", fan.getBrand());
            assertEquals(3, fan.getLevel());
            assertEquals("TEST_FAN", fan.getId());
            assertEquals("Test Fan", fan.getName());
            assertFalse(fan.isOn());
            assertFalse(fan.isOnline());
        }

        @Test
        @DisplayName("Should create Speaker builder with LG defaults")
        void shouldCreateSpeakerBuilderWithLGDefaults() {
            Appliance.Builder builder = lgFactory.speakerBuilder();
            assertNotNull(builder);

            Appliance speaker = builder.id("TEST_SPEAKER").name("Test Speaker").build();
            
            assertEquals("Speaker", speaker.getType());
            assertEquals("LG", speaker.getBrand());
            assertEquals(10, speaker.getLevel());
            assertEquals("TEST_SPEAKER", speaker.getId());
            assertEquals("Test Speaker", speaker.getName());
            assertFalse(speaker.isOn());
            assertFalse(speaker.isOnline());
        }

        @Test
        @DisplayName("Should implement ApplianceFactory interface")
        void shouldImplementApplianceFactoryInterface() {
            assertTrue(lgFactory instanceof ApplianceFactory);
        }
    }

    @Nested
    @DisplayName("SonyFactory Tests")
    class SonyFactoryTests {
        
        private SonyFactory sonyFactory;

        @BeforeEach
        void setUp() {
            sonyFactory = new SonyFactory();
        }

        @Test
        @DisplayName("Should create AC builder with Sony defaults")
        void shouldCreateACBuilderWithSonyDefaults() {
            Appliance.Builder builder = sonyFactory.acBuilder();
            assertNotNull(builder);

            Appliance ac = builder.id("TEST_AC").name("Test AC").build();
            
            assertEquals("AC", ac.getType());
            assertEquals("Sony", ac.getBrand());
            assertEquals(24, ac.getLevel());
            assertEquals("TEST_AC", ac.getId());
            assertEquals("Test AC", ac.getName());
            assertFalse(ac.isOn());
            assertFalse(ac.isOnline());
        }

        @Test
        @DisplayName("Should create Fan builder with Sony defaults")
        void shouldCreateFanBuilderWithSonyDefaults() {
            Appliance.Builder builder = sonyFactory.fanBuilder();
            assertNotNull(builder);

            Appliance fan = builder.id("TEST_FAN").name("Test Fan").build();
            
            assertEquals("Fan", fan.getType());
            assertEquals("Sony", fan.getBrand());
            assertEquals(3, fan.getLevel());
            assertEquals("TEST_FAN", fan.getId());
            assertEquals("Test Fan", fan.getName());
            assertFalse(fan.isOn());
            assertFalse(fan.isOnline());
        }

        @Test
        @DisplayName("Should create Speaker builder with Sony defaults")
        void shouldCreateSpeakerBuilderWithSonyDefaults() {
            Appliance.Builder builder = sonyFactory.speakerBuilder();
            assertNotNull(builder);

            Appliance speaker = builder.id("TEST_SPEAKER").name("Test Speaker").build();
            
            assertEquals("Speaker", speaker.getType());
            assertEquals("Sony", speaker.getBrand());
            assertEquals(10, speaker.getLevel());
            assertEquals("TEST_SPEAKER", speaker.getId());
            assertEquals("Test Speaker", speaker.getName());
            assertFalse(speaker.isOn());
            assertFalse(speaker.isOnline());
        }

        @Test
        @DisplayName("Should implement ApplianceFactory interface")
        void shouldImplementApplianceFactoryInterface() {
            assertTrue(sonyFactory instanceof ApplianceFactory);
        }
    }

    @Nested
    @DisplayName("Factory Comparison Tests")
    class FactoryComparisonTests {

        private HaierFactory haierFactory;
        private LGFactory lgFactory;
        private SonyFactory sonyFactory;

        @BeforeEach
        void setUp() {
            haierFactory = new HaierFactory();
            lgFactory = new LGFactory();
            sonyFactory = new SonyFactory();
        }

        @Test
        @DisplayName("Should create appliances with different brands")
        void shouldCreateAppliancesWithDifferentBrands() {
            Appliance haierAC = haierFactory.acBuilder().id("H_AC").name("Haier AC").build();
            Appliance lgAC = lgFactory.acBuilder().id("L_AC").name("LG AC").build();
            Appliance sonyAC = sonyFactory.acBuilder().id("S_AC").name("Sony AC").build();

            assertEquals("Haier", haierAC.getBrand());
            assertEquals("LG", lgAC.getBrand());
            assertEquals("Sony", sonyAC.getBrand());

            // Same type and level defaults
            assertEquals("AC", haierAC.getType());
            assertEquals("AC", lgAC.getType());
            assertEquals("AC", sonyAC.getType());

            assertEquals(24, haierAC.getLevel());
            assertEquals(24, lgAC.getLevel());
            assertEquals(24, sonyAC.getLevel());
        }

        @Test
        @DisplayName("Should have consistent default values across brands for same appliance type")
        void shouldHaveConsistentDefaultValuesAcrossBrandsForSameApplianceType() {
            // AC defaults
            Appliance haierAC = haierFactory.acBuilder().id("1").build();
            Appliance lgAC = lgFactory.acBuilder().id("2").build();
            Appliance sonyAC = sonyFactory.acBuilder().id("3").build();

            assertEquals(haierAC.getLevel(), lgAC.getLevel());
            assertEquals(lgAC.getLevel(), sonyAC.getLevel());
            assertEquals(24, haierAC.getLevel());

            // Fan defaults
            Appliance haierFan = haierFactory.fanBuilder().id("4").build();
            Appliance lgFan = lgFactory.fanBuilder().id("5").build();
            Appliance sonyFan = sonyFactory.fanBuilder().id("6").build();

            assertEquals(haierFan.getLevel(), lgFan.getLevel());
            assertEquals(lgFan.getLevel(), sonyFan.getLevel());
            assertEquals(3, haierFan.getLevel());

            // Speaker defaults
            Appliance haierSpeaker = haierFactory.speakerBuilder().id("7").build();
            Appliance lgSpeaker = lgFactory.speakerBuilder().id("8").build();
            Appliance sonySpeaker = sonyFactory.speakerBuilder().id("9").build();

            assertEquals(haierSpeaker.getLevel(), lgSpeaker.getLevel());
            assertEquals(lgSpeaker.getLevel(), sonySpeaker.getLevel());
            assertEquals(10, haierSpeaker.getLevel());
        }

        @Test
        @DisplayName("Should create functionally equivalent appliances across different factories")
        void shouldCreateFunctionallyEquivalentAppliancesAcrossDifferentFactories() {
            Appliance[] fans = {
                haierFactory.fanBuilder().id("FAN1").name("Test Fan").build(),
                lgFactory.fanBuilder().id("FAN2").name("Test Fan").build(),
                sonyFactory.fanBuilder().id("FAN3").name("Test Fan").build()
            };

            // All should have same functional properties except brand
            for (Appliance fan : fans) {
                assertEquals("Fan", fan.getType());
                assertEquals("Test Fan", fan.getName());
                assertEquals(3, fan.getLevel());
                assertFalse(fan.isOn());
                assertFalse(fan.isOnline());
                assertEquals(0, fan.getPowerUsage()); // Default
            }

            // Only brands should be different
            assertEquals("Haier", fans[0].getBrand());
            assertEquals("LG", fans[1].getBrand());
            assertEquals("Sony", fans[2].getBrand());
        }
    }

    @Nested
    @DisplayName("Factory Pattern Validation Tests")
    class FactoryPatternValidationTests {

        @Test
        @DisplayName("Should support polymorphic factory usage")
        void shouldSupportPolymorphicFactoryUsage() {
            ApplianceFactory[] factories = {
                new HaierFactory(),
                new LGFactory(),
                new SonyFactory()
            };

            String[] expectedBrands = {"Haier", "LG", "Sony"};

            for (int i = 0; i < factories.length; i++) {
                ApplianceFactory factory = factories[i];
                String expectedBrand = expectedBrands[i];

                // Test AC creation
                Appliance ac = factory.acBuilder().id("AC" + i).build();
                assertEquals(expectedBrand, ac.getBrand());
                assertEquals("AC", ac.getType());

                // Test Fan creation
                Appliance fan = factory.fanBuilder().id("FAN" + i).build();
                assertEquals(expectedBrand, fan.getBrand());
                assertEquals("Fan", fan.getType());

                // Test Speaker creation
                Appliance speaker = factory.speakerBuilder().id("SPK" + i).build();
                assertEquals(expectedBrand, speaker.getBrand());
                assertEquals("Speaker", speaker.getType());
            }
        }

        @Test
        @DisplayName("Should maintain builder pattern integrity")
        void shouldMaintainBuilderPatternIntegrity() {
            ApplianceFactory factory = new HaierFactory();

            // Should be able to chain builder methods
            Appliance complexAppliance = factory.acBuilder()
                    .id("COMPLEX_AC")
                    .name("Complex AC Unit")
                    .level(26)
                    .on(true)
                    .online(true)
                    .powerUsage(800)
                    .build();

            assertEquals("COMPLEX_AC", complexAppliance.getId());
            assertEquals("Complex AC Unit", complexAppliance.getName());
            assertEquals("AC", complexAppliance.getType());
            assertEquals("Haier", complexAppliance.getBrand());
            assertEquals(26, complexAppliance.getLevel());
            assertTrue(complexAppliance.isOn());
            assertTrue(complexAppliance.isOnline());
            assertEquals(800, complexAppliance.getPowerUsage());
        }

        @Test
        @DisplayName("Should create independent appliance instances")
        void shouldCreateIndependentApplianceInstances() {
            HaierFactory factory = new HaierFactory();

            Appliance ac1 = factory.acBuilder().id("AC1").name("First AC").build();
            Appliance ac2 = factory.acBuilder().id("AC2").name("Second AC").build();

            // Should be different instances
            assertNotSame(ac1, ac2);
            assertNotEquals(ac1.getId(), ac2.getId());
            assertNotEquals(ac1.getName(), ac2.getName());

            // But same brand and type defaults
            assertEquals(ac1.getBrand(), ac2.getBrand());
            assertEquals(ac1.getType(), ac2.getType());
            assertEquals(ac1.getLevel(), ac2.getLevel());
        }

        @Test
        @DisplayName("Should support factory instantiation without parameters")
        void shouldSupportFactoryInstantiationWithoutParameters() {
            // Should be able to create factories using default constructors
            assertDoesNotThrow(() -> new HaierFactory());
            assertDoesNotThrow(() -> new LGFactory());
            assertDoesNotThrow(() -> new SonyFactory());

            // And use them immediately
            HaierFactory haierFactory = new HaierFactory();
            assertNotNull(haierFactory.acBuilder());
            assertNotNull(haierFactory.fanBuilder());
            assertNotNull(haierFactory.speakerBuilder());
        }
    }

    @Nested
    @DisplayName("Integration with Appliance Tests")
    class IntegrationWithApplianceTests {

        @Test
        @DisplayName("Should create appliances that work with Device interface")
        void shouldCreateAppliancesThatWorkWithDeviceInterface() {
            HaierFactory factory = new HaierFactory();
            Appliance ac = factory.acBuilder()
                    .id("DEVICE_TEST")
                    .name("Device Test AC")
                    .build();

            // Should implement Device interface methods
            assertDoesNotThrow(() -> {
                ac.toggleOnOff();
                ac.simulate(25);
            });

            // Verify device behavior
            assertTrue(ac.isOn()); // Should be on after toggle
            assertEquals(25, ac.getLevel()); // Should have simulated level
        }

        @Test
        @DisplayName("Should create appliances with proper power calculations")
        void shouldCreateAppliancesWithProperPowerCalculations() {
            ApplianceFactory[] factories = {
                new HaierFactory(), new LGFactory(), new SonyFactory()
            };

            for (ApplianceFactory factory : factories) {
                // Test AC power calculation
                Appliance ac = factory.acBuilder().id("PWR_AC").build();
                ac.toggleOnOff(); // Turn on
                ac.simulate(20); // Set temperature
                assertTrue(ac.getPowerUsage() > 0, "AC should consume power when on");

                // Test Fan power calculation
                Appliance fan = factory.fanBuilder().id("PWR_FAN").build();
                fan.toggleOnOff(); // Turn on
                fan.simulate(4); // Set speed
                assertTrue(fan.getPowerUsage() > 0, "Fan should consume power when on");

                // Test Speaker power calculation
                Appliance speaker = factory.speakerBuilder().id("PWR_SPEAKER").build();
                speaker.toggleOnOff(); // Turn on
                speaker.simulate(50); // Set volume
                assertTrue(speaker.getPowerUsage() > 0, "Speaker should consume power when on");
            }
        }

        @Test
        @DisplayName("Should create appliances that support observer pattern")
        void shouldCreateAppliancesThatSupportObserverPattern() {
            HaierFactory factory = new HaierFactory();
            Appliance ac = factory.acBuilder().id("OBSERVER_TEST").build();

            // Create a simple observer
            TestObserver observer = new TestObserver();
            
            // Should support observer pattern
            assertDoesNotThrow(() -> {
                ac.attach(observer);
                ac.toggleOnOff(); // Should notify observer
                ac.detach(observer);
            });

            assertTrue(observer.wasNotified(), "Observer should have been notified");
        }

        // Simple test observer for testing
        private static class TestObserver implements com.smarthome.observer.Observer {
            private boolean notified = false;

            @Override
            public void update(String message) {
                this.notified = true;
            }

            public boolean wasNotified() {
                return notified;
            }
        }
    }
}