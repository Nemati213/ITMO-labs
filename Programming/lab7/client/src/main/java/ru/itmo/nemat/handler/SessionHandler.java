package ru.itmo.nemat.handler;

import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.models.DragonCharacter;

/**
 * The type Session handler.
 */
public class SessionHandler {

    private String login;
    private String password;

    /**
     * Instantiates a new Session handler.
     */
    public SessionHandler() {
        this.login = null;
        this.password = null;
    }

    /**
     * Gets login.
     *
     * @return the login
     */
    public String getLogin() {
        return login;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Log out.
     */
    public void logOut() {
        this.login = null;
        this.password = null;
    }

    /**
     * Sets session.
     *
     * @param login    the login
     * @param password the password
     */
    public void setSession(String login, String password) {
        this.login = login;
        this.password = password;
    }

    /**
     * Is logged in boolean.
     *
     * @return the boolean
     */
    public boolean isLoggedIn(){
        return login != null && password != null;
    }

}
