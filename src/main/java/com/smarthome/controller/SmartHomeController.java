package com.smarthome.controller;

import com.smarthome.model.Appliance;
import com.smarthome.service.SmartHomeService;
import com.smarthome.exception.ApplicationException;

import java.util.List;

public class SmartHomeController {
    private final SmartHomeService service;

    public SmartHomeController(SmartHomeService service) {
        this.service = service;
    }

    // ---------------- Load Devices ----------------
    public void loadDevices() {
        try {
            service.loadDevicesFromDB();
            System.out.println("Devices loaded successfully.");
        } catch (ApplicationException e) {
            System.out.println("⚠️ Error loading devices: " + e.getMessage());
        }
    }

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

    // ================= Device Management =================
    public void saveDevice(String type, String brand, String name) {
        try {
            service.registerDevice(type, brand, name);
            System.out.println("Device registered: " + name);
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to register device: " + e.getMessage());
        }
    }

    public void deleteDevice(String id) {
        try {
            service.deregisterDevice(id);
            System.out.println("Device deregistered");
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to deregister device: " + e.getMessage());
        }
    }

    // ================= Network Management =================
    public void connectDevice(String deviceId) {
        try {
            service.connectDevice(deviceId);
            System.out.println("Device connected.");
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to connect device: " + e.getMessage());
        }
    }

    public void disconnectDevice(String deviceId) {
        try {
            service.disconnectDevice(deviceId);
            System.out.println("Device disconnected");
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to disconnect device: " + e.getMessage());
        }
    }

    // ================= Topology =================
    public void showTopology() {
        try {
            String topology = service.buildNetworkTopology();
            System.out.println("CURRENT NETWORK TOPOLOGY:\n");
            System.out.println(topology);
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to display network topology: " + e.getMessage());
        }
    }


    // ================= Device Control =================
    public void toggleOnOff(String id) {
        try {
            service.toggleDevice(id);
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to toggle device: " + e.getMessage());
        }
    }

    public void simulate(String deviceId, String levelInput) {
        try {
            service.simulateDevice(deviceId, levelInput);
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to simulate device: " + e.getMessage());
        }
    }

    // ================= Sensor Readings =================
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

    // ================= Sensor / Dashboard =================
    public void startDashboard() {
        try {
            service.startSensorStream();
            System.out.println("📊 Live dashboard started.");
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to start live dashboard: " + e.getMessage());
        }
    }

    public void stopDashboard() {
        try {
            service.stopSensorStream();
            System.out.println("📊 Live dashboard stopped.");
        } catch (ApplicationException e) {
            System.out.println("⚠️ Failed to stop live dashboard: " + e.getMessage());
        }
    }

}
