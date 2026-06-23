package ru.itmo.nemat;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ru.itmo.nemat.commands.*;
import ru.itmo.nemat.controllers.AuthController;
import ru.itmo.nemat.handler.SessionHandler;
import ru.itmo.nemat.handler.UIHandler;
import ru.itmo.nemat.managers.ClientCollectionManager;
import ru.itmo.nemat.managers.ClientCommandManager;
import ru.itmo.nemat.managers.InteractionManager;
import ru.itmo.nemat.utils.*;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Validator validator = new Validator();

        Serializer serializer = new Serializer();

        UIPrinter uiPrinter = new UIPrinter();

        InteractionManager interactionManager = new InteractionManager("localhost", 8888, serializer);

        SessionHandler sessionHandler = new SessionHandler();
        ClientCommandManager commandManager = new ClientCommandManager(uiPrinter);
        ClientCollectionManager clientCollectionManager = new ClientCollectionManager();
        commandManager.register(new LoginCommand());
        commandManager.register(new RegisterCommand());
        commandManager.register(new ShowClientCommand());
        commandManager.register(new AddClientCommand());
        commandManager.register(new RemoveLastClientCommand());
        commandManager.register(new ClearClientCommand());
        commandManager.register(new HelpClientCommand(commandManager, GuiOutputPrinter.getInstance()));
        commandManager.register(new InfoClientCommand());
        commandManager.register(new HistoryClientCommand(commandManager,  GuiOutputPrinter.getInstance()));
        commandManager.register(new RemoveAllByCharacterClientCommand());
        commandManager.register(new RemoveByIdClientCommand());
        commandManager.register(new UpdateClientCommand());
        commandManager.register(new PrintAscendingClientCommand());
        commandManager.register(new ExitClientCommand());
        commandManager.register(new SortClientCommand());
        commandManager.register(new PrintFieldDescendingAgeClientCommand());


        UIHandler uiHandler = new UIHandler(commandManager,clientCollectionManager, interactionManager, sessionHandler, uiPrinter);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        AuthController authController = loader.getController();
        authController.setUiHandler(uiHandler);
        stage.setScene(new Scene(root));
        stage.show();
    }
}