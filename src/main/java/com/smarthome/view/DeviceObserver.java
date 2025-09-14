package com.smarthome.view;

import com.smarthome.observer.Observer;
import static com.smarthome.util.ConsoleColorsUtil.*;

/**
 * An implementation of the {@link Observer} interface.
 * <p>
 * This class acts as a simple display or a logger (a "dashboard") for
 * the smart home system. It subscribes to {@link com.smarthome.model.Appliance}
 * objects and receives notifications whenever their state changes, printing
 * the update messages to the console.
 */
public class DeviceObserver implements Observer {

    /**
     * Receives and processes an update message from a subject it is observing.
     * This method is automatically called by the {@code Appliance} objects
     * when their state changes.
     *
     * @param message The string message containing details of the state change.
     */
    @Override
    public void update(String message) {
        System.out.println(YELLOW + BOLD + "[Device Observer] " + RESET + message);
    }
}