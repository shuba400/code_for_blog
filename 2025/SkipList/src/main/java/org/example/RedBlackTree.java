package org.example;

import java.util.TreeMap;

public class RedBlackTree<T extends Comparable<T>,V> implements SortedStructure<T,V>{

    TreeMap<T,V> treeMap;

    RedBlackTree(){
        treeMap = new TreeMap<>();
    }

    @Override
    public void insert(T key, V value) {
        treeMap.put(key,value);
    }

    @Override
    public V get(T key) {
        return treeMap.get(key);
    }

    public String getName(){
        return "Red Black Tree";
    }
}
