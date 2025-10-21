package PersistentBPlusTree.Serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class IntegerSerializer implements Serializer<Integer> {
    @Override
    public void serialize(ByteBuffer out, Integer key) throws IOException {
        out.putInt(key);
    }

    @Override
    public Integer deserialize(ByteBuffer in) throws IOException {
        return in.getInt();
    }
}
