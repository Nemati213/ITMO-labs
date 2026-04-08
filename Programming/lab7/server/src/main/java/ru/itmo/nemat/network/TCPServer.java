package ru.itmo.nemat.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * The type Tcp server.
 */
public class TCPServer {
    private final int port;
    private ServerSocket serverSocket;

    /**
     * Instantiates a new Tcp server.
     *
     * @param port the port
     */
    public TCPServer(int port) {
        this.port = port;
    }

    /**
     * Start.
     *
     * @throws IOException the io exception
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(100);
    }

    /**
     * Accept connection socket.
     *
     * @return the socket
     * @throws IOException the io exception
     */
    public Socket acceptConnection() throws IOException {
        try {
            return serverSocket.accept();
        } catch (SocketTimeoutException e) {
            return null;
        }
    }

    /**
     * Stop.
     */
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}
    }
}