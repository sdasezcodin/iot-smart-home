package com.smarthome.factory;

import com.smarthome.model.Appliance;

/**
 * Concrete factory for creating Haier brand appliances.
 * <p>
 * Provides preconfigured {@link Appliance.Builder} instances
 * for Air Conditioners, Fans, and Speakers with Haier defaults.
 */
public class HaierFactory implements ApplianceFactory {

    /**
     * Create a Haier AC builder with default settings.
     *
     * @return preconfigured {@link Appliance.Builder} for Haier AC
     */
    @Override
    public Appliance.Builder acBuilder() {
        return new Appliance.Builder()
                .type("AC")
                .brand("Haier")
                .level(24); // Default AC temperature
    }

    /**
     * Create a Haier Fan builder with default settings.
     *
     * @return preconfigured {@link Appliance.Builder} for Haier Fan
     */
    @Override
    public Appliance.Builder fanBuilder() {
        return new Appliance.Builder()
                .type("Fan")
                .brand("Haier")
                .level(3); // Default fan speed
    }

    /**
     * Create a Haier Speaker builder with default settings.
     *
     * @return preconfigured {@link Appliance.Builder} for Haier Speaker
     */
    @Override
    public Appliance.Builder speakerBuilder() {
        return new Appliance.Builder()
                .type("Speaker")
                .brand("Haier")
                .level(10); // Default speaker volume
    }
}
