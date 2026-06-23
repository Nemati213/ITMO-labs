package ru.itmo.nemat.interaction;

import ru.itmo.nemat.utils.DragonDTO;

import java.io.Serializable;

public class Request implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String commandName;
    private final String[] args;
    private final DragonDTO dragon;
    private final String login;
    private final String password;
    //private final long requestId;

    public static class Builder{
        private String commandName;
        private String[] args = new String[0];
        private DragonDTO dragon;
        private String login;
        private String password;
        //private long requestId;

        public Builder commandName(String commandName){
            this.commandName = commandName;
            return this;
        }

//        public Builder requestId(long requestId){
//            this.requestId = requestId;
//            return this;
//        }

        public Builder args(String[] args){
            this.args = args;
            return this;
        }

        public Builder dragon(DragonDTO dragon){
            this.dragon = dragon;
            return this;
        }

        public Builder login(String login){
            this.login = login;
            return this;
        }

        public Builder password(String password){
            this.password = password;
            return this;
        }

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
        //this.requestId = requestId;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }

    public DragonDTO getDragon() {
        return dragon;
    }

    public String getLogin() { return login; }

    public String getPassword() { return password; }

    @Override
    public String toString() {
        return "Request{" +
                "commandName='" + commandName + '\'' +
                '}';
    }

}
