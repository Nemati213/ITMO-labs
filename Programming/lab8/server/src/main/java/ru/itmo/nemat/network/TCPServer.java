package ru.itmo.nemat.network;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.util.Properties;

public class TCPServer {
    private ServerSocket serverSocket;

    private static final String PROPERTIES_PATH = "server.properties";
    private static final String KEYSTORE_PATH = "/keystore.p12";

    private static final String PASSWORD_KEY = "ssl.keystore.password";
    private static final String PORT_KEY = "server.port";

    public TCPServer() {
    }

    public void start() throws IOException {
        try {
            Properties props = new Properties();
            try (InputStream in = new FileInputStream(PROPERTIES_PATH)) {
                props.load(in);
            }

            int port = Integer.parseInt(props.getProperty(PORT_KEY));
            char[] password = props.getProperty(PASSWORD_KEY).toCharArray();

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream in = getClass().getResourceAsStream(KEYSTORE_PATH)) {
                if (in == null)
                    throw new IOException("Файл " + KEYSTORE_PATH + " не найден в ресурсах!");
                keyStore.load(in, password);
            }

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(keyStore, password);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);

            SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
            serverSocket = ssf.createServerSocket(port);
            serverSocket.setSoTimeout(100);

            System.out.println("SSL Server started on port: " + port);

        } catch (Exception e) {
            throw new IOException("Ошибка инициализации SSL сервера: " + e.getMessage(), e);
        }
    }

    public Socket acceptConnection() throws IOException {
        try {
            Socket socket = serverSocket.accept();
            if (socket instanceof SSLSocket) {
                ((SSLSocket) socket).startHandshake();
            }
            return socket;
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