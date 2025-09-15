package com.smarthome.model;

import com.smarthome.observer.Observer;
import com.smarthome.observer.Subject;
import com.smarthome.util.ConsoleColorsUtil;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.smarthome.util.ConsoleColorsUtil.*;

/**
 * Represents a smart appliance in the system.
 * <p>
 * This class serves as the core data model for a smart device. It contains all the properties
 * and behaviors of an appliance, such as its state (on/off, online/offline), settings (level),
 * and power usage. It also implements the {@link Subject} interface to support the
 * Observer pattern, allowing other components (like a dashboard) to be notified of state changes.
 * The class is annotated with {@link DynamoDbBean} to enable direct mapping to a DynamoDB table.
 */
@DynamoDbBean
public class Appliance implements Device, Subject {

    private String id;
    private String type;
    private String name;
    private String brand;
    private boolean on;
    private boolean online;
    private int level;
    private int powerUsage;

    // The list of observers to be notified of state changes.
    // CopyOnWriteArrayList is used for thread safety in a multi-threaded environment.
    private final List<Observer> observers = new CopyOnWriteArrayList<>();

    /**
     * Private constructor used by the Builder pattern to create an immutable Appliance object.
     * @param builder The builder instance containing all the properties.
     */
    private Appliance(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.name = builder.name;
        this.brand = builder.brand;
        this.on = builder.on;
        this.online = builder.online;
        this.level = builder.level;
        this.powerUsage = builder.powerUsage;
    }

    /**
     * Default constructor. Required by the DynamoDB Enhanced Client.
     */
    public Appliance() {}

    // =================================================================
    //                    Getters and Setters
    // =================================================================

    /**
     * @return The unique identifier of the appliance.
     */
    @DynamoDbPartitionKey
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // Other getters and setters follow a similar pattern
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    /**
     * @return The power state of the appliance (true for ON, false for OFF).
     */
    public synchronized boolean isOn() { return on; }
    public synchronized void setOn(boolean on) { this.on = on; }

    /**
     * @return The network state of the appliance (true for online, false for offline).
     */
    public synchronized boolean isOnline() { return online; }
    public synchronized void setOnline(boolean online) { this.online = online; }

    /**
     * @return The current operational level of the appliance (e.g., fan speed, AC temperature).
     */
    public synchronized int getLevel() { return level; }
    public synchronized void setLevel(int level) { this.level = level; }

    /**
     * @return The current power consumption of the appliance in Watts (W).
     */
    public synchronized int getPowerUsage() { return powerUsage; }
    public synchronized void setPowerUsage(int powerUsage) { this.powerUsage = powerUsage; }

    // =================================================================
    //                    Behavioral Methods
    // =================================================================

    /**
     * Toggles the power state of the appliance (ON/OFF).
     * Power usage is recalculated and observers are notified.
     */
    @Override
    public synchronized void toggleOnOff() {
        this.on = !this.on;
        // Recalculate power usage based on the new state.
        this.powerUsage = this.on ? calculatePower(level) : 0;
        // Notify observers of the state change with a formatted message.
        notifyObservers(GREEN + name + " turned " + (on ? "ON" : "OFF") + RESET);
    }

    /**
     * Simulates a change in the appliance's operational level.
     * The power usage is recalculated only if the appliance is currently on.
     * @param val The new operational level.
     */
    @Override
    public synchronized void simulate(int val) {
        this.level = val;
        if (on) powerUsage = calculatePower(val);

        String simulationDetail = switch (type.toUpperCase()) {
            case "AC" -> "temperature set to " + val + "°C";
            case "SPEAKER" -> "volume set to " + val + "%";
            case "FAN" -> "speed set to level " + val;
            default -> "level adjusted to " + val;
        };

        // Notify observers about the simulation action.
        notifyObservers(CYAN + name + " " + simulationDetail + RESET);
    }

    /**
     * Calculates the estimated power consumption based on the appliance type and its current level.
     * @param level The current operational level (e.g., temperature, speed, volume).
     * @return The estimated power usage in Watts.
     */
    public int calculatePower(int level) {
        return switch (type.toUpperCase()) {
            case "AC" -> {
                int baseIdle = 50;
                int activeLoad = Math.max(0, (30 - level) * 80);
                yield baseIdle + activeLoad;
            }
            case "FAN" -> switch (level) {
                case 1 -> 15;
                case 2 -> 25;
                case 3 -> 35;
                case 4 -> 45;
                case 5 -> 55;
                default -> 0;
            };
            case "SPEAKER" -> 5 + (int)(level * 0.5);
            default -> 0;
        };
    }

    // =================================================================
    //                    Observer Pattern Implementation
    // =================================================================

    /**
     * Attaches an observer to this subject to receive notifications.
     * @param o The observer to attach.
     */
    @Override
    public void attach(Observer o) { observers.add(o); }

    /**
     * Detaches an observer from this subject.
     * @param o The observer to detach.
     */
    @Override
    public void detach(Observer o) { observers.remove(o); }

    /**
     * Notifies all attached observers with a specific message.
     * @param message The message to send to the observers.
     */
    @Override
    public void notifyObservers(String message) {
        for (Observer o : observers) o.update(message);
    }

    // =================================================================
    //                    Builder Pattern
    // =================================================================

    /**
     * A static nested class that implements the Builder pattern for creating Appliance instances.
     * This provides a clear, readable, and flexible way to create objects with many optional parameters.
     */
    public static class Builder {
        private String id;
        private String type;
        private String name;
        private String brand;
        private boolean on = false;
        private boolean online = false;
        private int level = 0;
        private int powerUsage = 0;

        public Builder id(String id) { this.id = id; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder on(boolean on) { this.on = on; return this; }
        public Builder online(boolean online) { this.online = online; return this; }
        public Builder level(int level) { this.level = level; return this; }
        public Builder powerUsage(int powerUsage) { this.powerUsage = powerUsage; return this; }

        /**
         * Builds and returns a new {@link Appliance} instance with the specified properties.
         * @return A new Appliance object.
         */
        public Appliance build() { return new Appliance(this); }
    }

    /**
     * Returns a string representation of the Appliance object, including all its properties.
     * @return A formatted string with appliance details
     */
    @Override
    public String toString() {
        return PURPLE + "Appliance{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", type='" + type + '\'' +
                ", online=" + online +
                ", on=" + on +
                ", level=" + level +
                ", powerUsage=" + powerUsage +
                '}' + RESET;
    }
}