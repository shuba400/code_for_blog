package org.example;

import BTree.BPlusTrees;
import BinarySearchTree.AVLBalancedTree;
import BinarySearchTree.BST;
import BinarySearchTree.RedBlackBalancedTree;
import PersistentBPlusTree.PersistentBPlusTree;
import PersistentBPlusTree.Serializer.IntegerSerializer;
import Trees.Tree;

import java.io.IOException;
import java.util.*;

public class Controller {

    public void run() throws IOException {
        int num_of_elements = 10000;
        Tree<Integer,Integer> tree_bst = new BST<>();
        Tree<Integer,Integer> tree_avl = new AVLBalancedTree<>();
        Tree<Integer,Integer> tree_redBlack = new RedBlackBalancedTree<>();
        Tree<Integer,Integer> bplusTree = new BPlusTrees<>(5);
        Tree<Integer,Integer> bplusTreePersistent = new PersistentBPlusTree<>(new IntegerSerializer(),new IntegerSerializer());
        List<Tree<Integer,Integer>> trees = new ArrayList<>(List.of(
                tree_bst,
                tree_avl,
                tree_redBlack,
                bplusTree,
                bplusTreePersistent
        ));
        testAddingElement(trees,false,num_of_elements);
        testGettingElement(trees,num_of_elements);

    }

    void testAddingElement(Tree<Integer,Integer> tree,List<Integer> integers) throws IOException{
        long startTime,endTime,cnter;
        startTime = System.currentTimeMillis();
        cnter = 0;
        for(Integer i : integers){
            boolean done = tree.insert(i,i);
            if(done) cnter++;
        }
        endTime = System.currentTimeMillis();
        System.out.printf("%s added %d elements and took %d ms\n",tree.getName(),cnter,endTime - startTime);
    }

    void testAddingElement(List<Tree<Integer,Integer>> trees,boolean isRandom,int num_of_elements) throws IOException{

        List<Integer> integers = new ArrayList<>();
        for(int i = 0; i < num_of_elements; i++){
            integers.add(i);
        }
        if(isRandom) {
            Collections.shuffle(integers);
        }
        trees.forEach(tree -> {
            try {
                testAddingElement(tree,integers);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    void testGettingElement(Tree<Integer,Integer> tree,int numOfElements) throws IOException {
        int cnter = 0;
        Long startTime = System.currentTimeMillis();
        for(int i = 0; i < numOfElements; i++){
            int val = tree.get(i);
            if(val == i) cnter++;
        }
        Long endTime = System.currentTimeMillis();
        System.out.printf("%s got %d elements in %d ms\n",tree.getName(),cnter,endTime - startTime);
    }

    void testGettingElement(List<Tree<Integer,Integer>> trees,int numOfElements){
        trees.forEach(tree -> {
            try {
                testGettingElement(tree,numOfElements);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
