package ru.itmo.nemat.lab6.server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class TCPServer {
    private final int port;
    private ServerSocket serverSocket;

    public TCPServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(100);
    }

    public Socket acceptConnection() throws IOException {
        try {
            return serverSocket.accept();
        } catch (SocketTimeoutException e) {
            return null;
        }
    }

    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}
    }
}