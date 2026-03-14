package ru.itmo.nemat.lab6.client.network;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TCPClient {
    private final SocketAddress address;
    private SocketChannel channel;

    public TCPClient(SocketAddress address) {
        this.address = address;
    }

    public void connect() throws IOException {
        if (channel != null && channel.isConnected()) return;
        channel = SocketChannel.open();
        channel.configureBlocking(false);
        channel.connect(address);

        long startTime = System.currentTimeMillis();
        while (!channel.finishConnect()) {
            if (System.currentTimeMillis() - startTime > 5000) {
                channel.close();
                throw new IOException("Время подключения к серверу истекло");
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void send(byte[] data) throws IOException {

        ByteBuffer buffer = ByteBuffer.allocate(data.length + 4);
        buffer.putInt(data.length);
        buffer.put(data);
        buffer.flip();
        while (buffer.hasRemaining()) channel.write(buffer);
    }

    public byte[] receive() throws IOException {
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        readIntoBuffer(lengthBuffer);
        lengthBuffer.flip();
        int payloadLength = lengthBuffer.getInt();

        ByteBuffer payloadBuffer = ByteBuffer.allocate(payloadLength);
        readIntoBuffer(payloadBuffer);

        return payloadBuffer.array();
    }

    private void readIntoBuffer(ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            int read = channel.read(buffer);
            if (read == -1) throw new IOException("Соединение разорвано");
            if (read == 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Чтение прервано");
                }
            }
        }
    }

    public void disconnect() {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
        } catch (IOException ignored) {}
    }
}
