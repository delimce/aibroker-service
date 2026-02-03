#!/bin/bash

# Exit on error
set -e

# Load .env file
if [ ! -f .env ]; then
    echo "Error: .env file not found"
    exit 1
fi

# Read port from .env file
APP_PORT=$(grep "^SPRING.APPLICATION.PORT=" .env | cut -d '=' -f2)

if [ -z "$APP_PORT" ]; then
    echo "Error: SPRING.APPLICATION.PORT not found in .env file"
    exit 1
fi

echo "Configured port: $APP_PORT"

# Check if port is already in use
PORT_PID=$(lsof -ti :$APP_PORT || true)

if [ ! -z "$PORT_PID" ]; then
    echo "Port $APP_PORT is already in use by process $PORT_PID"
    echo "Killing process $PORT_PID..."
    kill -9 $PORT_PID
    sleep 2
    echo "Process killed successfully"
else
    echo "Port $APP_PORT is available"
fi

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
nohup java $JAVA_OPTS -jar "$JAR_FILE" > app.log 2>&1 &
echo "Application version $VERSION started in background with PID: $!"