package BinarySearchTree;

import Trees.Tree;

public class AVLBalancedTree<T extends Comparable<T>,V> implements Tree<T,V> {
    static class Node<T,V> {
        T key;
        V value;
        Node<T,V> left;
        Node<T,V> right;
        int height;
        int balance;

        Node(T key,V value){
            this.key = key;
            this.value = value;
            height = 0;
            balance = 0;
        }
    }

    private Node<T,V> root;
    public AVLBalancedTree(){
        root = null;
    }


    private void updateNode(Node<T,V> curr){
        int l = -1;
        int r = -1;
        if(curr.left != null){
            l = curr.left.height;
        }
        if(curr.right != null){
            r = curr.right.height;
        }
        curr.height = Math.max(l,r) + 1;
        curr.balance = r - l;
    }

    private Node<T,V> rightRotate(Node<T,V> curr){
        Node<T,V> leftChild = curr.left;
        curr.left = leftChild.right;
        leftChild.right = curr;
        updateNode(curr);
        updateNode(leftChild);
        return leftChild;
    }

    private Node<T,V> leftRotate(Node<T,V> curr){
        Node<T,V> rightChild = curr.right;
        curr.right = rightChild.left;
        rightChild.left = curr;
        updateNode(curr);
        updateNode(rightChild);
        return rightChild;
    }

    private Node<T,V> leftLeftCase(Node<T,V> curr){
        return rightRotate(curr);
    }

    private Node<T,V> rightRightCase(Node<T,V> curr){
        return leftRotate(curr);
    }

    private Node<T,V> leftRightCase(Node<T,V> curr){
        curr.left = leftRotate(curr.left);
        return leftLeftCase(curr);
    }

    private Node<T,V> rightLeftCase(Node<T,V> curr){
        curr.right = rightRotate(curr.right);
        return rightRightCase(curr);
    }

    private Node<T,V> balance(Node<T,V> curr){
        updateNode(curr);
        //left is heavy
        if(curr.balance == -2){
            if(curr.left.balance <= 0){
                return leftLeftCase(curr);
            } else {
                return leftRightCase(curr);
            }
        } else if (curr.balance == 2){
            if(curr.right.balance >= 0){
                return rightRightCase(curr);
            } else {
                return rightLeftCase(curr);
            }
        }
        return curr;
    }

    // Doing this recursively will make implementation quite easy, but performance wise,
    // it will be inferior to iterative approach.
    private Node<T,V> add(Node<T,V> curr,T key,V value){
        if(curr == null){
            return new Node<>(key,value);
        }
        int cmp = key.compareTo(curr.key);
        if(cmp > 0){
            curr.right = add(curr.right,key,value);
        } else {
            curr.left = add(curr.left,key,value);
        }
        return balance(curr);
    }

    @Override
    public String getName() {
        return "AVL Balanced BST";
    }

    @Override
    public boolean insert(T key,V value) {
        if(this.get(key) != null) return false;
        root = add(root,key,value);
        return true;
    }

    @Override
    public V get(T key) {
        Node<T,V> current = root;
        int cmp;
        while (current != null){
            cmp = key.compareTo(current.key);
            if(cmp == 0){
                return current.value;
            }
            if(cmp < 0){
                current = current.left;
            } else {
                current = current.right;
            }
        }
        return null;
    }


    @Override
    public boolean delete(T key) {
        return false;
    }

    @Override
    public void printTree(){
        printTree(root,0);
    }

    private void printTree(Node<T,V> current, int gap){
        if (current == null){
            return;
        }
        T l = null,r = null;
        if(current.left != null) l = current.left.key;
        if(current.right != null) r = current.right.key;
        System.out.printf("%d %d %d %d %d\n",current.key,l,r,current.balance,current.height);
        printTree(current.left,gap + 1);
        printTree(current.right,gap + 1);
    }
}
