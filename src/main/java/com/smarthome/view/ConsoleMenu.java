package com.smarthome.view;

import com.smarthome.controller.SmartHomeController;
import com.smarthome.config.AppConfig;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import static com.smarthome.util.ConsoleColorsUtil.*;

/**
 * A console-based user interface for interacting with the Smart Home system.
 * <p>
 * This class provides a text-based menu for users to manage devices,
 * view network topology, access sensor data, and monitor real-time
 * activity. It acts as the view layer, communicating with the
 * {@link SmartHomeController} to execute commands.
 */
public class ConsoleMenu {

    private final SmartHomeController controller;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Constructs a new ConsoleMenu.
     *
     * @param controller The controller instance that handles all business logic.
     */
    public ConsoleMenu(SmartHomeController controller) {
        this.controller = controller;
        showWelcomeScreen();
        // Load existing devices from the database at startup.
        controller.loadDevices();
    }

    // --- Welcome Screen and Main Menu Logic ---

    /**
     * Displays a welcome screen with an ASCII logo and a loading animation.
     */
    private void showWelcomeScreen() {
        clearScreen();
        String[] logo = {
                "   _____                      _     _    _                      ",
                "  / ____|                    | |   | |  | |                     ",
                " | (___  _ __ ___   __ _ _ __| |_  | |__| | ___  _ __ ___   ___ ",
                "  \\___ \\| '_ ` _ \\ / _` | '__| __| |  __  |/ _ \\| '_ ` _ \\ / _ \\",
                "  ____) | | | | | | (_| | |  | |_  | |  | | (_) | | | | | |  __/",
                " "
                        + "|_____/|_| |_| |_|\\__,_|_|   \\__| |_|  |_|\\___/|_| |_| |_|\\___|",
                "                                                                ",
                "               IoT Dashboard & Control System                   "
        };

        System.out.println();
        for (String line : logo) {
            System.out.println(CYAN + line + RESET);
            sleep(100);
        }

        System.out.println("\n" + YELLOW + BOLD + UNDERLINE + "Welcome to Your Smart Home Control Center" + RESET);
        System.out.println(GREEN + "Connecting to your smart devices..." + RESET);
        sleep(1000);
        System.out.print("Loading device registry");
        for (int i = 0; i < 3; i++) {
            System.out.print(".");
            sleep(300);
        }
        System.out.println();
        sleep(500);
    }

    /**
     * Displays the main menu and handles user input to navigate to
     * different sections of the application.
     */
    public void showMainMenu() {
        while (true) {
            clearScreen();
            printMenuHeader("SMART HOME MENU");
            System.out.println(
                    CYAN + " [1] Device Management " + RESET + YELLOW + "- Control your devices" + RESET + "\n" +
                            CYAN + " [2] Network " + RESET + YELLOW + "- Manage connections" + RESET + "\n" +
                            CYAN + " [3] Sensor Readings " + RESET + YELLOW + "- View historical data" + RESET + "\n" +
                            CYAN + " [4] Dashboard " + RESET + YELLOW + "- Real-time monitoring" + RESET + "\n" +
                            CYAN + " [0] Exit " + RESET + YELLOW + "- Close application" + RESET
            );
            System.out.println();
            int choice = readIntInput(BOLD + BLUE + "Enter your choice: " + RESET);

            switch (choice) {
                case 1 -> deviceManagementMenu();
                case 2 -> networkMenu();
                case 3 -> sensorMenu();
                case 4 -> launchDashboard();
                case 0 -> {
                    exitApplication();
                    return;
                }
                default -> {
                    System.out.println(RED + "⚠️ Invalid choice! Please select a valid option." + RESET);
                    sleep(1500);
                }
            }
        }
    }

    // --- Device Management Menu Logic ---

