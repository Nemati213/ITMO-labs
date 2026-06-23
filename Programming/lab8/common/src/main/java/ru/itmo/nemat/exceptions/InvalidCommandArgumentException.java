package ru.itmo.nemat.exceptions;

public class InvalidCommandArgumentException extends Exception {

    public InvalidCommandArgumentException(String message) {
        super(message);
    }

    public InvalidCommandArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}