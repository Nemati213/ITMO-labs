package ru.itmo.nemat.exceptions;

public class InvalidLoginArgumentException extends IllegalArgumentException {
    public InvalidLoginArgumentException(String message) {
        super(message);
    }
}
