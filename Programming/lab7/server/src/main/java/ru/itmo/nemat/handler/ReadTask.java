package ru.itmo.nemat.handler;

import ru.itmo.nemat.database.DragonDAO;
import ru.itmo.nemat.interaction.Request;
import ru.itmo.nemat.managers.CollectionManager;
import ru.itmo.nemat.managers.DatabaseManager;
import ru.itmo.nemat.managers.ServerCommandManager;
import ru.itmo.nemat.utils.AuthService;
import ru.itmo.nemat.utils.Serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type Read task.
 */
public class ReadTask implements Runnable {

    private final Socket socket;
    private final ServerCommandManager commandManager;
    private final Serializer serializer;
    private final ExecutorService processPool;
    private final AuthService authService;
    private final ExecutorService sendPool;
    private final DatabaseManager databaseManager;
    private final DragonDAO dragonDAO;
    private final CollectionManager collectionManager;

    private static final Logger logger = Logger.getLogger(ReadTask.class.getName());


    /**
     * Instantiates a new Read task.
     *
     * @param socket            the socket
     * @param commandManager    the command manager
     * @param serializer        the serializer
     * @param processPool       the process pool
     * @param authService       the auth service
     * @param sendPool          the send pool
     * @param databaseManager   the database manager
     * @param dragonDAO         the dragon dao
     * @param collectionManager the collection manager
     */
    public ReadTask(Socket socket, ServerCommandManager commandManager, Serializer serializer, ExecutorService processPool, AuthService authService, ExecutorService sendPool, DatabaseManager databaseManager, DragonDAO dragonDAO, CollectionManager collectionManager) {
        this.socket = socket;
        this.commandManager = commandManager;
        this.serializer = serializer;
        this.processPool = processPool;
        this.authService = authService;
        this.sendPool = sendPool;
        this.databaseManager = databaseManager;
        this.dragonDAO = dragonDAO;
        this.collectionManager = collectionManager;
    }

    @Override
    public void run() {
        try {
            logger.info("Начало обработки запроса от: " + socket.getRemoteSocketAddress());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            int length = in.readInt();
            byte[] requestBytes = new byte[length];
            in.readFully(requestBytes);

            Request request = serializer.deserialize(requestBytes);
            logger.info("Получен запрос: " + request.getCommandName());

            ProcessTask processTask = new ProcessTask(request, socket, commandManager, authService, sendPool, serializer, databaseManager, dragonDAO, collectionManager);
            processPool.submit(processTask);
            logger.info("Задача на выполнение команды '" + request.getCommandName() + "' успешно поставлена в очередь (processPool).");

        } catch (EOFException e) {
            logger.warning("Клиент закрыл соединение");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка ввода-вывода при работе с клиентом", e);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Ошибка десериализации (несовпадение версий классов)", e);
        }
    }
}
