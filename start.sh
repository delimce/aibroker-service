#!/bin/bash

# Start the application with optimized JVM settings
java -Xms512m -Xmx1024m \
     -XX:+UseG1GC \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=./heapdump.hprof \
     -XX:+DisableExplicitGC \
     -Djava.awt.headless=true \
     -jar target/aibroker-service-0.0.1-SNAPSHOT.jar
