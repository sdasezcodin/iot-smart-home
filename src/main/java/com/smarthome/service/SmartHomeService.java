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
 * Responsibilities include:
 * <ul>
 *     <li>Device registration, toggling, and simulation</li>
 *     <li>Network connectivity and device online/offline management</li>
 *     <li>Building dynamic network topology</li>
 *     <li>Fetching sensor readings</li>
 *     <li>Streaming live sensor data</li>
 * </ul>
 */
public class SmartHomeService {

    private final ApplianceDAO<Appliance> applianceDAO;
    private final SensorDataDAO<SensorData> readingDAO;
    private final Map<String, Appliance> memoryDevices = new ConcurrentHashMap<>();
    private final Client networkClient;
    private final Remote remote = new Remote();
    private final DeviceObserver dashboard;

    // For live streaming control
    private final AtomicBoolean streamingActive = new AtomicBoolean(false);

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
     * Loads all devices from the database into memory and attaches the dashboard observer.
     */
    public void loadDevicesFromDB() throws DatabaseException {
        memoryDevices.clear();
        applianceDAO.findAll().forEach(device -> {
            device.attach(dashboard);
            memoryDevices.put(device.getId(), device);
        });
    }

    /**
     * Registers a new device with the given type, brand, and name.
     */
    public void registerDevice(String type, String brand, String name)
            throws DeviceException, DatabaseException, UserException {

        String id = IdGenerator.generateID();
        InputValidator.validateName(name);

        ApplianceFactory factory = switch (brand.toLowerCase()) {
            case "haier" -> new HaierFactory();
            case "lg" -> new LGFactory();
            default -> new SonyFactory();
        };

        Appliance appliance = switch (type.toUpperCase()) {
            case "AC" -> factory.acBuilder().id(id).type(type).name(name).brand(brand).level(27).build();
            case "FAN" -> factory.fanBuilder().id(id).type(type).name(name).brand(brand).level(3).build();
            default -> factory.speakerBuilder().id(id).type(type).name(name).brand(brand).level(30).build();
        };

        appliance.attach(dashboard);
        memoryDevices.put(id, appliance);

        try {
            applianceDAO.save(appliance);
        } catch (Exception e) {
            memoryDevices.remove(id);
            throw new DeviceException("Failed to save device: " + e.getMessage(), e);
        }
    }

    /**
     * Deregisters a device by ID.
     */
    public void deregisterDevice(String id) throws DeviceException, DatabaseException {
        Appliance appliance = memoryDevices.get(id);
        if (appliance == null) throw new DeviceException("Device not found: " + id);

        memoryDevices.remove(id);
        try {
            applianceDAO.deleteById(id);
        } catch (Exception e) {
            memoryDevices.put(id, appliance);
            throw new DeviceException("Failed to delete device: " + e.getMessage(), e);
        }
    }

    /**
     * Toggles device ON/OFF using the command pattern.
     */
    public void toggleDevice(String deviceId) throws DeviceException {
        Appliance appliance = memoryDevices.get(deviceId);
        if (appliance == null) throw new DeviceException("Device not found: " + deviceId);

        try {
            Toggle toggle = new Toggle(appliance);
            remote.setCommand(toggle);
            remote.pressButton();
        } catch (Exception e) {
            throw new DeviceException("Failed to toggle device: " + deviceId, e);
        }
    }

    /**
     * Simulates a device action (AC temperature, Fan speed, Speaker volume).
     */
    public void simulateDevice(String deviceId, String levelInput) throws UserException, DeviceException {
        Appliance appliance = memoryDevices.get(deviceId);
        if (appliance == null) throw new DeviceException("Device not found: " + deviceId);
        if (!appliance.isOn()) throw new DeviceException("Device is off: " + deviceId);

        switch (appliance.getType().toUpperCase()) {
            case "AC" -> InputValidator.checkTemperatureRange(levelInput);
            case "FAN" -> InputValidator.checkSpeedRange(levelInput);
            default -> InputValidator.checkVolumeRange(levelInput);
        }

        int level = Integer.parseInt(levelInput.trim());

        try {
            Simulate simulate = new Simulate(appliance, level);
            remote.setCommand(simulate);
            remote.pressButton();
        } catch (Exception e) {
            throw new DeviceException("Failed to simulate device: " + deviceId, e);
        }
    }

