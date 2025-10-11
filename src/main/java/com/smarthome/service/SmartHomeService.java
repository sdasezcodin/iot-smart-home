package com.smarthome.service;

import com.smarthome.exception.*;
import com.smarthome.factory.*;
import com.smarthome.model.*;
import com.smarthome.db.*;
import com.smarthome.network.Client;
import com.smarthome.util.*;
import com.smarthome.command.*;
import com.smarthome.view.DeviceObserver;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Service layer for managing smart home appliances, sensor data,
 * and network communication.
 * <p>
 * This class encapsulates the core business logic of the smart home system.
 * It acts as a bridge between the controller (user interface) and the data
 * access layer (DAOs), handling tasks such as device state management,
 * database persistence, and network interactions.
 */
public class SmartHomeService {

    // DAOs for database interaction, injected via the constructor.
    private final ApplianceDAO<Appliance> applianceDAO;
    private final SensorDataDAO<SensorData> readingDAO;

    // In-memory cache of all registered devices.
    // Using ConcurrentHashMap for thread-safe access from multiple threads.
    private final Map<String, Appliance> memoryDevices = new ConcurrentHashMap<>();

    // Network client for communicating with the server.
    private final Client networkClient;

    // Command pattern invoker to execute device actions.
    private final Remote remote = new Remote();

    // Observer to be notified of device state changes.
    private final DeviceObserver dashboard;

    // For live streaming control; AtomicBoolean ensures thread-safe operations.
    private final AtomicBoolean streamingActive = new AtomicBoolean(false);
    private Thread streamThread;

    /**
     * Constructs a new SmartHomeService with its dependencies.
     * This is an example of Dependency Injection.
     *
     * @param applianceDAO   DAO for managing Appliance entities.
     * @param readingDAO     DAO for managing SensorData entities.
     * @param networkClient  Client for network communication.
     * @param dashboard      Observer to receive updates on device state changes.
     */
    public SmartHomeService(ApplianceDAO<Appliance> applianceDAO,
                            SensorDataDAO<SensorData> readingDAO,
                            Client networkClient,
                            DeviceObserver dashboard) {
        this.applianceDAO = applianceDAO;
        this.readingDAO = readingDAO;
        this.networkClient = networkClient;
        this.dashboard = dashboard;
    }

    // ---------------- Device Management ----------------

    /**
     * Loads all devices from the database into memory.
     * The dashboard observer is attached to each loaded device.
     *
     * @throws DatabaseException if there is an error accessing the database.
     */
    public void loadDevicesFromDB() throws DatabaseException {
        memoryDevices.clear();
        applianceDAO.findAll().forEach(device -> {
            device.attach(dashboard);
            memoryDevices.put(device.getId(), device);
        });
    }

    /**
     * Registers a new device by creating it using a factory, saving it to the database,
     * and adding it to the in-memory cache.
     *
     * @param type  The type of the appliance (e.g., "AC", "FAN").
     * @param brand The brand of the appliance (e.g., "haier", "lg").
     * @param name  The user-defined name for the appliance.
     * @throws DeviceException   if the device creation or saving fails.
     * @throws DatabaseException if a database error occurs.
     * @throws UserException     if user input validation fails.
     */
    public void registerDevice(String type, String brand, String name)
            throws DeviceException, DatabaseException, UserException {

        String id = IdGenerator.generateID();
        InputValidator.validateName(name);

        // Use a factory to create the correct appliance builder based on the brand.
        ApplianceFactory factory = switch (brand.toLowerCase()) {
            case "haier" -> new HaierFactory();
            case "lg" -> new LGFactory();
            default -> new SonyFactory();
        };

        // Use a switch statement to create the correct appliance type with initial settings.
        Appliance appliance = switch (type.toUpperCase()) {
            case "AC" -> factory.acBuilder().id(id).type(type).name(name).brand(brand).level(27).build();
            case "FAN" -> factory.fanBuilder().id(id).type(type).name(name).brand(brand).level(3).build();
            default -> factory.speakerBuilder().id(id).type(type).name(name).brand(brand).level(30).build();
        };

        // Attach the dashboard as an observer.
        appliance.attach(dashboard);
        // Add the device to the in-memory cache.
        memoryDevices.put(id, appliance);

        try {
            // Persist the new device to the database.
            applianceDAO.save(appliance);
        } catch (Exception e) {
            // Rollback: if saving to the DB fails, remove the device from memory.
            memoryDevices.remove(id);
            throw new DeviceException("Failed to save device: " + e.getMessage(), e);
        }
    }

