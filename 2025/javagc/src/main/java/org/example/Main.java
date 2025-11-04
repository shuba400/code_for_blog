package org.example;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int LARGE_ALLOC_MB = 800; // 700 MB since for some reason 1 Gb does not work for me
    private static final int SMALL_ALLOC_COUNT = 2000_000_000;
    private static final int SMALL_ALLOC_SIZE = 128;

    public static void main(String[] args) {
        String mode = args[0];
        if ("on".equals(mode)) {
            runOnHeapTest();
        } else {
            runOffHeapTest();
        }
    }

    private static void runOnHeapTest() {
        System.out.println("Running on heap test");
        List<byte[]> largeOnHeapAllocation = new ArrayList<>();
//        for (int i = 0; i < 1; i++) {
        largeOnHeapAllocation.add(new byte[LARGE_ALLOC_MB*1024 * 1024]); // 1MB chunks onheap
//        }
        allocateSmallObjectOnHeap();
    }

    private static void runOffHeapTest() {
        System.out.println("Running off heap test");
        List<ByteBuffer> largeOffHeapAllocation = new ArrayList<>();
        for (int i = 0; i < LARGE_ALLOC_MB; i++) {
            largeOffHeapAllocation.add(ByteBuffer.allocateDirect(1024 * 1024)); // 1MB chunks offheap
        }
        allocateSmallObjectOnHeap();
    }

    private static void allocateSmallObjectOnHeap(){
        for (int i = 0; i < SMALL_ALLOC_COUNT; i++) {
            byte[] temp = new byte[SMALL_ALLOC_SIZE];
            if (i % 100 == 0) {
                temp[0] = (byte) i; // Lightly "use" the memory, gpt suggested that java optimizes this, so just add a little complexity
            }
        }
    }
}
