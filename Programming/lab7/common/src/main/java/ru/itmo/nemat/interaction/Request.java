package ru.itmo.nemat.interaction;



import ru.itmo.nemat.utils.DragonDTO;

import java.io.Serializable;

/**
 * The type Request.
 */
public class Request implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String commandName;
    private final String[] args;
    private final DragonDTO dragon;
    private final String login;
    private final String password;

    /**
     * The type Builder.
     */
    public static class Builder{
        private String commandName;
        private String[] args = new String[0];
        private DragonDTO dragon;
        private String login;
        private String password;

        /**
         * Command name builder.
         *
         * @param commandName the command name
         * @return the builder
         */
        public Builder commandName(String commandName){
            this.commandName = commandName;
            return this;
        }

        /**
         * Args builder.
         *
         * @param args the args
         * @return the builder
         */
        public Builder args(String[] args){
            this.args = args;
            return this;
        }

        /**
         * Dragon builder.
         *
         * @param dragon the dragon
         * @return the builder
         */
        public Builder dragon(DragonDTO dragon){
            this.dragon = dragon;
            return this;
        }

        /**
         * Login builder.
         *
         * @param login the login
         * @return the builder
         */
        public Builder login(String login){
            this.login = login;
            return this;
        }

        /**
         * Password builder.
         *
         * @param password the password
         * @return the builder
         */
        public Builder password(String password){
            this.password = password;
            return this;
        }

        /**
         * Build request.
         *
         * @return the request
         */
        public Request build(){
            if (commandName == null || login.isBlank() || password.isBlank()) throw new IllegalStateException("Команда, логин и пароль не могут быть пустыми!");
            return new Request(commandName, args, dragon, login, password);
        }
    }

    private Request(String commandName, String[] args, DragonDTO dragon,  String login, String password) {
        this.commandName = commandName;
        this.args = args;
        this.dragon = dragon;
        this.login = login;
        this.password = password;
    }

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder(){
        return new Builder();
    }

    /**
     * Gets command name.
     *
     * @return the command name
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Get args string [ ].
     *
     * @return the string [ ]
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Gets dragon.
     *
     * @return the dragon
     */
    public DragonDTO getDragon() {
        return dragon;
    }

    /**
     * Gets login.
     *
     * @return the login
     */
    public String getLogin() { return login; }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() { return password; }

    @Override
    public String toString() {
        return "Request{" +
                "commandName='" + commandName + '\'' +
                '}';
    }

}
