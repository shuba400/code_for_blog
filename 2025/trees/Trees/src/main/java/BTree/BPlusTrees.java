package BTree;

import Trees.Tree;

public class BPlusTrees<T extends Comparable<T>,V> implements Tree<T,V> {
    static class Node<T>{
        int capacity;
        boolean isLeaf;
        T[] keys;
        int position = 0;

        @SuppressWarnings("unchecked")
        Node(int degree){
            this.capacity = degree - 1;
            keys = (T[]) new Comparable[this.capacity + 1];
        }
    }

    static class InnerNode<T> extends Node<T> {
        Node<T>[] child;

        @SuppressWarnings("unchecked")
        InnerNode(int degree) {
            super(degree);
            this.isLeaf = false;
            this.child = (Node<T>[]) new Node[degree + 1]; // having a degree of +1 to store extra child while balancing
        }

    }

    @SuppressWarnings("unchecked")
    static class LeafNode<T,V> extends Node<T>{
        Node<T> next;
        Node<T> prev;
        V[] value;

        LeafNode(int degree) {
            super(degree);
            this.value = (V[]) new Object[this.capacity + 1];
            this.isLeaf = true;
            this.next = null;
            this.prev = null;
        }
    }

    int degree;
    Node<T> root;

    public BPlusTrees(int degree){
        this.degree = degree;
        root = new LeafNode<>(this.degree);
    }

    private void swap(LeafNode<T,V> leafNode, int i, int j) {
        T tmp = leafNode.keys[j];
        leafNode.keys[j] = leafNode.keys[i];
        leafNode.keys[i] = tmp;

        V cmpndV = leafNode.value[j];
        leafNode.value[j] = leafNode.value[i];
        leafNode.value[i] = cmpndV;
    }

    private Node<T> splitLeafNode(LeafNode<T,V> currentNode){
        LeafNode<T,V> newNode = new LeafNode<>(degree);
        currentNode.next = newNode;
        newNode.prev = currentNode;
        newNode.next = currentNode.next;
        int mid = currentNode.capacity/2;
        for(int i = mid; i <= currentNode.capacity; i++){
            newNode.keys[newNode.position] = currentNode.keys[i];
            newNode.value[newNode.position] = currentNode.value[i];
            newNode.position++;
            currentNode.keys[i] = null;
        }
        currentNode.position = mid;
        return newNode;
    }

    private Node<T> splitInnerNode(InnerNode<T> currentNode){
        InnerNode<T> newNode = new InnerNode<>(degree);
        int mid = currentNode.capacity/2;
        //a b c d e  ---> (a b) c (d e)
        //1 2 3 4 5 6  --> (1 2 3) (4 5 6)
        currentNode.keys[mid] = null;
        for(int i = mid + 1; i <= currentNode.capacity; i++){
            newNode.keys[newNode.position] = currentNode.keys[i];
            newNode.child[newNode.position] = currentNode.child[i];
            newNode.position++;
            currentNode.keys[i] = null;
            currentNode.child[i] = null;
        }
        newNode.child[newNode.position] = currentNode.child[currentNode.position];
        currentNode.child[currentNode.position] = null;
        currentNode.position = mid;
        return newNode;
    }

    private void splitChild(InnerNode<T> parent,int idx){
        Node<T> child = parent.child[idx];
        T midKey = child.keys[child.capacity / 2];
        Node<T> newNode;
        if(child.isLeaf){
            newNode = splitLeafNode((LeafNode<T,V>) child);
        } else {
            newNode = splitInnerNode((InnerNode<T>) child);
        }
        parent.child[parent.position + 1] = parent.child[parent.position];
        for(int i = parent.position; i > idx + 1; i--){
            parent.child[i] = parent.child[i - 1];
            parent.keys[i] = parent.keys[i - 1];
        }
        /*
            a b idx c d ---> e,newNode --> a b e idx c d
            1 2 3 4 5 6                    1 2 3 newNode 5 6
         */
        parent.child[idx + 1] = newNode;
        parent.child[idx] = child;

        if(idx + 1 <= parent.capacity) parent.keys[idx + 1] = parent.keys[idx];
        parent.keys[idx] = midKey;

        parent.position++;
    }
    /*
         Find Leaf Node where this key can be added
         add the key. Now we have a node with more keys

     */
    private void addKey(Node<T> current, T key,V value){
        if(current.isLeaf){
            LeafNode<T,V> leafWorkingNode = (LeafNode<T, V>) current;
            leafWorkingNode.keys[current.position] = key;
            leafWorkingNode.value[current.position] = value;
            int idx = current.position;
            current.position++;
            while (idx > 0){
                if(leafWorkingNode.keys[idx].compareTo(leafWorkingNode.keys[idx - 1]) < 0){
                    swap(leafWorkingNode,idx,idx - 1);
                }
                idx--;
            }
            return;
        }
        InnerNode<T> workingNode = (InnerNode<T>) current;
        int idx = 0;

        //This can be refined with a binary search, although I am not sure if worst case improves.
        while (idx < workingNode.position){
            if(key.compareTo(workingNode.keys[idx]) < 0){
                break;
            }
            idx++;
        }
        addKey(workingNode.child[idx],key,value);
        if(workingNode.child[idx].position == workingNode.capacity + 1){
            splitChild(workingNode,idx);
        }
    }

    @Override
    public String getName() {
        return "In Memory BplusTree";
    }

    @Override
    public boolean insert(T key,V value) {
        if(get(key) != null){
            return false;
        }
        addKey(root,key,value);
        if(root.position == root.capacity + 1){
            InnerNode<T> newRoot = new InnerNode<>(degree);
            newRoot.child[0] = root;
            root = newRoot;
            splitChild(newRoot,0);
        }
        return true;
    }



    @Override
    public boolean delete(T key) {
        throw new RuntimeException("Not Implemented");
    }


    private Node<T> getLeafNodeFor(T key){
        Node<T> current = root;
        InnerNode<T> temporary;
        int cmp;
        while (!current.isLeaf){
            temporary = (InnerNode<T>) current;
            current = null;
            for(int i = 0; i < temporary.position; i++){
                cmp = key.compareTo(temporary.keys[i]);
                if(cmp < 0){
                    current = temporary.child[i];
                    break;
                }
            }
            if(current == null) {
                current = temporary.child[temporary.position];
            }
        }
        return  current;
    }

    @Override
    public V get(T key) {
        Node<T> node = getLeafNodeFor(key);
        for(int i = 0; i < node.position; i++){
            if(key.compareTo(node.keys[i]) == 0){
                return ((LeafNode<T,V>) node).value[i];
            }
        }
        return null;
    }

    @Override
    public void printTree() {
        throw new RuntimeException("Not Implemented");
    }
}
