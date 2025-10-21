package PersistentBPlusTree;

import PersistentBPlusTree.Serializer.Serializer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/*
Integer currentOffset; //do not need to be serialized
    Integer nxtNodeOffset;
    Integer prevNodeOffset;
    boolean isLeaf;
    ArrayList<T> keys;
    ArrayList<Integer> childNodeOffsets;
    ArrayList<V> values;

 */

public class NodeSerializer {
    static <K,V> ByteBuffer serialize(Node<K,V> node, Serializer<K> keySerializer, Serializer<V> valueSerializer, Integer pageSize) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(pageSize);
        byteBuffer.put((byte) (node.isLeaf ?  1 : 0));
        byteBuffer.putInt(node.keys.size());
        for (K key : node.keys){
            keySerializer.serialize(byteBuffer,key);
        }
        if(node.isLeaf) {
            for (V value : node.values) {
                valueSerializer.serialize(byteBuffer, value);
            }
            byteBuffer.putInt(node.prevNodeOffset);
            byteBuffer.putInt(node.nxtNodeOffset);
        } else {
            for (Integer childOffSet : node.childNodeOffsets) {
                byteBuffer.putInt(childOffSet);
            }
        }
        return byteBuffer;
    }

    static <K,V> Node<K,V> deserialize(ByteBuffer byteBuffer, Integer currNodeOffSet, Serializer<K> keySerializer, Serializer<V> valueSerializer) throws IOException {
        Node.Builder<K,V> nodeBuilder = new Node.Builder<>();
        nodeBuilder.setCurrentOffset(currNodeOffSet);
        Boolean isLeaf = byteBuffer.get() == (byte) 1;
        nodeBuilder.setLeaf(isLeaf);
        int size = byteBuffer.getInt();
        ArrayList<K> keys = new ArrayList<>(size);
        for(int i = 0; i < size; i++){
            keys.add(i,keySerializer.deserialize(byteBuffer));
        }
        nodeBuilder.setKeys(keys);
        if(isLeaf){
            ArrayList<V> values = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                values.add(i, valueSerializer.deserialize(byteBuffer));
            }
            Integer prevOffset = byteBuffer.getInt();
            Integer nextOffset = byteBuffer.getInt();
            nodeBuilder.setValues(values);
            nodeBuilder.setNxtNodeOffset(nextOffset);
            nodeBuilder.setPrevNodeOffset(prevOffset);

        } else {
            ArrayList<Integer> childNodeOffsets;
            childNodeOffsets = new ArrayList<>(size + 1);
            for(int i = 0; i < size + 1; i++){
                childNodeOffsets.add(i,byteBuffer.getInt());
            }
            nodeBuilder.setChildNodeOffsets(childNodeOffsets);
        }
        return nodeBuilder.build();
    }
}
