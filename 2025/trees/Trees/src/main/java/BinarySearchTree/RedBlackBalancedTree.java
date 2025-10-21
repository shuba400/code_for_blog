package BinarySearchTree;

import Trees.Tree;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class RedBlackBalancedTree<T extends Comparable<T>,V> implements Tree<T,V> {
    TreeMap<T,V> s;
    public RedBlackBalancedTree(){
        s = new TreeMap<>();
    }

    @Override
    public String getName() {
        return "Red Black BST";
    }

    @Override
    public boolean insert(T key,V value) {
        return s.put(key,value) == null;
    }

    @Override
    public boolean delete(T key) {
        return false;
    }

    @Override
    public V get(T key) {
        return s.get(key);
    }

    @Override
    public void printTree() {

    }
}
