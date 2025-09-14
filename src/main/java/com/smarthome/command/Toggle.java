package com.smarthome.command;

import com.smarthome.model.Appliance;

/**
 * Command to toggle an appliance's power state
 * between ON and OFF.
 */
public class Toggle implements Command {

    private final Appliance appliance;

    /**
     * Creates a new toggle command for a given appliance.
     *
     * @param appliance the appliance whose power state will be toggled
     */
    public Toggle(Appliance appliance) {
        this.appliance = appliance;
    }

    /**
     * Executes the toggle action on the target appliance.
     */
    @Override
    public void execute() {
        appliance.toggleOnOff();
    }
}
