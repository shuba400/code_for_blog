package PersistentBPlusTree.Serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface Serializer<T> {
    void serialize(ByteBuffer out, T key) throws IOException;
    T deserialize(ByteBuffer in) throws IOException;
}
