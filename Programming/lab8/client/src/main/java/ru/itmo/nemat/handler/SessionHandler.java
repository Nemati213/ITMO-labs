package ru.itmo.nemat.handler;

import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.models.DragonCharacter;

public class SessionHandler {

    private String login;
    private String password;

    public SessionHandler() {
        this.login = null;
        this.password = null;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void logOut() {
        this.login = null;
        this.password = null;
    }

    public void clear() {
        this.login = "";
        this.password = "";
    }

    public void setSession(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public boolean isLoggedIn(){
        return login != null && password != null;
    }

}
