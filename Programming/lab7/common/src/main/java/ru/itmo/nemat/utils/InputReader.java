package ru.itmo.nemat.utils;

import java.io.IOException;

/**
 * The interface Input reader.
 */
public interface InputReader {

    /**
     * Read line string.
     *
     * @return the string
     */
    public String readLine();

    /**
     * Has next line boolean.
     *
     * @return the boolean
     */
    boolean hasNextLine();

    /**
     * Is interactive boolean.
     *
     * @return the boolean
     */
    boolean isInteractive();

    /**
     * Read password string.
     *
     * @return the string
     */
    public String readPassword();
}