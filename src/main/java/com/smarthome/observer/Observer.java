package com.smarthome.observer;

/**
 * Represents an observer in the Observer pattern.
 * <p>
 * Any class implementing this interface can receive notifications
 * from subjects it is attached to.
 */
public interface Observer {

    /**
     * Called when a subject notifies its observers of a change or event.
     *
     * @param message Information about the change or event
     */
    void update(String message);
}
