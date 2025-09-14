package com.smarthome.factory;

import com.smarthome.model.Appliance;

/**
 * Factory interface for creating appliance builders.
 * <p>
 * Each method returns a preconfigured {@link Appliance.Builder}
 * for a specific appliance type (e.g., AC, Fan, Speaker).
 */
public interface ApplianceFactory {

    /**
     * Get a builder for an Air Conditioner appliance.
     *
     * @return an {@link Appliance.Builder} preconfigured for AC
     */
    Appliance.Builder acBuilder();

    /**
     * Get a builder for a Fan appliance.
     *
     * @return an {@link Appliance.Builder} preconfigured for Fan
     */
    Appliance.Builder fanBuilder();

    /**
     * Get a builder for a Speaker appliance.
     *
     * @return an {@link Appliance.Builder} preconfigured for Speaker
     */
    Appliance.Builder speakerBuilder();
}
