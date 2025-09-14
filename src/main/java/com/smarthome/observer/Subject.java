package com.smarthome.observer;

/**
 * Represents a subject in the Observer pattern.
 * <p>
 * Any class implementing this interface can have observers attached,
 * detached, and notified of events or state changes.
 */
public interface Subject {

    /**
     * Attaches an observer to this subject.
     *
     * @param o Observer to attach
     */
    void attach(Observer o);

    /**
     * Detaches an observer from this subject.
     *
     * @param o Observer to detach
     */
    void detach(Observer o);

    /**
     * Notifies all attached observers with a message.
     *
     * @param message Information about the state change or event
     */
    void notifyObservers(String message);
}