    /**
     * Deregisters a device by removing it from both the in-memory cache and the database.
     *
     * @param id The unique ID of the device to deregister.
     * @throws DeviceException   if the device is not found or deletion fails.
     * @throws DatabaseException if a database error occurs.
     */
    public void deregisterDevice(String id) throws DeviceException, DatabaseException {
        Appliance appliance = memoryDevices.get(id);
        if (appliance == null) throw new DeviceException("Device not found: " + id);

        memoryDevices.remove(id);
        try {
            applianceDAO.deleteById(id);
        } catch (Exception e) {
            // Rollback: if deleting from the DB fails, add the device back to memory.
            memoryDevices.put(id, appliance);
            throw new DeviceException("Failed to delete device: " + e.getMessage(), e);
        }
    }

    /**
     * Toggles a device's power state (ON/OFF) using the Command pattern.
     *
     * @param deviceId The ID of the device to toggle.
     * @throws DeviceException if the device is not found or an error occurs during the command execution.
     */
    public void toggleDevice(String deviceId) throws DeviceException {
        Appliance appliance = memoryDevices.get(deviceId);
        if (appliance == null) throw new DeviceException("Device not found: " + deviceId);

        try {
            // Create a Toggle command and set it on the remote invoker.
            Toggle toggle = new Toggle(appliance);
            remote.setCommand(toggle);
            // Execute the command.
            remote.pressButton();
        } catch (Exception e) {
            throw new DeviceException("Failed to toggle device: " + deviceId, e);
        }
    }

    /**
     * Simulates a device action (e.g., changing temperature, speed, or volume)
     * using the Command pattern.
     *
     * @param deviceId   The ID of the device to simulate.
     * @param levelInput The new level as a string.
     * @throws UserException   if the input is invalid.
     * @throws DeviceException if the device is not found or is currently off.
     */
    public void simulateDevice(String deviceId, String levelInput) throws UserException, DeviceException {
        Appliance appliance = memoryDevices.get(deviceId);
        if (appliance == null) throw new DeviceException("Device not found: " + deviceId);
        if (!appliance.isOn()) throw new DeviceException("Device is off: " + deviceId);

        // Validate the input based on the device type before parsing.
        switch (appliance.getType().toUpperCase()) {
            case "AC" -> InputValidator.checkTemperatureRange(levelInput);
            case "FAN" -> InputValidator.checkSpeedRange(levelInput);
            default -> InputValidator.checkVolumeRange(levelInput);
        }

        int level = Integer.parseInt(levelInput.trim());

        try {
            // Create a Simulate command and set it on the remote invoker.
            Simulate simulate = new Simulate(appliance, level);
            remote.setCommand(simulate);
            // Execute the command.
            remote.pressButton();
        } catch (Exception e) {
            throw new DeviceException("Failed to simulate device: " + deviceId, e);
        }
    }

    // ---------------- Network Management ----------------

    /**
     * Connects a device to the network by sending a command to the server and
     * updating the device's online status.
     *
     * @param deviceId The ID of the device to connect.
     * @throws DeviceException   if the device is not found or is already online/off.
     * @throws NetworkException  if the network client fails to send the message.
     */
    public void connectDevice(String deviceId) throws DeviceException, NetworkException {
        Appliance appliance = memoryDevices.get(deviceId);
        if (appliance == null) throw new DeviceException("Device not found: " + deviceId);
        if (!appliance.isOn()) throw new DeviceException("Device is OFF: " + deviceId);
        if (appliance.isOnline()) throw new DeviceException("Device already online: " + deviceId);

        networkClient.sendConnect(appliance.getName());
        appliance.setOnline(true);
    }

    /**
     * Disconnects a device from the network.
     *
     * @param deviceId The ID of the device to disconnect.
     * @throws DeviceException   if the device is not found or is already offline.
     * @throws NetworkException  if the network client fails to send the message.
     */
    public void disconnectDevice(String deviceId) throws DeviceException, NetworkException {
        Appliance appliance = memoryDevices.get(deviceId);
        if (appliance == null) throw new DeviceException("Device not found: " + deviceId);
        if (!appliance.isOnline()) throw new DeviceException("Device already offline: " + deviceId);

        networkClient.sendDisconnect(appliance.getName());
        appliance.setOnline(false);
    }

    // ---------------- Network Topology ----------------

    /**
     * Builds a string representation of the current network topology,
     * showing the server and all currently online devices.
     *
     * @return A formatted string of the network topology.
     */
    public String buildNetworkTopology() {
        Graph topology = new Graph();
        List<Appliance> onlineDevices = new ArrayList<>();
        final String serverNodeName = "Server";

        topology.addNode(serverNodeName);

        for (Appliance device : memoryDevices.values()) {
            if (device.isOnline() && !device.getName().equalsIgnoreCase(serverNodeName)) {
                onlineDevices.add(device);
                topology.addNode(device.getName());
            }
        }

        for (Appliance device : onlineDevices) {
            topology.addEdge(serverNodeName, device.getName());
        }

        return onlineDevices.isEmpty() ? "No devices online" : topology.showTopology(serverNodeName);
    }

