package com.smarthome.model;

/**
 * Represents a generic smart device in the Smart Home system.
 * <p>
 * All devices must support toggling their power state
 * (on/off) and simulating behavior at a given level/value
 * (e.g., temperature, speed, volume).
 */
public interface Device {

    /**
     * Toggle the device power state.
     * <p>
     * If the device is currently ON, it should turn OFF.
     * If it is OFF, it should turn ON.
     */
    void toggleOnOff();

    /**
     * Simulate the device’s operation at a given level.
     * <p>
     * Examples:
     * <ul>
     *   <li>AC → simulate at a temperature level</li>
     *   <li>Fan → simulate at a speed level</li>
     *   <li>Speaker → simulate at a volume level</li>
     * </ul>
     *
     * @param val the intensity/level for simulation
     */
    void simulate(int val);
}
