package ru.itmo.nemat.handlers;

import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.managers.ClientManager;
import ru.itmo.nemat.managers.DatabaseManager;
import ru.itmo.nemat.managers.ServerCommandManager;
import ru.itmo.nemat.services.DragonService;
import ru.itmo.nemat.services.UserService;
import ru.itmo.nemat.utils.Serializer;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadTask implements Runnable {

    private final Socket socket;
    private final ServerCommandManager commandManager;
    private final Serializer serializer;
    private final ExecutorService processPool;
    private final UserService userService;
    private final ExecutorService sendPool;
    private final DatabaseManager databaseManager;
    private final DragonService dragonService;
    private final ClientManager clientManager;

    private static final Logger logger = Logger.getLogger(ReadTask.class.getName());


    public ReadTask(Socket socket, ServerCommandManager commandManager, Serializer serializer, ExecutorService processPool, UserService userService, ExecutorService sendPool, DatabaseManager databaseManager, DragonService dragonService, ClientManager clientManager) {
        this.socket = socket;
        this.commandManager = commandManager;
        this.serializer = serializer;
        this.processPool = processPool;
        this.userService = userService;
        this.sendPool = sendPool;
        this.databaseManager = databaseManager;
        this.dragonService = dragonService;
        this.clientManager = clientManager;
    }

    @Override
    public void run() {

        try(DataInputStream in = new DataInputStream(socket.getInputStream())) {
            logger.info("Начало обработки запроса от: " + socket.getRemoteSocketAddress());
            while(!socket.isClosed()) {
                int length = in.readInt();
                byte[] requestBytes = new byte[length];
                in.readFully(requestBytes);
                Request request = serializer.deserialize(requestBytes);
                logger.info("Получен запрос: " + request.getCommandName());

                ProcessTask processTask = new ProcessTask(request, socket, commandManager, userService, sendPool, serializer, databaseManager, dragonService);
                processPool.submit(processTask);
                logger.info("Задача на выполнение команды '" + request.getCommandName() + "' успешно поставлена в очередь (processPool).");
            }
        } catch (EOFException e) {
            logger.warning("Клиент закрыл соединение");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка ввода-вывода при работе с клиентом", e);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Ошибка десериализации (несовпадение версий классов)", e);
        }

    }
}
