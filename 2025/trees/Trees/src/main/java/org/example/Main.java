package org.example;

import PersistentBPlusTree.DiskManager;
import PersistentBPlusTree.Node;
import PersistentBPlusTree.PersistentBPlusTree;
import PersistentBPlusTree.Serializer.IntegerSerializer;
import PersistentBPlusTree.Serializer.Serializer;
import Trees.Tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Controller controller = new Controller();
            controller.run();
//            Tree<Integer,Integer> tree = new PersistentBPlusTree<Integer, Integer>(new IntegerSerializer(),new IntegerSerializer());
//            int m = 2000;
//            for (int i = 1; i <= m; i++){
//                tree.insert(i,i);
//            }
//            Integer count = 0;
//            for (int i = 1; i <= m; i++){
//                if(tree.get(i) != i){
//                    System.out.println("Errrrror\n");
//                } else {
//                    count++;
//                }
//            }
//            System.out.printf("Able to fetch count nicellyyyy, %d\n",count);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}