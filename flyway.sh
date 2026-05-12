#!/bin/bash

# Usage: ./flyway.sh [info|migrate|clean] [home|cloud]
# Defaults to cloud profile if not provided.

COMMAND="$1"
PROFILE="$2"

if [[ "$COMMAND" == "home" || "$COMMAND" == "cloud" ]]; then
  PROFILE="$COMMAND"
  COMMAND="$2"
fi

if [[ -z "$COMMAND" ]]; then
  echo "Usage: $0 [info|migrate|clean] [home|cloud]"
  exit 1
fi

if [[ -z "$PROFILE" ]]; then
  PROFILE="cloud"
fi

if [[ "$PROFILE" == "cloud" ]]; then
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

  LOCATIONS="classpath:db/migration"
elif [[ "$PROFILE" == "home" ]]; then
  DB_URL="jdbc:sqlite:${HOME}/.aibroker/aibroker.db"
  DB_USER=""
  DB_PASSWORD=""
  LOCATIONS="classpath:db/migration-sqlite"
else
  echo "Unknown profile: $PROFILE"
  echo "Usage: $0 [info|migrate|clean] [home|cloud]"
  exit 1
fi

# Trim any carriage returns from the values
DB_URL=$(echo "$DB_URL" | tr -d '\r')
DB_USER=$(echo "$DB_USER" | tr -d '\r')
DB_PASSWORD=$(echo "$DB_PASSWORD" | tr -d '\r')

run_flyway() {
  local action="$1"
  local cmd=(./mvnw "flyway:${action}" -Dflyway.url="$DB_URL" -Dflyway.locations="$LOCATIONS")

  if [[ -n "$DB_USER" ]]; then
    cmd+=(-Dflyway.user="$DB_USER")
  fi

  if [[ -n "$DB_PASSWORD" ]]; then
    cmd+=(-Dflyway.password="$DB_PASSWORD")
  fi

  "${cmd[@]}"
}

# Run the Flyway command with the extracted parameters
if [ "$COMMAND" = "info" ]; then
  run_flyway "info"
elif [ "$COMMAND" = "migrate" ]; then
  run_flyway "migrate"
elif [ "$COMMAND" = "clean" ]; then
  echo "WARNING: This will delete all objects in the schema!"
  read -p "Are you sure you want to continue? (y/n) " -n 1 -r
  echo
  if [[ $REPLY =~ ^[Yy]$ ]]; then
    run_flyway "clean"
  fi
else
  echo "Usage: $0 [info|migrate|clean] [home|cloud]"
  exit 1
fi
