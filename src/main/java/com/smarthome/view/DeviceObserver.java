package com.smarthome.view;

import com.smarthome.observer.Observer;
import static com.smarthome.util.ConsoleColorsUtil.*;

public class DeviceObserver implements Observer {

    @Override
    public void update(String message) {
        System.out.println(YELLOW + BOLD + "[Device Observer] " + RESET + message);
    }
}
