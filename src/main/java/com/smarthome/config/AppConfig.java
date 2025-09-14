package com.smarthome.config;

import com.smarthome.controller.SmartHomeController;
import com.smarthome.db.ApplianceDB;
import com.smarthome.db.SensorDataDB;
import com.smarthome.network.Client;
import com.smarthome.network.Server;
import com.smarthome.service.SmartHomeService;
import com.smarthome.view.DeviceObserver;
import com.smarthome.view.ConsoleMenu;

/**
 * Configuration class for the Smart Home application.
 * This class uses the Singleton pattern to ensure only one instance
 * of the application's core components is created. It also handles
 * the initialization and graceful shutdown of the entire system.
 */
public class AppConfig {

    // A static instance of AppConfig to enforce the Singleton pattern.
    private static AppConfig instance;

    // Core components of the application. They are declared here to facilitate
    // dependency injection in the constructor.
    private final SmartHomeService smartHomeService;
    private final SmartHomeController deviceController;
    private final ConsoleMenu consoleMenu;
    private final ApplianceDB applianceDAO;
    private final Server server;
    private final Client client;
    private final DeviceObserver dashboard;

    /**
     * Private constructor to prevent direct instantiation from outside the class.
     * This is required for the Singleton pattern.
     */
    private AppConfig() {
        // Initialize and start the network server in a background thread.
        // The server runs as a daemon so it won't block the application from shutting down.
        this.server = new Server(5555);
        Thread serverThread = new Thread(server::start, "SmartHome-Server");
        serverThread.setDaemon(true);
        serverThread.start();

        // Pause briefly to ensure the server thread has started and is ready
        // to accept connections before the client attempts to connect.
        try { Thread.sleep(500); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Initialize the Data Access Objects (DAOs) for database interactions.
        this.applianceDAO = new ApplianceDB();
        SensorDataDB readingDAO = new SensorDataDB();

        // Initialize the network client and connect to the local server.
        this.client = new Client("localhost", 5555);
        this.client.connectToServer();

        // The DeviceObserver acts as a "dashboard" to monitor device state changes.
        this.dashboard = new DeviceObserver();

        // Initialize the core service layer (business logic).
        // Dependencies (DAOs, client, dashboard) are injected into the service.
        this.smartHomeService = new SmartHomeService(applianceDAO, readingDAO, client, dashboard);

        // Initialize the controller, which handles user input and orchestrates the service.
        this.deviceController = new SmartHomeController(smartHomeService);

        // Initialize the console menu for the user interface, passing the controller.
        this.consoleMenu = new ConsoleMenu(deviceController);
    }

    /**
     * Returns the single instance of the AppConfig class.
     * If the instance does not exist, it creates one.
     * This is the entry point for accessing all core application components.
     * @return The singleton instance of AppConfig.
     */
    public static AppConfig init() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    /**
     * Performs a graceful shutdown of the entire application.
     * The shutdown sequence is critical: network connections are closed first,
     * followed by saving any in-memory data to the database to prevent data loss.
     */
    public void shutdown() {
        System.out.println("Shutting down the system...");

        // Ensure a graceful shutdown by stopping the server first.
        if (server != null) {
            server.stop();
        }

        // Close the client's network connection.
        if (client != null) {
            client.close();
        }

        // Persist any remaining data to the database before the application exits.
        smartHomeService.shutdownSystem();
        System.out.println("System shutdown complete.");
    }

    // --- Public Getters for all core components ---

    public ConsoleMenu getConsoleMenu() { return consoleMenu; }
    public SmartHomeController getDeviceController() { return deviceController; }
    public SmartHomeService getSmartHomeService() { return smartHomeService; }
    public Server getServer() { return server; }
    public Client getClient() { return client; }
    public DeviceObserver getDashboard() { return dashboard; }
}