package com.smarthome;

import com.smarthome.config.AppConfig;

public class SmartHomeApp {

    public static void main(String[] args) {
        // Initialize application configuration, set up dependencies,
        // and launch the console-based smart home dashboard main menu..
        AppConfig.init().getConsoleMenu().showMainMenu();
    }
}
