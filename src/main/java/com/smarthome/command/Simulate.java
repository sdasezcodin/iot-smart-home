package com.smarthome.command;

import com.smarthome.model.Appliance;

/**
 * Command to simulate the operation of an appliance
 * at a specified intensity level.
 */
public class Simulate implements Command {

    private final Appliance appliance;
    private final int level;

    /**
     * Creates a new simulate command for a given appliance.
     *
     * @param appliance the appliance to simulate
     * @param level     the intensity level for simulation
     */
    public Simulate(Appliance appliance, int level) {
        this.appliance = appliance;
        this.level = level;
    }

    /**
     * Executes the simulation on the target appliance.
     */
    @Override
    public void execute() {
        appliance.simulate(level);
    }
}
