package ru.itmo.nemat.managers;

import ru.itmo.nemat.handlers.SendTask;
import ru.itmo.nemat.interaction.Response;
import ru.itmo.nemat.utils.Serializer;

import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class ClientManager {
    private final ExecutorService sendPool;
    private final Serializer serializer;

    private final Set<Socket> activeClients = ConcurrentHashMap.newKeySet();

    public ClientManager(ExecutorService sendPool, Serializer serializer) {
        this.sendPool = sendPool;
        this.serializer = serializer;
    }

    public void addClient(Socket s) {
        activeClients.add(s);
    }

    public void removeClient(Socket s) {
        activeClients.remove(s);
    }

    public void broadcast(Response response) {
        for (Socket socket : activeClients) {
            if (!socket.isClosed())
                sendPool.submit(new SendTask(response, socket, serializer));
            else
                removeClient(socket);


        }
    }
}
