package org.example;

public class Main {
    public static void main(String[] args) {
        SkipList<Integer,Integer> skipList = new SkipList<>(20,2);
        SortedStructure<Integer,Integer> redBlackTree = new RedBlackTree<>();
        insertElements(redBlackTree);
        printElement(redBlackTree);
        System.out.println(skipList.currentMaxLevel);
        insertElements(skipList);
        printElement(skipList);
    }
    static void insertElements(SortedStructure<Integer, Integer> sortedStructure){
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++){
            sortedStructure.insert(i,0);
        }

        long endTime = System.nanoTime();
        // 4. Calculate Duration (in nanoseconds)
        long durationNano = endTime - startTime;

        // 5. Convert to Readable Units (e.g., milliseconds)
        double durationMillis = (double) durationNano / 1_000_000;

        System.out.printf("Duration of inserting 1000000 elements %s: %,d nanoseconds%n", sortedStructure.getName(),durationNano);
    }

    static void printElement(SortedStructure<Integer, Integer> sortedStructure){
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000000; i++){
            sortedStructure.get(i);
        }

        long endTime = System.nanoTime();
        // 4. Calculate Duration (in nanoseconds)
        long durationNano = endTime - startTime;

        // 5. Convert to Readable Units (e.g., milliseconds)
        double durationMillis = (double) durationNano / 1_000_000;

        System.out.printf("Duration of inserting 1000000 elements %s: %,d nanoseconds%n", sortedStructure.getName(),durationNano);
    }
}