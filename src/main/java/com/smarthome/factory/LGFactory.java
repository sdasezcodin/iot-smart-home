package com.smarthome.factory;

import com.smarthome.model.Appliance;

/**
 * Concrete factory for creating LG brand appliances.
 * <p>
 * Provides preconfigured {@link Appliance.Builder} instances
 * for Air Conditioners, Fans, and Speakers with LG defaults.
 */
public class LGFactory implements ApplianceFactory {

    /**
     * Create an LG AC builder with default settings.
     *
     * @return preconfigured {@link Appliance.Builder} for LG AC
     */
    @Override
    public Appliance.Builder acBuilder() {
        return new Appliance.Builder()
                .type("AC")
                .brand("LG")
                .level(24); // Default AC temperature
    }

    /**
     * Create an LG Fan builder with default settings.
     *
     * @return preconfigured {@link Appliance.Builder} for LG Fan
     */
    @Override
    public Appliance.Builder fanBuilder() {
        return new Appliance.Builder()
                .type("Fan")
                .brand("LG")
                .level(3); // Default fan speed
    }

    /**
     * Create an LG Speaker builder with default settings.
     *
     * @return preconfigured {@link Appliance.Builder} for LG Speaker
     */
    @Override
    public Appliance.Builder speakerBuilder() {
        return new Appliance.Builder()
                .type("Speaker")
                .brand("LG")
                .level(10); // Default speaker volume
    }
}
