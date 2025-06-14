#!/bin/bash

# Load database config from .env file
if [ -f .env ]; then
  # Read the .env file line by line
  while IFS= read -r line || [[ -n "$line" ]]; do
    # Skip empty lines and comments
    [[ -z "$line" || "$line" == \#* ]] && continue
    
    # Extract key and value
    key=$(echo "$line" | cut -d '=' -f1)
    value=$(echo "$line" | cut -d '=' -f2-)
    
    # Store each value as a variable
    if [[ "$key" == "SPRING.DATASOURCE.URL" ]]; then
      DB_URL="$value"
    elif [[ "$key" == "SPRING.DATASOURCE.USERNAME" ]]; then
      DB_USER="$value"
    elif [[ "$key" == "SPRING.DATASOURCE.PASSWORD" ]]; then
      DB_PASSWORD="$value"
    fi
  done < .env
else
  echo "Error: .env file not found"
  exit 1
fi

# Trim any carriage returns from the values
DB_URL=$(echo "$DB_URL" | tr -d '\r')
DB_USER=$(echo "$DB_USER" | tr -d '\r')
DB_PASSWORD=$(echo "$DB_PASSWORD" | tr -d '\r')

# Run the Flyway command with the extracted parameters
if [ "$1" = "info" ]; then
  ./mvnw flyway:info -Dflyway.url="$DB_URL" -Dflyway.user="$DB_USER" -Dflyway.password="$DB_PASSWORD"
elif [ "$1" = "migrate" ]; then
  ./mvnw flyway:migrate -Dflyway.url="$DB_URL" -Dflyway.user="$DB_USER" -Dflyway.password="$DB_PASSWORD"
elif [ "$1" = "clean" ]; then
  echo "WARNING: This will delete all objects in the schema!"
  read -p "Are you sure you want to continue? (y/n) " -n 1 -r
  echo
  if [[ $REPLY =~ ^[Yy]$ ]]; then
    ./mvnw flyway:clean -Dflyway.url="$DB_URL" -Dflyway.user="$DB_USER" -Dflyway.password="$DB_PASSWORD"
  fi
else
  echo "Usage: $0 [info|migrate|clean]"
  exit 1
fi
