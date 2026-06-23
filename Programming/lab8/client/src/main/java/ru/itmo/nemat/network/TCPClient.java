package ru.itmo.nemat.network;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;
import java.util.Properties;

public class TCPClient {

    private static final String PROPERTIES_PATH = "/connection/client.properties";
    private static final String TRUSTSTORE_PATH = "/connection/truststore.p12";
    private static final String PASSWORD_KEY = "ssl.truststore.password";
    private static final int MAX_MESSAGE_SIZE = 16 * 1024 * 1024;

    private SSLSocket socket;
    private DataInputStream input;
    private DataOutputStream output;

    private final String host;
    private final int port;
    private SSLContext sslContext;

    public TCPClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void ensureSSLContextInitialized() throws Exception {
        if (this.sslContext != null) return;

        Properties props = new Properties();
        try (InputStream in = getClass().getResourceAsStream(PROPERTIES_PATH)) {
            if (in == null) throw new FileNotFoundException("ui.error.client.properties_not_found:" + PROPERTIES_PATH);
            props.load(in);
        }

        String rawPassword = props.getProperty(PASSWORD_KEY);
        if (rawPassword == null) throw new IllegalStateException("ui.error.client.password_missing");
        char[] password = rawPassword.toCharArray();

        KeyStore ks = KeyStore.getInstance("PKCS12");
        try (InputStream tsIn = getClass().getResourceAsStream(TRUSTSTORE_PATH)) {
            if (tsIn == null) throw new FileNotFoundException("ui.error.client.truststore_not_found:" + TRUSTSTORE_PATH);
            ks.load(tsIn, password);
        }

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);

        this.sslContext = SSLContext.getInstance("TLS");
        this.sslContext.init(null, tmf.getTrustManagers(), null);
    }

    public void connect() throws IOException {
        try {
            ensureSSLContextInitialized();

            SSLSocketFactory factory = sslContext.getSocketFactory();
            this.socket = (SSLSocket) factory.createSocket(host, port);
            this.socket.startHandshake();

            this.output = new DataOutputStream(socket.getOutputStream());
            this.input = new DataInputStream(socket.getInputStream());
        } catch (Exception ex) {
            disconnect();
            ex.printStackTrace();
            throw new IOException("ui.error.client.connection_failed:" + ex.getMessage(), ex);
        }
    }

    public void send(byte[] data) throws IOException {
        if (output == null) throw new IOException("ui.error.client.not_connected");
        output.writeInt(data.length);
        output.write(data);
        output.flush();
    }

    public byte[] receive() throws IOException {
        if (input == null) throw new IOException("ui.error.client.not_connected");
        int length = input.readInt();
        if (length < 0 || length > MAX_MESSAGE_SIZE) {
            throw new IOException("ui.error.client.illegal_length:" + length);
        }
        byte[] data = new byte[length];
        input.readFully(data);
        return data;
    }

    public void disconnect() {
        try {
            if (input != null) input.close();
        } catch (IOException ignored) {}
        try {
            if (output != null) output.close();
        } catch (IOException ignored) {}
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
        this.input = null;
        this.output = null;
        this.socket = null;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}