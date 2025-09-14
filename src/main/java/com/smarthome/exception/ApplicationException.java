package com.smarthome.exception;

/**
 * Base exception type for application-level errors.
 * <p>
 * Extends {@link RuntimeException} so it can be thrown
 * without being declared in method signatures.
 */
public class ApplicationException extends RuntimeException {

    /**
     * Creates a new exception with a detail message.
     *
     * @param message the detail message
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with a detail message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause of the exception
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
