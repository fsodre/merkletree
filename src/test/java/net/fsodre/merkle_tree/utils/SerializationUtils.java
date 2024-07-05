package net.fsodre.merkle_tree.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializationUtils {
    public static <T extends Serializable> T serializeAndBack(T obj) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        ObjectOutputStream outObj = new ObjectOutputStream(outStream);
        outObj.writeObject(obj);
        outObj.flush();

        ByteArrayInputStream inStream = new ByteArrayInputStream(outStream.toByteArray());
        ObjectInputStream inObj = new ObjectInputStream(inStream);

        return (T)inObj.readObject();
    }
}
