package ru.itmo.nemat.handler;

import ru.itmo.nemat.commands.ClientCommand;
import ru.itmo.nemat.exceptions.InvalidCommandArgumentException;
import ru.itmo.nemat.exceptions.ServerUnavailableException;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.managers.ClientCollectionManager;
import ru.itmo.nemat.managers.ClientCommandManager;
import ru.itmo.nemat.managers.InteractionManager;
import ru.itmo.nemat.models.Dragon;
import ru.itmo.nemat.models.ResponseStatus;
import ru.itmo.nemat.utils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class UIHandler {
    private final ClientCommandManager commandManager;
    private final InteractionManager interactionManager;
    private final ClientCollectionManager collectionManager;

    private ServerListener serverListener;

    private final SessionHandler sessionHandler;
    private final UIPrinter printer;




    public UIHandler(ClientCommandManager commandManager, ClientCollectionManager collectionManager, InteractionManager interactionManager, SessionHandler sessionHandler,  UIPrinter printer) {
        this.commandManager = commandManager;
        this.interactionManager = interactionManager;
        this.sessionHandler = sessionHandler;
        this.collectionManager = collectionManager;
        this.printer = printer;
    }
    public ServerListener getServerListener() {
        return serverListener;
    }

    public void setServerListener(ServerListener serverListener) {
        this.serverListener = serverListener;
    }

    public void loginHandler(String login, String password) throws Exception {
        String[] creds = AuthFactory.getInstance().buildFromStrings(login, password);

        authenticate("login", creds[0], creds[1]);

    }

    public void stopNetworking() {
        if (serverListener != null) {
            serverListener.stopListener();
        }
        interactionManager.stop();
    }

    public ClientCommandManager  getCommandManager() {
        return commandManager;
    }
    public InteractionManager getInteractionManager() {
        return interactionManager;
    }

    public void registerHandler(String login, String password, String confirmPassword) throws Exception {
        String[] creds = AuthFactory.getInstance().buildRegistrationFromStrings(login, password, confirmPassword);

        authenticate("register", creds[0], creds[1]);
    }

    private void authenticate(String commandName, String login, String password) throws Exception {
        ClientCommand command = commandManager.getCommand(commandName);

        Request request = command.apply(new String[0], login, password, () -> null).get();

        Response response = interactionManager.sendAndReceive(request);

        if (response.isSuccess()) {
            sessionHandler.setSession(login, password);


            ClientCommand showCmd = commandManager.getCommand("show");
            Request showReq = showCmd.apply(new String[0], login, password, () -> null).get();
            Response showResponse = interactionManager.sendAndReceive(showReq);
            if(showResponse.isSuccess()) {
                collectionManager.setDragons((List<Dragon>) showResponse.getDragons());

            }
            ServerListener listener = new ServerListener(interactionManager.getTcpClient(), collectionManager, interactionManager.getSerializer(), printer);
            this.serverListener = listener;
            Thread listenerThread = new Thread(listener);
            listenerThread.setDaemon(true);
            listenerThread.start();
        } else {
            throw new Exception(response.getMessage());
        }
    }

    public SessionHandler getSessionHandler() {
        return sessionHandler;
    }

    public ClientCollectionManager getCollectionManager() {
        return collectionManager;
    }

    public void apply(String commandName, String[] args, Supplier<DragonDTO> dragonSupplier) throws Exception {
        ClientCommand command = commandManager.getCommand(commandName);
        if (command == null)throw new Exception("ui.error.command_not_found_local");

        Optional<Request> request = commandManager.apply(commandName, args, sessionHandler.getLogin(), sessionHandler.getPassword(), dragonSupplier);

        request.ifPresentOrElse(interactionManager::send, () -> GuiOutputPrinter.getInstance().getAndClear(commandName));
    }
}
