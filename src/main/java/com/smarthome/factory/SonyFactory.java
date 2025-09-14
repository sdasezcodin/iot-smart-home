package com.smarthome.factory;

import com.smarthome.model.Appliance;

/**
 * Concrete factory for creating Sony brand appliances.
 * <p>
 * Provides preconfigured {@link Appliance.Builder} instances
 * for Air Conditioners, Fans, and Speakers with Sony defaults.
 */
public class SonyFactory implements ApplianceFactory {

    /**
     * Create a Sony AC builder with default settings.
     *
     * @return preconfigured {@link Appliance.Builder} for Sony AC
     */
    @Override
    public Appliance.Builder acBuilder() {
        return new Appliance.Builder()
                .type("AC")
                .brand("Sony")
                .level(24); // Default AC temperature
    }

    /**
     * Create a Sony Fan builder with default settings.
     *
     * @return preconfigured {@link Appliance.Builder} for Sony Fan
     */
    @Override
    public Appliance.Builder fanBuilder() {
        return new Appliance.Builder()
                .type("Fan")
                .brand("Sony")
                .level(3); // Default fan speed
    }

    /**
     * Create a Sony Speaker builder with default settings.
     *
     * @return preconfigured {@link Appliance.Builder} for Sony Speaker
     */
    @Override
    public Appliance.Builder speakerBuilder() {
        return new Appliance.Builder()
                .type("Speaker")
                .brand("Sony")
                .level(10); // Default speaker volume
    }
}
