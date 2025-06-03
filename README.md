# AIBroker Service

A middleware service that acts as an intelligent broker between applications and various Large Language Models (LLMs). It simplifies AI integration by providing a unified API to route, process, and optimize requests to different LLM providers, handling authentication, rate limiting, and response formatting automatically.


## Prerequisites

*   Java Development Kit (JDK) - Version 17 or higher recommended.
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
      docker-compose up -d db
      ```
    * This command starts the database container in detached mode, making it accessible on the port specified in the `docker-compose.yml` file.
    * Edit the `.env` file to configure your environment-specific settings.
     > **Note:** This approach is primarily intended for development and debugging purposes. For production environments, consider using a managed database service, environment vars setup or a more robust deployment strategy.

3. **create database for testing**
    * For integration tests involving the database, a test-specific configuration is used:    
    * Database configurations for tests are specified in `src/main/resources/application-test.yml`
    * Tests use a MySQL database named `aibroker_db_test` for testing

## Building
```bash
./mvnw clean package and create the executable JAR file, run the following command from the project root directory:
```
```bash
This will compile the code, run tests, and package the application into `target/aibroker-service-0.0.1-SNAPSHOT.jar`.
```
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
