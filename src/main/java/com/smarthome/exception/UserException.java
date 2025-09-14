package com.smarthome.exception;

/**
 * Exception type for user-related errors.
 * <p>
 * Used to represent problems such as invalid input,
 * unauthorized actions, or user state conflicts.
 */
public class UserException extends ApplicationException {

    /**
     * Creates a new user exception with a detail message.
     *
     * @param message the detail message
     */
    public UserException(String message) {
        super(message);
    }

    /**
     * Creates a new user exception with a detail message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause of the exception
     */
    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}
