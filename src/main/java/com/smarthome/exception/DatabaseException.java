package com.smarthome.exception;

/**
 * Exception type for database-related errors.
 * <p>
 * Extends {@link ApplicationException} to distinguish
 * persistence issues from other application-level exceptions.
 */
public class DatabaseException extends ApplicationException {

    /**
     * Creates a new database exception with a detail message.
     *
     * @param message the detail message
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Creates a new database exception with a detail message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause of the exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
