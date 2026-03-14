package ru.itmo.nemat.lab6.common.exceptions;

public class InvalidCommandArgumentException extends Exception {

    public InvalidCommandArgumentException(String message) {
        super(message);
    }

    public InvalidCommandArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}