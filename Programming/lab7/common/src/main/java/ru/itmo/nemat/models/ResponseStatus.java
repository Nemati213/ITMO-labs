package ru.itmo.nemat.models;

/**
 * The enum Response status.
 */
public enum ResponseStatus {
    /**
     * Ok response status.
     */
    OK,
    /**
     * Auth success response status.
     */
    AUTH_SUCCESS,
    /**
     * Auth error response status.
     */
    AUTH_ERROR,
    /**
     * Registration error response status.
     */
    REGISTRATION_ERROR,
    /**
     * Validation error response status.
     */
    VALIDATION_ERROR,
    /**
     * Permission denied response status.
     */
    PERMISSION_DENIED,
    /**
     * Error response status.
     */
    ERROR,
    /**
     * Not found response status.
     */
    NOT_FOUND;
}
