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

    private final List<Observer> observers = new CopyOnWriteArrayList<>();

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

    public Appliance() {}

    @DynamoDbPartitionKey
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public synchronized boolean isOn() { return on; }
    public synchronized void setOn(boolean on) { this.on = on; }

    public synchronized boolean isOnline() { return online; }
    public synchronized void setOnline(boolean online) { this.online = online; }

    public synchronized int getLevel() { return level; }
    public synchronized void setLevel(int level) { this.level = level; }

    public synchronized int getPowerUsage() { return powerUsage; }
    public synchronized void setPowerUsage(int powerUsage) { this.powerUsage = powerUsage; }

    @Override
    public synchronized void toggleOnOff() {
        this.on = !this.on;
        this.powerUsage = this.on ? calculatePower(level) : 0;
        notifyObservers(GREEN + name + " turned " + (on ? "ON" : "OFF") + RESET);
    }

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

        notifyObservers(CYAN + name + " " + simulationDetail + RESET);
    }

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




    @Override
    public void attach(Observer o) { observers.add(o); }

    @Override
    public void detach(Observer o) { observers.remove(o); }

    @Override
    public void notifyObservers(String message) {
        for (Observer o : observers) o.update(message);
    }

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
        public Appliance build() { return new Appliance(this); }
    }

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
