package org.example;

public interface SortedStructure<T extends Comparable<T>,V> {
    void insert(T key,V value);
    V get(T key);
    String getName();
}
