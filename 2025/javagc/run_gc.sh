#!/bin/bash

./gradlew classes && \
java -cp build/classes/java/main -Xmx2g -XX:MaxDirectMemorySize=2g '-Xlog:gc*:file=off_heap.log' org.example.Main off && \
java -cp build/classes/java/main  -Xmx2g -XX:MaxDirectMemorySize=2g '-Xlog:gc*:file=on_heap_one_section.log' org.example.Main on
