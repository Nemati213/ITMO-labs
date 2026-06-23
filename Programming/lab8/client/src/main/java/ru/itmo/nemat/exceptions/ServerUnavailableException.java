package ru.itmo.nemat.exceptions;

public class ServerUnavailableException extends RuntimeException{
    public ServerUnavailableException(String message) {
        super(message);
    }

}
