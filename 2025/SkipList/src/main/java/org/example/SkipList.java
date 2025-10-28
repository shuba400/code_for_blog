package org.example;

import java.util.Random;

public class SkipList<T extends Comparable<T>,V> implements SortedStructure<T,V> {
    private final int maxLevel;
    private final Random rand;
    public int currentMaxLevel;
    private final Node<T,V> header;

    static class Node<T,V> {
        T key;
        V value;
        Node<T,V>[] forwardNodes;

        Node(T key,V value,int maxLevel){
            this.key = key;
            this.value = value;
            this.forwardNodes = new Node[maxLevel + 1];
        }
    }

    int getLevel(){
        int newLvl = 0;
        while (rand.nextBoolean() && newLvl <= (currentMaxLevel + 1) && newLvl <= maxLevel) {
            newLvl++;
        }
        return newLvl;
    }

    SkipList(int maxLevel,int seed){
        this.maxLevel = maxLevel;
        this.rand = new Random(seed);
        this.header = new Node<>(null,null,maxLevel);
        this.currentMaxLevel = 0;
    }

    @Override
    public void insert(T key,V value) {
        Node<T,V> currentNode = header;
        Node<T,V>[] updateNodes = new Node[maxLevel + 1];
        for (int i = 0; i <= maxLevel; i++){
            updateNodes[i] = header;
        }
        for (int i = currentMaxLevel; i >= 0; i--){
            while (currentNode.forwardNodes[currentMaxLevel] != null && key.compareTo(currentNode.forwardNodes[currentMaxLevel].key) < 0){
                currentNode = currentNode.forwardNodes[i];
            }
            updateNodes[i] = currentNode;
        }
        int currentNodeLevel = getLevel();
        if (currentNodeLevel > currentMaxLevel){
            for (int i = currentMaxLevel + 1; i <= currentNodeLevel; i++){
                updateNodes[i] = header;
            }
            currentMaxLevel = currentNodeLevel;
        }
        Node<T,V> node = new Node<>(key,value,currentNodeLevel);
        for (int i = 0; i <= currentNodeLevel; i++){
            node.forwardNodes[i] = updateNodes[i].forwardNodes[i];
            updateNodes[i].forwardNodes[i] = node;
        }
    }

    @Override
    public V get(T key) {
        Node<T,V> currentNode = header;
        for (int i = this.currentMaxLevel; i >= 0; i--){
            while (currentNode.forwardNodes[i] != null && key.compareTo(currentNode.forwardNodes[i].key) < 0) {
                currentNode = currentNode.forwardNodes[i];
            }
            if(currentNode.forwardNodes[i] != null  && currentNode.forwardNodes[i].key != key){
                return currentNode.forwardNodes[i].value;
            }
        }
        currentNode = currentNode.forwardNodes[0];
        if (currentNode == null || currentNode.key != key){
            return null;
        }
        return currentNode.value;
    }

    public String getName(){
        return "Skip List";
    }
}
