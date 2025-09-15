package com.smarthome.controller;

import com.smarthome.model.Appliance;
import com.smarthome.service.SmartHomeService;
import com.smarthome.exception.ApplicationException;

import java.util.List;

/**
 * Controller layer for the Smart Home application.
 * This class acts as a mediator between the user interface (ConsoleMenu)
 * and the business logic layer (SmartHomeService). It handles user input
 * and orchestrates actions by calling the appropriate service methods.
 * It also handles and displays error messages to the user.
 */
public class SmartHomeController {

    private final SmartHomeService service;

    /**
     * Constructs a new SmartHomeController with a reference to the SmartHomeService.
     * This demonstrates the Dependency Injection pattern.
     * @param service The SmartHomeService instance to be used by this controller.
     */
    public SmartHomeController(SmartHomeService service) {
        this.service = service;
    }

    // ================================================================
    //                    Device Data and Status
    // ================================================================

    /**
     * Loads all devices from the database into the in-memory cache.
     * This is typically done at application startup.
     */
    public void loadDevices() {
        try {
            service.loadDevicesFromDB();
            System.out.println("Devices loaded successfully.");
        } catch (ApplicationException e) {
            System.out.println("⚠️ Error loading devices: " + e.getMessage());
        }
    }

    /**
     * Fetches and displays a list of all devices currently in memory.
     */
    public void showAllDevices() {
        try {
            List<Appliance> devices = service.getAllDevices();
            if (devices.isEmpty()) {
                System.out.println("No devices registered yet.");
                return;
            }

            for (Appliance d : devices) {
                System.out.println(d);
            }
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to fetch devices: " + e.getMessage());
        }
    }

    // =================================================================
    //                    Device Management
    // =================================================================

    /**
     * Registers a new smart device with the system and saves it to the database.
     * @param type The type of the device (e.g., "AC", "FAN", "SPEAKER").
     * @param brand The brand of the device (e.g., "haier", "lg").
     * @param name The user-defined name for the device.
     */
    public void saveDevice(String type, String brand, String name) {
        try {
            service.registerDevice(type, brand, name);
            System.out.println("Device registered: " + name);
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to register device: " + e.getMessage());
        }
    }

    /**
     * Deregisters a device from the system and deletes it from the database.
     * @param id The unique ID of the device to be deleted.
     */
    public void deleteDevice(String id) {
        try {
            service.deregisterDevice(id);
            System.out.println("Device deregistered");
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to deregister device: " + e.getMessage());
        }
    }

    // =================================================================
    //                    Network Management
    // =================================================================

    /**
     * Connects a device to the network, simulating an online state.
     * @param deviceId The unique ID of the device to connect.
     */
    public void connectDevice(String deviceId) {
        try {
            service.connectDevice(deviceId);
            System.out.println("Device connected.");
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to connect device: " + e.getMessage());
        }
    }

    /**
     * Disconnects a device from the network, simulating an offline state.
     * @param deviceId The unique ID of the device to disconnect.
     */
    public void disconnectDevice(String deviceId) {
        try {
            service.disconnectDevice(deviceId);
            System.out.println("Device disconnected");
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to disconnect device: " + e.getMessage());
        }
    }

    // =================================================================
    //                    Network Topology
    // =================================================================

    /**
     * Builds and displays the current network topology, showing all connected devices.
     */
    public void showTopology() {
        try {
            String topology = service.buildNetworkTopology();
            System.out.println("CURRENT NETWORK TOPOLOGY:\n");
            System.out.println(topology);
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to display network topology: " + e.getMessage());
        }
    }

    // =================================================================
    //                    Device Control
    // =================================================================

    /**
     * Toggles a device's power state (ON/OFF).
     * @param id The unique ID of the device to toggle.
     */
    public void toggleOnOff(String id) {
        try {
            service.toggleDevice(id);
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to toggle device: " + e.getMessage());
        }
    }

    /**
     * Simulates a change in a device's state, such as adjusting fan speed,
     * AC temperature, or speaker volume.
     * @param deviceId The unique ID of the device to simulate.
     * @param levelInput The new level (e.g., "3" for fan speed, "24" for temperature).
     */
    public void simulate(String deviceId, String levelInput) {
        try {
            service.simulateDevice(deviceId, levelInput);
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to simulate device: " + e.getMessage());
        }
    }

    // =================================================================
    //                    Sensor Readings
    // =================================================================

    /**
     * Fetches and displays sensor readings for a specific device, up to a given limit.
     * @param deviceId The unique ID of the device.
     * @param limit The maximum number of readings to fetch.
     */
    public void showReadingsByDevice(String deviceId, String limit) {
        try {
            var readings = service.getReadingsByDevice(deviceId, limit);
            if (readings.isEmpty()) {
                System.out.println("No readings found for device: " + deviceId);
                return;
            }
            readings.forEach(System.out::println);
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to fetch readings for " + deviceId + ": " + e.getMessage());
        }
    }

    /**
     * Fetches and displays sensor readings within a specific date range.
     * @param startDate The start date of the range (YYYY-MM-DD).
     * @param endDate The end date of the range (YYYY-MM-DD).
     */
    public void showReadingsByDateRange(String startDate, String endDate) {
        try {
            var readings = service.getReadingsByDateRange(startDate, endDate);
            if (readings.isEmpty()) {
                System.out.println("No readings found in the given date range.");
                return;
            }
            readings.forEach(System.out::println);
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to fetch readings by date range: " + e.getMessage());
        }
    }

    // =================================================================
    //                    Sensor / Dashboard
    // =================================================================

    /**
     * Starts the live sensor data stream, which periodically fetches and displays
     * sensor data from connected devices.
     */
    public void startDashboard() {
        try {
            service.startSensorStream();
            System.out.println("📊 Live dashboard started.");
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to start live dashboard: " + e.getMessage());
        }
    }

    /**
     * Stops the live sensor data stream.
     */
    public void stopDashboard() {
        try {
            service.stopSensorStream();
            System.out.println("📊 Live dashboard stopped.");
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to stop live dashboard: " + e.getMessage());
        }
    }
}