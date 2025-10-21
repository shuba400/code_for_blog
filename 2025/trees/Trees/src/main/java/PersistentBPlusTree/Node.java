package PersistentBPlusTree;


import java.security.Key;
import java.util.ArrayList;

public class Node<K,V> {
    Integer currentOffset;
    Integer nxtNodeOffset;
    Integer prevNodeOffset;
    Boolean isLeaf;
    ArrayList<K> keys;
    ArrayList<Integer> childNodeOffsets;
    ArrayList<V> values;

    Node(Builder<K,V> builder){
        this.currentOffset = builder.currentOffset;
        this.nxtNodeOffset = builder.nxtNodeOffset;
        this.prevNodeOffset = builder.prevNodeOffset;
        this.keys = builder.keys;
        this.childNodeOffsets = builder.childNodeOffsets;
        this.values = builder.values;
        this.isLeaf = builder.isLeaf;

    }

    public void print(){
        StringBuilder printingMessage = new StringBuilder();
        printingMessage.append("Current Node Offset ").append(currentOffset).append("\n");if(isLeaf){
            printingMessage.append("Leaf Node\n");

        } else {
            printingMessage.append("Non Leaf Node\n");
        }
        printingMessage.append("Keys - ").append(keys.size()).append(" - ");
        for (K key : keys){
            printingMessage.append(key).append(", ");
        }
        printingMessage.append("\n");
        if (isLeaf){
            printingMessage.append("Values - ");
            for(V value : values){
                printingMessage.append(value).append(" , ");
            }
            printingMessage.append("\n");
            printingMessage.append("Prev OffSet ").append(prevNodeOffset).append(", Next Offset ").append(nxtNodeOffset).append("\n");
        } else {
            printingMessage.append("Childrens Offset - ").append("\n");
            for (Integer child : childNodeOffsets){
                printingMessage.append(child).append(" , ");
            }
        }
        System.out.println(printingMessage);
        return;
    }

    public static class Builder<K,V> {
        Integer currentOffset = null;
        Integer nxtNodeOffset = -1;
        Integer prevNodeOffset = -1;
        Boolean isLeaf = null;
        ArrayList<K> keys = null;
        ArrayList<Integer> childNodeOffsets = null;
        ArrayList<V> values = null;

        public Builder<K, V> setChildNodeOffsets(ArrayList<Integer> childNodeOffsets) {
            this.setLeaf(false);
            this.childNodeOffsets = childNodeOffsets;
            return this;
        }

        public Builder<K, V> setCurrentOffset(Integer currentOffset) {
            this.currentOffset = currentOffset;
            return this;
        }

        public Builder<K,V> setKeys(ArrayList<K> keys) {
            this.keys = keys;
            return this;
        }

        public Builder<K,V> setLeaf(Boolean leaf) {
            isLeaf = leaf;
            return this;
        }

        public Builder<K,V> setNxtNodeOffset(Integer nxtNodeOffset) {
            this.nxtNodeOffset = nxtNodeOffset;
            return this;
        }

        public Builder<K,V> setPrevNodeOffset(Integer prevNodeOffset) {
            this.prevNodeOffset = prevNodeOffset;
            return this;
        }

        public Builder<K,V> setValues(ArrayList<V> values) {
            this.setLeaf(true);
            this.values = values;
            return this;
        }

        public Node<K,V> build(){
            return new Node<K, V>(this);
        }
    }
}
