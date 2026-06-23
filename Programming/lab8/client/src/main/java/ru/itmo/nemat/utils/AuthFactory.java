package ru.itmo.nemat.utils;

import java.util.function.Supplier;

public class AuthFactory {
    private static AuthFactory instance;
    private AuthFactory() {
    }

    public static AuthFactory getInstance() {
        if (instance == null) {
            instance = new AuthFactory();
        }
        return instance;
    }

    public String[] buildFromStrings(String login, String password) {
        Validator.validateLogin(login, password);
        return new String[]{login, password};
    }

    public String[] buildRegistrationFromStrings(String login, String password, String confirmPassword) {

        Validator.validateRegister(login, password, confirmPassword);
        return new String[]{login, password};
    }


    public String[] buildFromSequence(Supplier<String> lineReader) {
        String login = lineReader.get();
        String password = lineReader.get();
        Validator.validateLogin(login, password);
        return new String[]{login, password};
    }

    public String[] buildRegistrationFromSequence(Supplier<String> lineReader) {
        String login = lineReader.get();
        String password = lineReader.get();
        String confirmPassword = lineReader.get();
        Validator.validateRegister(login, password, confirmPassword);
        return new String[]{login, password, confirmPassword};
    }
}