    // ---------------- Sensor Readings ----------------

    /**
     * Fetches sensor readings for a specific device from the database.
     *
     * @param deviceId    The ID of the device.
     * @param limitInput  The maximum number of readings to fetch.
     * @return A list of sensor readings.
     * @throws UserException   if the input limit is invalid.
     * @throws DatabaseException if a database error occurs.
     */
    public List<SensorData> getReadingsByDevice(String deviceId, String limitInput) throws UserException, DatabaseException {

        InputValidator.validateSensorDataFetchLimit(limitInput);

        int limit = Integer.parseInt(limitInput.trim());

        return readingDAO.findByDeviceId(deviceId, limit);
    }

    /**
     * Fetches all sensor readings from the database within a specified date range.
     *
     * @param start The start date/time (e.g., "YYYY-MM-DDTHH:MM:SS").
     * @param end   The end date/time (e.g., "YYYY-MM-DDTHH:MM:SS").
     * @return A list of sensor readings.
     * @throws UserException   if the date range input is invalid.
     * @throws DatabaseException if a database error occurs.
     */
    public List<SensorData> getReadingsByDateRange(String start, String end) throws UserException, DatabaseException {
        InputValidator.validDateRange(start, end);
        return readingDAO.findByDateRange(start, end);
    }

    /**
     * Gets a list of all devices from the in-memory cache.
     * @return A list of Appliance objects.
     */
    public List<Appliance> getAllDevices() {
        return new ArrayList<>(memoryDevices.values());
    }

    /**
     * Performs a graceful shutdown of the system.
     * It closes the network connection and saves the current state of all
     * in-memory devices back to the database.
     */
    public void shutdownSystem() {
        networkClient.close();
        memoryDevices.values().forEach(a -> {
            try {
                applianceDAO.save(a);
            } catch (Exception ignored) {
                // Ignore any exceptions during shutdown to ensure the process completes.
            }
        });
    }

    // ---------------- Sensor Streaming ----------------

    /**
     * Stops the live sensor data stream gracefully.
     * It interrupts the streaming thread and waits for it to terminate.
     */
    public void stopSensorStream() {
        // Set the atomic flag to false to signal the thread to stop its loop.
        streamingActive.set(false);
        if (streamThread != null && streamThread.isAlive()) {
            // Interrupt the thread to wake it from its sleep state.
            streamThread.interrupt();
            try {
                // Wait for the thread to finish its work and terminate.
                streamThread.join();
            } catch (InterruptedException e) {
                // Restore the interrupted status.
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Starts a new background thread to stream live sensor data.
     * The method is idempotent, meaning it won't start a new stream if one is already running.
     */
    public void startSensorStream() {
        // Check the atomic flag to prevent starting multiple stream threads.
        if (streamingActive.get()) return;
        streamingActive.set(true);

        streamThread = new Thread(() -> {

            // The main streaming loop, controlled by the atomic flag.
            while (streamingActive.get()) {

                // Iterate through all in-memory devices.
                for (Appliance appliance : memoryDevices.values()) {
                    // Check the flag again to allow for an immediate stop.
                    if (!streamingActive.get()) break;

                    // Only process devices that are ON.
                    if (!appliance.isOn()) {
                        continue;
                    }

                    try {
                        // --- 1. DATABASE SAVE (Snapshot for this device) ---
                        String saveReadingMsg = SensorDataGenerator.generateMessage(
                                appliance.getName(),
                                appliance.getType(),
                                appliance.getLevel(),
                                appliance.getPowerUsage()
                        );

                        SensorData sensorData = new SensorData.Builder()
                                .id(IdGenerator.generateReadingDataID())
                                .deviceId(appliance.getId())
                                .date(DateTimeUtil.getCurrentDate())
                                .time(DateTimeUtil.getCurrentTime())
                                .data(saveReadingMsg)
                                .build();

                        readingDAO.save(sensorData);

                        // --- 2. NETWORK SEND (Real-time reading for this device) ---
                        // Only send data over the network if the device is ONLINE.
                        if (appliance.isOnline()) {
                            String readingMsg = SensorDataGenerator.generateMessage(
                                    appliance.getName(),
                                    appliance.getType(),
                                    appliance.getLevel(),
                                    appliance.getPowerUsage()
                            );
                            networkClient.sendSensorReading(readingMsg);
                        }
                    } catch (Exception ignored) {
                        // Ignore individual device processing failures to ensure the stream continues.
                    }

                }

                // Wait 9 seconds before starting the next full cycle of devices.
                try {
                    Thread.sleep(9000);
                } catch (InterruptedException e) {
                    // If interrupted, restore the flag and break out of the loop.
                    Thread.currentThread().interrupt();
                    streamingActive.set(false);
                    break;
                }
            }
        }, "SensorStreamThread");

        streamThread.start();
    }
}