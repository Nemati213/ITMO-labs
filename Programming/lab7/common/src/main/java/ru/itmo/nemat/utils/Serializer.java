package ru.itmo.nemat.utils;

import java.io.*;

/**
 * The type Serializer.
 */
public class Serializer {
    /**
     * Serialize byte [ ].
     *
     * @param object the object
     * @return the byte [ ]
     * @throws IOException the io exception
     */
    public byte[] serialize(Serializable object) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);) {

            objectOutputStream.writeObject(object);
            objectOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * Deserialize t.
     *
     * @param <T>  the type parameter
     * @param data the data
     * @return the t
     * @throws IOException            the io exception
     * @throws ClassNotFoundException the class not found exception
     */
    public <T> T deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);) {
            return (T) objectInputStream.readObject();
        }
    }
}
