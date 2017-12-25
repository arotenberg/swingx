/*
 * Created on 24.08.2006
 *
 */
package org.jdesktop.test;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Support class to test serializable behaviour.
 * 
 *  @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public final class SerializableSupport {
    private SerializableSupport() {
        //prevent instantiation
    }
    
    /**
     * Serialize the specified object to bytes, then deserialize it back.
     */
    @SuppressWarnings("unchecked")
    public static <T> T serialize(T object) {
        try {
            return (T) fromBytes(toBytes(object));
        } catch (ClassNotFoundException shouldNeverHappen) {
            throw new Error(shouldNeverHappen);
        } catch (IOException shouldNeverHappen) {
            throw new Error(shouldNeverHappen);
        }
    }

    private static byte[] toBytes(Object object) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream objectsOut = new ObjectOutputStream(bytesOut);
        objectsOut.writeObject(object);
        return bytesOut.toByteArray();
    }

    private static Object fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(bytes);
        ObjectInputStream objectsIn = new ObjectInputStream(bytesIn);
        return objectsIn.readObject();
    }

    /**
     * Encodes an object as XML and then decodes it.
     * 
     * @param <T>
     *            the type to operate on
     * @param object
     *            the object to encode/decode
     * @return a copy of the original object that was encoded and decoded
     * @see XMLEncoder
     * @see XMLDecoder
     */
    @SuppressWarnings("unchecked")
    public static <T> T encode(T object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(baos);
        encoder.writeObject(object);
        encoder.close();
        
        XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(baos.toByteArray()));
        T copy = (T) decoder.readObject();
        decoder.close();
        
        return copy;
    }
}