    /**
     * Displays the device management menu and handles user input for
     * managing devices (register, deregister, toggle, simulate).
     */
    private void deviceManagementMenu() {
        while (true) {
            clearScreen();
            printMenuHeader("DEVICE MANAGEMENT");
            System.out.println(BOLD + YELLOW + "CURRENT DEVICES" + RESET);
            controller.showAllDevices();
            System.out.println();
            System.out.println(
                    CYAN + " [1] Register " + RESET + YELLOW + "- Add a new device" + RESET + "\n" +
                            CYAN + " [2] Deregister " + RESET + YELLOW + "- Remove a device" + RESET + "\n" +
                            CYAN + " [3] Toggle On/Off " + RESET + YELLOW + "- Turn device on/off" + RESET + "\n" +
                            CYAN + " [4] Simulate " + RESET + YELLOW + "- Change device settings" + RESET + "\n" +
                            CYAN + " [0] Back " + RESET + YELLOW + "- Return to main menu" + RESET
            );
            System.out.println();

            int choice = readIntInput(BOLD + BLUE + "Enter your choice: " + RESET);
            switch (choice) {
                case 1 -> registerDevice();
                case 2 -> deregisterDevice();
                case 3 -> toggleDevice();
                case 4 -> simulateDevice();
                case 0 -> { return; }
                default -> {
                    System.out.println(RED + "⚠️ Invalid choice! Please select a valid option." + RESET);
                    sleep(1500);
                }
            }
        }
    }

    /** Handles the registration of a new device based on user input. */
    private void registerDevice() {
        String type = null;
        // Loop until a valid device type is selected.
        while (type == null) {
            System.out.println("Select Type: 1. AC  2. Fan  3. Speaker");
            int choice = readIntInput("Choice: ");
            type = switch (choice) {
                case 1 -> "AC";
                case 2 -> "Fan";
                case 3 -> "Speaker";
                default -> { System.out.println(RED + "⚠️ Wrong choice!" + RESET); yield null; }
            };
        }

        String brand = null;
        // Loop until a valid brand is selected.
        while (brand == null) {
            System.out.println("Select Brand: 1. LG  2. Haier  3. Sony");
            int choice = readIntInput("Choice: ");
            brand = switch (choice) {
                case 1 -> "LG";
                case 2 -> "Haier";
                case 3 -> "Sony";
                default -> { System.out.println(RED + "⚠️ Wrong choice!" + RESET); yield null; }
            };
        }

        System.out.print("Enter device name: ");
        String name = scanner.nextLine().trim();

        System.out.println(GREEN + "Registering device..." + RESET);
        controller.saveDevice(type, brand, name);
        sleep(1000);
    }

    /** Handles the deregistration of an existing device based on user input. */
    private void deregisterDevice() {
        System.out.print("Device ID to remove: ");
        String id = scanner.nextLine().trim();
        System.out.println(YELLOW + "Removing device " + id + "..." + RESET);
        controller.deleteDevice(id);
        sleep(1000);
    }

    /** Handles toggling a device's power state (ON/OFF) based on user input. */
    private void toggleDevice() {
        System.out.print("Device ID to toggle: ");
        String id = scanner.nextLine().trim();
        System.out.println(YELLOW + "Toggling device " + id + "..." + RESET);
        controller.toggleOnOff(id);
        sleep(1000);
    }

    /** Handles simulating a device's action based on user input. */
    private void simulateDevice() {
        System.out.print("Device ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Simulation level: ");
        String level = scanner.nextLine().trim();
        System.out.println(YELLOW + "Simulating device " + id + "..." + RESET);
        controller.simulate(id, level);
        sleep(1000);
    }

    // --- Network Menu Logic ---

    /**
     * Displays the network menu and handles user input for
     * connecting or disconnecting devices.
     */
    private void networkMenu() {
        while (true) {
            clearScreen();
            printMenuHeader("NETWORK TOPOLOGY");
            controller.showTopology();
            System.out.println(
                    CYAN + " [1] Connect Device " + RESET + YELLOW + "- Bring device online" + RESET + "\n" +
                            CYAN + " [2] Disconnect Device " + RESET + YELLOW + "- Take device offline" + RESET + "\n" +
                            CYAN + " [0] Back " + RESET + YELLOW + "- Return to main menu" + RESET
            );
            int choice = readIntInput(BOLD + BLUE + "Enter your choice: " + RESET);
            switch (choice) {
                case 1 -> {
                    System.out.print("Device ID to connect: ");
                    controller.connectDevice(scanner.nextLine().trim());
                }
                case 2 -> {
                    System.out.print("Device ID to disconnect: ");
                    controller.disconnectDevice(scanner.nextLine().trim());
                }
                case 0 -> { return; }
                default -> {
                    System.out.println(RED + "⚠️ Invalid choice!" + RESET);
                    sleep(1500);
                }
            }
        }
    }

