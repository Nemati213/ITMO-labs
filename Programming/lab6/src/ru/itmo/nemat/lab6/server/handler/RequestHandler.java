package ru.itmo.nemat.lab6.server.network;

import ru.itmo.nemat.lab6.common.interaction.Request;
import ru.itmo.nemat.lab6.common.interaction.Response;
import ru.itmo.nemat.lab6.common.utils.Serializer;
import ru.itmo.nemat.lab6.server.managers.ServerCommandManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler {

    private final Socket socket;
    private final ServerCommandManager commandManager;
    private final Serializer serializer;
    private static final Logger logger = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket socket, ServerCommandManager commandManager, Serializer serializer) {
        this.socket = socket;
        this.commandManager = commandManager;
        this.serializer = serializer;
    }

    public void handle() {
        try (Socket s = socket;
             DataInputStream in = new DataInputStream(s.getInputStream());
             DataOutputStream out = new DataOutputStream(s.getOutputStream())) {

            int length = in.readInt();
            byte[] requestBytes = new byte[length];
            in.readFully(requestBytes);

            Request request = serializer.deserialize(requestBytes);
            logger.info("Получен запрос: " + request.getCommandName());

            Response response = commandManager.execute(request);

            byte[] responseBytes = serializer.serialize(response);
            out.writeInt(responseBytes.length);
            out.write(responseBytes);
            out.flush();

            logger.info("Ответ отправлен клиенту. Статус: " + response.isSuccess());

        } catch (EOFException e) {
            logger.warning("Клиент закрыл соединение");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка ввода-вывода при работе с клиентом", e);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Ошибка десериализации (несовпадение версий классов)", e);
        }
    }
}