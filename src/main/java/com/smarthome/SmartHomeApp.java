package com.smarthome;

import com.smarthome.config.AppConfig;

public class SmartHomeApp {
    public static void main(String[] args) {
        AppConfig.init().getConsoleMenu().showMainMenu();
    }
}