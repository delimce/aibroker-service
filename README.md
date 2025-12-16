# AIBroker Service

A middleware service that acts as an intelligent broker between applications and various Large Language Models (LLMs). It simplifies AI integration by providing a unified API to route, process, and optimize requests to different LLM providers, handling authentication, rate limiting, and response formatting automatically.


## Prerequisites

*   Java Development Kit (JDK) - Version 21 or higher recommended.
*   Docker & Docker Compose - For running associated services or the application containerized.

## Installation

1.  Clone the repository:
    ````bash
    git clone git@github.com:delimce/aibroker-service.git
    ````
2.  Navigate into the project directory:
    ````bash
    cd aibroker-service
    ````

## Configuration

1.  **Application Configuration:**
    *   The main application configuration is located in `src/main/resources/application.yml`.
    *   Copy the `.env.example` file to `.env` and set the required environment variables:
        ```bash
        cp .env.example .env
        ```
2.  **Database Setup:**
    * To create and run the database container in one step:
      ```bash
      docker-compose up -d
      ```
    * This command starts the database container in detached mode, making it accessible on the port specified in the `docker-compose.yml` file.
    * Edit the `.env` file to configure your environment-specific settings.
     > **Note:** This approach is primarily intended for development and debugging purposes. For production environments, consider using a managed database service, environment vars setup or a more robust deployment strategy.

3. **Create database for testing**
    * For integration tests involving the database, a test-specific configuration is used:    
    * Database configurations for tests are specified in `src/main/resources/application-test.yml`
    * Tests use a MySQL database named `aibroker_db_test` for testing

4. **Create valid JWT secret key for signing**
    * Ensure you have a valid JWT token for testing authentication-related endpoints.
    * You can generate a token using your preferred method or tool, making sure it matches the expected format and secret key used by the application.
    command to create a JWT secret key:
    ```bash
    openssl rand -base64 64
    ```

## Building

To clean, compile, run tests, and package the application into an executable JAR file:

```bash
./mvnw clean package
```

This will create the file `target/aibroker-service-x.x.x-SNAPSHOT.jar`.

To run the application in production mode:

```bash
./start.sh
```

## Database Migrations

This project uses Flyway for database migrations. Migration scripts are located in `src/main/resources/db/migration`.

To manage database migrations, use the provided script that automatically loads database configuration from your .env file:

```bash
# See the migration status
./flyway.sh info

# Apply pending migrations
./flyway.sh migrate

# Clean the database (USE WITH CAUTION - DELETES ALL DATA)
./flyway.sh clean
```

### Migration Documentation

For comprehensive information about database migrations, including:
- Migration naming conventions
- How to add new migrations
- Important guidelines and best practices
- Command reference

See the 📚 [**Database Migrations Documentation**](src/main/resources/db/migration/README.md)

## Testing
To run the unit tests, execute the following command:

```bash
./mvnw test
```

## API Testing

*   An HTTP client file `test-api.http` is included for testing API endpoints using tools like the VS Code REST Client extension.


## Integration Testing


2. **Running Database Integration Tests:**
    ```bash
    ./mvnw test -Dspring.profiles.active=test
    ```

3. **Custom Test Data:**
    * Test fixtures and data initialization scripts are located in `src/test/resources/data`
    * Database schema is automatically created based on JPA entity definitions

4. **Test Database Logging:**
    * SQL logging can be enabled in test mode by setting appropriate log levels in the test configuration

> **Note:** Integration tests are isolated from your development or production databases, ensuring no test-related data affects your actual databases.
