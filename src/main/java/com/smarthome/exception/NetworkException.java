package com.smarthome.exception;

/**
 * Exception type for network-related errors.
 * <p>
 * Used to signal failures in connectivity, communication,
 * or remote interactions between devices and services.
 */
public class NetworkException extends ApplicationException {

    /**
     * Creates a new network exception with a detail message.
     *
     * @param message the detail message
     */
    public NetworkException(String message) {
        super(message);
    }

    /**
     * Creates a new network exception with a detail message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause of the exception
     */
    public NetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
