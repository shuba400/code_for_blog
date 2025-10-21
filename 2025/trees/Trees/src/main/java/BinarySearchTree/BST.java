package BinarySearchTree;

import Trees.Tree;

public class BST<T extends Comparable<T>,V> implements Tree<T,V> {

    static class Node<T,V> {
        T key;
        V val;
        Node<T,V> left,right;
        Node (T key,V value){
            this.key = key;
            this.val = value;
        }
    }

    Node<T,V> head;

    public BST(){
        head = null;
    }

    @Override
    public String getName() {
        return "Naive BST";
    }

    @Override
    public boolean insert(T key,V value) {
        if (head == null){
            head = new Node<>(key,value);
            return true;
        }
        Node<T,V> current = head;
        Node<T,V> par = null;
        int cmp = 0;
        while (current != null){
            cmp = key.compareTo(current.key);
            if(cmp == 0){
                return false;
            }

            par = current;
            if(cmp > 0){
                current = current.right;
            } else {
                current = current.left;
            }
        }
        Node<T,V> newNode = new Node<>(key,value);
        if(cmp < 0){
            par.left = newNode;
        } else {
            par.right = newNode;
        }
        return true;
    }

    @Override
    public boolean delete(T key) {
        Node<T,V> current = head;
        Node<T,V> par = null;
        while (current != null){
            if(key.compareTo(current.key) == 0) {
                removeNode(current, par);
                return true;
            }
            par = current;
            if(key.compareTo(current.key) > 0){
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return false;
    }


    @Override
    public V get(T key) {
        Node<T,V> current = head;
        while (current != null){
            if (key.compareTo(current.key) == 0){
                return current.val;
            }
            if(key.compareTo(current.key) > 0){
                current = current.right;
            } else {
                current = current.left;
            }
        }
        return null;
    }


    private T getAndRemoveRightMostKeyOnLeftSubtree(Node<T,V> node){
        Node<T,V> par = node;
        node = node.left;
        while (node.right != null){
            par = node;
            node = node.right;
        }
        par.right = node.left;
        node.left = null;
        return node.key;
    }

    private T getAndRemoveLeftMostKeyOnRightSubtree(Node<T,V> node){
        Node<T,V> par = node;
        node = node.right;
        while (node.left != null){
            par = node;
            node = node.left;
        }
        par.left = node.right;
        node.right = null;
        return node.key;
    }

    private void removeNode(Node<T,V> current,Node<T,V> parent){
        T replacementKey = null;
        if(current.left != null){
            replacementKey = getAndRemoveRightMostKeyOnLeftSubtree(current);
        } else if(current.right != null){
            replacementKey = getAndRemoveLeftMostKeyOnRightSubtree(current);
        } else {
            if(parent == null){
                head = null;
                return;
            }
            if(current == parent.left) parent.left = null;
            else parent.right = null;
        }
        current.key = replacementKey;
    }

    public void printTree(){
        printTree(head,0);
    }

    private void printTree(Node<T,V> current,int gap){
        if(current == null){
            return;
        }
        StringBuilder lineBuilder = new StringBuilder();
        if(gap > 0){
            lineBuilder.append('|');
        }
        for(int i = 1; i < gap; i++){
            lineBuilder.append(' ');
            lineBuilder.append('|');
        }
        if(gap > 0){
            lineBuilder.append('-');
        }
        lineBuilder.append(current.key.toString());
        System.out.println(lineBuilder);
        printTree(current.left,gap + 1);
        printTree(current.right,gap + 1);
    }


}
