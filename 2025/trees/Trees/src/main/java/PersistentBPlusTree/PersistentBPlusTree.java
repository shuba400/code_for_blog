package PersistentBPlusTree;

import PersistentBPlusTree.Serializer.Serializer;
import Trees.Tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class PersistentBPlusTree<K extends Comparable<K>,V> implements Tree<K,V> {
    record SplitResult<K>(K key,Integer offset){};


    DiskManager<K,V> diskManager;
    Node<K,V> rootNode;

    public PersistentBPlusTree(Serializer<K> keySerializer,Serializer<V> valueSerializer) throws IOException {
        diskManager = new DiskManager<>(keySerializer,valueSerializer);
        rootNode = diskManager.getRootNode();
        if (rootNode == null){
            rootNode = new Node.Builder<K,V>().setValues(new ArrayList<>()).setKeys(new ArrayList<>()).setCurrentOffset(diskManager.getNewOffset()).build();
            diskManager.addNode(rootNode);
            diskManager.updateRootNode(rootNode);
        }
    }
    @Override
    public String getName() {
        return "B Plus Tree Persistent";
    }

    @Override
    public boolean insert(K key, V value) {
        try {
            addKey(key,value,false);
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(K key) {
        return false;
    }

    @Override
    public V get(K key) {
        try {
            Node<K,V> node = getLeafNode(key);
            for (int i = 0; i < node.keys.size(); i++){
                if (node.keys.get(i).equals(key)){
                    return node.values.get(i);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private void addKey(K key,V value,boolean overRide) throws IOException {
        Optional<SplitResult<K>> splitResult = addKey(rootNode,key,value,overRide);
        if (splitResult.isEmpty()){
            return;
        }
        K newRootKey = splitResult.get().key;
        ArrayList<K> keys = new ArrayList<>(List.of(newRootKey));
        ArrayList<Integer> childNodeOffset = new ArrayList<>(List.of(rootNode.currentOffset,splitResult.get().offset));
        Node<K,V> newRootNode = new Node.Builder<K,V>().setCurrentOffset(diskManager.getNewOffset()).setKeys(keys).setChildNodeOffsets(childNodeOffset).build();
        diskManager.addNode(newRootNode);
        rootNode = newRootNode;
        diskManager.updateRootNode(rootNode);
    }

    private Optional<SplitResult<K>> addKey(Node<K,V> node, K key, V value, boolean overRide) throws IOException {
        if (node.isLeaf){
            return addKeyToLeaf(node,key,value,overRide);
        }
        Integer position = 0;
        while (position < node.keys.size()){
            if (key.compareTo(node.keys.get(position)) > 0){
                position++;
            }
            else break;
        }
        Node<K,V> childNode = diskManager.getNode(node.childNodeOffsets.get(position));
        Optional<SplitResult<K>> splitResult = addKey(childNode,key,value,overRide);
        if (splitResult.isEmpty()){
            return Optional.empty();
        }
        Integer childOffSet = splitResult.get().offset;
        K childKey = splitResult.get().key;
        /*
        {3 5 10 11 12}
        {10 14 20 30 60 50}
         */
        position = node.keys.size();
        node.keys.add(position,childKey);
        node.childNodeOffsets.add(position + 1,childOffSet);
        for(int i = position; i > 0; i--){
            if(node.keys.get(i).compareTo(node.keys.get(i - 1)) < 0){
                Collections.swap(node.keys,i,i - 1);
                Collections.swap(node.childNodeOffsets,i + 1,i);
            }
        }
        return splitNonLeafNode(node);
    }

    private Optional<SplitResult<K>> addKeyToLeaf(Node<K,V> node,K key,V value,boolean override) throws IOException {
        for (int i = 0; i < node.keys.size(); i++){
            if(node.keys.get(i).equals(key)){
                if(override){
                    node.values.add(i,value);
                }
                return Optional.empty();
            }
        }
        int position = node.keys.size();
        node.keys.add(position,key);
        node.values.add(position,value);
        for (int i = position; i > 0; i--){
            if(node.keys.get(i).compareTo(node.keys.get(i - 1)) < 0){
                Collections.swap(node.keys,i,i - 1);
                Collections.swap(node.values,i,i - 1);
            }
        }
        return splitLeafNode(node);
    }
    private Optional<SplitResult<K>> splitLeafNode(Node<K,V> oldNode) throws IOException {
        if (!diskManager.needSplit(oldNode)){
            diskManager.addNode(oldNode);
            return Optional.empty();
        }
        int size = oldNode.keys.size();
        ArrayList<K> oldKeys = new ArrayList<>(oldNode.keys.subList(0,size/2));
        ArrayList<V> oldValues = new ArrayList<>(oldNode.values.subList(0,size/2));

        ArrayList<K> newKeys = new ArrayList<>(oldNode.keys.subList(size/2,size));
        ArrayList<V> newValues = new ArrayList<>(oldNode.values.subList(size/2,size));


        oldNode.keys = oldKeys;
        oldNode.values = oldValues;

        Node<K,V> newNode = new Node.Builder<K,V>().setValues(newValues).setKeys(newKeys).setCurrentOffset(diskManager.getNewOffset()).build();

        Integer oldNxtNodeOffSet = oldNode.nxtNodeOffset;
        oldNode.nxtNodeOffset = newNode.currentOffset;
        newNode.prevNodeOffset = oldNode.nxtNodeOffset;
        newNode.nxtNodeOffset = oldNxtNodeOffSet;

        if (oldNxtNodeOffSet != -1){
            Node<K,V> oldNxtNode = diskManager.getNode(oldNxtNodeOffSet);
            oldNxtNode.prevNodeOffset = newNode.currentOffset;
            diskManager.addNode(oldNxtNode);
        }

        diskManager.addNode(oldNode);
        diskManager.addNode(newNode);
        return Optional.of(new SplitResult<>(newNode.keys.getFirst(),newNode.currentOffset));
    }

    private Optional<SplitResult<K>> splitNonLeafNode(Node<K,V> currNode) throws IOException {
        if (!diskManager.needSplit(currNode)){
            diskManager.addNode(currNode);
            return Optional.empty();
        }
        Integer size = currNode.keys.size();
        Integer splitPosition = size/2;
        K splitKey = currNode.keys.get(splitPosition);
        ArrayList<K> currNodeKeys = new ArrayList<>(currNode.keys.subList(0,splitPosition));
        ArrayList<Integer> currNodeChildOffsets= new ArrayList<>(currNode.childNodeOffsets.subList(0,splitPosition + 1));
        /*
            3           8          11           14
        1 2     3 4 5      8 9 10     11 12 13    14 15 18 19
           8
        3    11 14
        1 3   8 11 14
         */
        ArrayList<K> newNodeKeys = new ArrayList<>(currNode.keys.subList((size/2) + 1,size));
        ArrayList<Integer> newNodeChildOffsets = new ArrayList<>(currNode.childNodeOffsets.subList(splitPosition + 1,size + 1));

        currNode.keys = currNodeKeys;
        currNode.childNodeOffsets = currNodeChildOffsets;
        diskManager.addNode(currNode);

        Node<K,V> newNode = new Node.Builder<K, V>().setCurrentOffset(diskManager.getNewOffset())
                .setKeys(newNodeKeys)
                .setChildNodeOffsets(newNodeChildOffsets)
                .build();
        diskManager.addNode(newNode);
        return Optional.of(new SplitResult<>(splitKey,newNode.currentOffset));
    }

    private Node<K,V> getLeafNode(K key) throws IOException {
        Node<K,V> currNode = rootNode;
        int cmp;
        while (!currNode.isLeaf){
            int ptr = 0;
            while (ptr < currNode.keys.size()){
                cmp = key.compareTo(currNode.keys.get(ptr));
                if (cmp < 0){
                    break;
                }
                ptr++;
            }
            currNode = diskManager.getNode(currNode.childNodeOffsets.get(ptr));
        }
        return currNode;
    }

    @Override
    public void printTree() {

    }
}