    // ---------------- Network Management ----------------

    public void connectDevice(String deviceId) throws DeviceException, NetworkException {
        Appliance appliance = memoryDevices.get(deviceId);
        if (appliance == null) throw new DeviceException("Device not found: " + deviceId);
        if (!appliance.isOn()) throw new DeviceException("Device is OFF: " + deviceId);
        if (appliance.isOnline()) throw new DeviceException("Device already online: " + deviceId);

        networkClient.sendConnect(appliance.getName());
        appliance.setOnline(true);
    }

    public void disconnectDevice(String deviceId) throws DeviceException, NetworkException {
        Appliance appliance = memoryDevices.get(deviceId);
        if (appliance == null) throw new DeviceException("Device not found: " + deviceId);
        if (!appliance.isOnline()) throw new DeviceException("Device already offline: " + deviceId);

        networkClient.sendDisconnect(appliance.getName());
        appliance.setOnline(false);
    }

    // ---------------- Network Topology ----------------

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

    public List<SensorData> getReadingsByDevice(String deviceId, String limitInput) throws UserException, DatabaseException {
        InputValidator.validateSensorDataFetchLimit(limitInput);
        int limit = Integer.parseInt(limitInput.trim());
        return readingDAO.findByDeviceId(deviceId, limit);
    }

    public List<SensorData> getReadingsByDateRange(String start, String end) throws UserException, DatabaseException {
        InputValidator.validDateRange(start, end);
        return readingDAO.findByDateRange(start, end);
    }

    public List<Appliance> getAllDevices() {
        return new ArrayList<>(memoryDevices.values());
    }

    public void shutdownSystem() {
        networkClient.close();
        memoryDevices.values().forEach(a -> {
            try { applianceDAO.save(a); } catch (Exception ignored) {}
        });
    }

    // ---------------- Sensor Streaming ----------------


    private Thread streamThread; // add this at class level

    public void stopSensorStream() {
        streamingActive.set(false);
        if (streamThread != null && streamThread.isAlive()) {
            streamThread.interrupt();
            try {
                streamThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void startSensorStream() {
        if (streamingActive.get()) return;
        streamingActive.set(true);

        streamThread = new Thread(() -> {
            int counter = 0;

            while (streamingActive.get()) {
                for (Appliance appliance : memoryDevices.values()) {
                    if (!streamingActive.get()) break;
                    if (!appliance.isOnline() || !appliance.isOn()) continue;

                    counter++;

                    if (counter % 33 == 0) {
                        for (Appliance saveAppliance : memoryDevices.values()) {
                            if (!saveAppliance.isOnline() || !saveAppliance.isOn()) continue;

                            String saveReadingMsg = SensorDataGenerator.generateMessage(
                                    saveAppliance.getName(),
                                    saveAppliance.getType(),
                                    saveAppliance.getLevel(),
                                    saveAppliance.getPowerUsage()
                            );

                            SensorData sensorData = new SensorData.Builder()
                                    .id(IdGenerator.generateID())
                                    .deviceId(saveAppliance.getId())
                                    .date(DateTimeUtil.getCurrentDate())
                                    .time(DateTimeUtil.getCurrentTime())
                                    .data(saveReadingMsg)
                                    .build();

                            try {
                                readingDAO.save(sensorData);
                                System.out.println("✅ Saved to DB (10th read snapshot): " + saveReadingMsg);
                            } catch (Exception e) {
                                System.err.println("⚠️ Failed to save sensor data: " + e.getMessage());
                            }
                        }
                    }

                    // Now send current appliance’s reading to network
                    String readingMsg = SensorDataGenerator.generateMessage(
                            appliance.getName(),
                            appliance.getType(),
                            appliance.getLevel(),
                            appliance.getPowerUsage()
                    );

                    networkClient.sendSensorReading(readingMsg);

                    try {
                        Thread.sleep(3000);  // 3 seconds pause between each appliance
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        streamingActive.set(false);
                        break;
                    }
                }
            }
        }, "SensorStreamThread");

        streamThread.start();
    }

}

