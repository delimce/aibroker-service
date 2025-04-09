#!/bin/bash

# Exit on error
set -e

# Set environment variables
export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-"default"}
export JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./heapdump.hprof -XX:+DisableExplicitGC -Djava.awt.headless=true"
export APPLICATION_NAME="aibroker-service"

echo "Starting Application: $APPLICATION_NAME"
echo "Using profile: $SPRING_PROFILES_ACTIVE"
echo "JVM Options: $JAVA_OPTS"

# Extract version from pom.xml
VERSION=$(grep -A 1 "<artifactId>$APPLICATION_NAME</artifactId>" pom.xml | grep version | sed -e 's/.*<version>\(.*\)<\/version>.*/\1/' | tr -d '[:space:]')

# Check if JAR file exists
JAR_FILE="target/$APPLICATION_NAME-$VERSION.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: JAR file not found at $JAR_FILE"
    echo "Please run 'mvn clean package' first"
    exit 1
fi

# Start the application
java $JAVA_OPTS -jar "$JAR_FILE"
