# Buy More E-Mart — Backend

Spring Boot 3 (Java 17) backend for the Buy More E-Mart frontend. This is currently a bare project setup — no entities, repositories, services, or controllers have been implemented yet.

## Tech Stack

- Java 17
- Spring Boot 3.5.16
- Spring Web
- Spring Data JPA
- PostgreSQL (driver)
- Spring Boot Validation
- Lombok
- Spring Boot DevTools
- Maven

## Package Structure

```
com.buymore.backend
├── controller   REST endpoints
├── service      business logic
├── repository   Spring Data JPA repositories
├── entity       JPA entities
├── dto          request/response DTOs
├── config       configuration classes (CORS, beans, etc.)
└── exception    exception handling
```

## Prerequisites

- JDK 17+
- Maven 3.9+ (or use the included `mvnw` wrapper, if present)
- A running PostgreSQL instance

## Database Configuration

Connection settings are read from environment variables (see [application.yml](./src/main/resources/application.yml)), with local-dev defaults:

| Variable      | Default     | Description         |
|---------------|-------------|----------------------|
| `DB_HOST`     | `localhost` | PostgreSQL host      |
| `DB_PORT`     | `5432`      | PostgreSQL port      |
| `DB_NAME`     | `buymore`   | Database name        |
| `DB_USERNAME` | `postgres`  | Database username     |
| `DB_PASSWORD` | `postgres`  | Database password     |
| `SERVER_PORT` | `8080`      | Port the app runs on |

Create the database before starting the app, e.g.:

```bash
createdb buymore
```

Or set the env vars to point at an existing database:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=buymore
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
```

## Running the app

From the `backend/` folder:

```bash
mvn spring-boot:run
```

Or build a jar and run it:

```bash
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

The app starts on `http://localhost:8080` by default.

## Building / verifying only

```bash
mvn clean compile
```
