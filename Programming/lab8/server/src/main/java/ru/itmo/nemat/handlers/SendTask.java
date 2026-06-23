package ru.itmo.nemat.handlers;

import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.utils.Serializer;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendTask implements Runnable {
    Response response;
    Socket socket;
    Serializer serializer;
    private static final Logger logger = Logger.getLogger(SendTask.class.getName());

    public SendTask(Response response, Socket socket, Serializer serializer) {
        this.response = response;
        this.socket = socket;
        this.serializer = serializer;
    }

    @Override
    public void run() {
        try {
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                logger.info("Отправка ответа на " + socket.getRemoteSocketAddress());
                byte[] responseBytes = serializer.serialize(response);
                out.writeInt(responseBytes.length);
                out.write(responseBytes);
                out.flush();

                logger.info("Ответ отправлен клиенту. Статус: " + response.getStatus());

        } catch (EOFException e) {
            logger.warning("Клиент закрыл соединение");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Ошибка ввода-вывода при работе с клиентом " + socket.getRemoteSocketAddress(), e);        }
    }
}
