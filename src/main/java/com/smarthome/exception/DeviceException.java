package com.smarthome.exception;

/**
 * Exception type for device-related errors.
 * <p>
 * Used to signal failures when interacting with or operating
 * smart home devices.
 */
public class DeviceException extends ApplicationException {

    /**
     * Creates a new device exception with a detail message.
     *
     * @param message the detail message
     */
    public DeviceException(String message) {
        super(message);
    }

    /**
     * Creates a new device exception with a detail message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause of the exception
     */
    public DeviceException(String message, Throwable cause) {
        super(message, cause);
    }
}
