package com.smarthome.config;

import com.smarthome.controller.SmartHomeController;
import com.smarthome.db.ApplianceDB;
import com.smarthome.db.SensorDataDB;
import com.smarthome.network.Client;
import com.smarthome.network.Server;
import com.smarthome.service.SmartHomeService;
import com.smarthome.view.DeviceObserver;
import com.smarthome.view.ConsoleMenu;

public class AppConfig {

    private static AppConfig instance;

    private final SmartHomeService smartHomeService;
    private final SmartHomeController deviceController;
    private final ConsoleMenu consoleMenu;
    private final ApplianceDB applianceDAO;
    private final Server server;
    private final Client client;
    private final DeviceObserver dashboard;

    private AppConfig() {
        this.server = new Server(5555);
        Thread serverThread = new Thread(server::start, "SmartHome-Server");
        serverThread.setDaemon(true);
        serverThread.start();

        try { Thread.sleep(500); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        this.applianceDAO = new ApplianceDB();
        SensorDataDB readingDAO = new SensorDataDB();

        this.client = new Client("localhost", 5555);
        this.client.connectToServer();

        this.dashboard = new DeviceObserver();

        this.smartHomeService = new SmartHomeService(applianceDAO, readingDAO, client, dashboard);

        this.deviceController = new SmartHomeController(smartHomeService);

        this.consoleMenu = new ConsoleMenu(deviceController);
    }

    public static AppConfig init() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    // --- NEW CENTRAL SHUTDOWN METHOD ---
    public void shutdown() {
        System.out.println("Shutting down the system...");

        // Stop the network server first
        if (server != null) {
            server.stop();
        }

        // Then close the network client
        if (client != null) {
            client.close();
        }

        // Finally, save all in-memory data
        smartHomeService.shutdownSystem();
        System.out.println("System shutdown complete.");
    }

    public ConsoleMenu getConsoleMenu() { return consoleMenu; }
    public SmartHomeController getDeviceController() { return deviceController; }
    public SmartHomeService getSmartHomeService() { return smartHomeService; }
    public Server getServer() { return server; }
    public Client getClient() { return client; }
    public DeviceObserver getDashboard() { return dashboard; }
}