    // --- Sensor Readings Menu Logic ---

    /**
     * Displays the sensor readings menu and handles user input for
     * fetching historical sensor data.
     */
    private void sensorMenu() {
        while (true) {
            clearScreen();
            printMenuHeader("SENSOR READINGS");
            System.out.println(
                    CYAN + " [1] By Device " + RESET + YELLOW + "- See readings from a device" + RESET + "\n" +
                            CYAN + " [2] By Date Range " + RESET + YELLOW + "- See readings over time" + RESET + "\n" +
                            CYAN + " [0] Back " + RESET + YELLOW + "- Return to main menu" + RESET
            );

            System.out.print(BOLD + BLUE + "Enter your choice: " + RESET);
            String choiceInput = scanner.nextLine().trim(); // Use nextLine().trim() to clear buffer
            int choice;
            try {
                choice = Integer.parseInt(choiceInput);
            } catch (NumberFormatException e) {
                System.out.println(RED + "⚠️ Invalid input, please enter a number." + RESET);
                sleep(1000);
                continue; // Skip switch and loop again
            }
            // --- END OF FIX ---

            switch (choice) {
                case 1 -> {
                    System.out.print("Device ID: ");
                    String id = scanner.nextLine().trim();
                    System.out.print("Limit (1-10): ");
                    String limit = scanner.nextLine().trim();
                    controller.showReadingsByDevice(id, limit);
                    sleep(500);
                }
                case 2 -> {
                    System.out.print("Start date (YYYY-MM-DD): ");
                    String start = scanner.nextLine().trim();
                    System.out.print("End date (YYYY-MM-DD): ");
                    String end = scanner.nextLine().trim();

                    controller.showReadingsByDateRange(start, end);
                    sleep(500);
                }
                case 0 -> { return; }
                default -> {
                    System.out.println(RED + "⚠️ Invalid choice!" + RESET);
                    sleep(1500);
                }
            }
        }
    }

    // --- Dashboard and Exit Logic ---

    /**
     * Launches the real-time monitoring dashboard by starting the
     * sensor data stream. The stream continues until the user presses Enter.
     */
    private void launchDashboard() {
        clearScreen();
        printMenuHeader("LIVE DASHBOARD");
        System.out.println(YELLOW + BOLD + "Press Enter to stop real-time monitoring..." + RESET);
        controller.startDashboard();
        // The program blocks here, waiting for the user to press Enter.
        scanner.nextLine();
        controller.stopDashboard();
        System.out.println(GREEN + "Dashboard stopped" + RESET);
        sleep(1000);
    }

    /**
     * Shuts down the application gracefully by calling the controller's shutdown method.
     */
    private void exitApplication() {
        clearScreen();
        System.out.println(BLUE + BOLD + "Shutting down Smart Home system..." + RESET);
        sleep(500);
        // Calls the shutdown method to close connections and save data.
        AppConfig.init().shutdown();
        System.out.println(GREEN + "Goodbye!" + RESET);
    }

    // --- Helper Methods ---

    /**
     * A helper method to print styled menu headers.
     * @param title The title of the menu.
     */
    private void printMenuHeader(String title) {
        String border = "=".repeat(title.length() + 10);
        System.out.println(CYAN + "+" + border + "+" + RESET);
        System.out.println(CYAN + "|" + RESET + BOLD + YELLOW + "     " + title + "     " + RESET + CYAN + "|" + RESET);
        System.out.println(CYAN + "+" + border + "+" + RESET);
    }

    /**
     * Clears the console screen by printing a large number of newlines.
     */
    private void clearScreen() {
        System.out.println("\n".repeat(50));
    }

    /**
     * A helper method to pause the application's execution.
     * @param ms The number of milliseconds to sleep.
     */
    private void sleep(long ms) {
        try { TimeUnit.MILLISECONDS.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /**
     * Prompts the user for an integer input and handles invalid input
     * @param prompt The message to display to the user.
     * @return The integer entered by the user, or -1 if the input is invalid.
     */
    private int readIntInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println(RED + "⚠️ Invalid input, please enter a number." + RESET);
            sleep(1000);
            return -1;
        }
    }
}