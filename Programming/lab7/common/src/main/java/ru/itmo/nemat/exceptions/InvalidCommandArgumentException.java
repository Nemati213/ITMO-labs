package ru.itmo.nemat.exceptions;

/**
 * The type Invalid command argument exception.
 */
public class InvalidCommandArgumentException extends Exception {

    /**
     * Instantiates a new Invalid command argument exception.
     *
     * @param message the message
     */
    public InvalidCommandArgumentException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Invalid command argument exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public InvalidCommandArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}