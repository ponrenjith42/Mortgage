# Mortgage Microservice

## 1. Overview

This is a Spring Boot microservice that provides mortgage-related operations:

- **Check mortgage feasibility** based on income, home value, and loan amount.
- **Manage interest rates** with multiple maturity periods.

- **Profiles**:
    - `local` → in-memory, no database (pure runtime, no persistence)
    - `h2` → in-memory H2 database for testing and integration
    - `cache` → Uses Redis cache and Postgres database

## 2. Technologies used

- Java 21, Spring Boot 3.5+
- Spring Data JPA (for H2 and Postgres profiles)
- SLF4J logging
- Swagger / OpenAPI
- Docker for containerized deployment

---

## 3. Prerequisites

- JDK 21+
- Maven 3.9+
- Docker (optional, for containerized deployment)
- IDE (IntelliJ IDEA, VS Code, etc.)

---

## 4. Profiles and Configuration

**Profiles**:

| Profile | Description                        | Database      |
|---------|------------------------------------|---------------|
| local   | In-memory, no persistence          | None          |
| h2      | In-memory database for testing     | H2            |
| cache   | Uses Redis cache and Postgres DB   | Redis+Postgre |


## 5. Running the Application

**Locally with Maven:**

```bash
# Run the application with the local profile (in-memory, no database)
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Run the application with the H2 profile (in-memory H2 database)
mvn spring-boot:run -Dspring-boot.run.profiles=h2

# Run the application with the cache profile (Redis cache + Postgres DB)
mvn spring-boot:run -Dspring-boot.run.profiles=cache
```
**With Docker:**

```bash
# Build Docker image
docker build -t mortgage-app .

# Run with local profile
docker run -e SPRING_PROFILES_ACTIVE=local -p 8080:8080 mortgage-app

# Run with H2 profile
docker run -e SPRING_PROFILES_ACTIVE=h2 -p 8080:8080 mortgage-app

# Run with Redis cache profile
docker run -e SPRING_PROFILES_ACTIVE=cache -e SPRING_REDIS_HOST=redis -e SPRING_REDIS_PORT=6379 -p 8080:8080 mortgage-app
```

## 6. API Endpoints

### 6.1 Get Interest Rates

**GET** `/v1/api/interest-rates`

**Response Body Example:**

```json
[
  {
    "maturityPeriod": 20,
    "interestRate": 3.5,
    "lastUpdate": "2026-01-28T13:36:38.652148"
  }
]
```

### 6.2 Mortgage Check

**POST** `/v1/api/mortgage-check`

**Request Body Example:**

```json
{
  "income": 5000.00,
  "maturityPeriod": 20,
  "loanValue": 2000.00,
  "homeValue": 3000.00
}
```

**Response Body Example:**

```json
{
  "feasible": true,
  "monthlyCost": 11.60
}
```

## 7. Error Handling

All errors include a unique `traceId` for easier debugging.

| Code                     | Description                            |
|--------------------------|----------------------------------------|
| INTEREST_RATE_NOT_FOUND   | Maturity period does not exist         |
| DUPLICATE_INTEREST_RATE   | Interest rate already exists           |
| VALIDATION_ERROR          | Input payload is invalid               |

**Example Error Response:**

```json
{
  "code": "INTEREST_RATE_NOT_FOUND",
  "messages": ["Interest rate not found for maturity: 99"],
  "traceId": "123e4567-e89b-12d3-a456-426614174000",
  "status": 404
}
```

## 8. Swagger / API Documentation

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> Note: Swagger UI is enabled by default. Disable it in production with the following configuration:

```yaml
springdoc:
  swagger-ui:
    enabled: false
```
## 9. Notes

- Default profile is `local` (in-memory, no DB).
- Use `h2` profile for  (in-memory, with DB).
- Use `cache` profile for  (redis, with DB).
- Trace IDs are generated for all errors for easy log correlation.
- Local/H2 profile objects are ephemeral; all data will be lost on restart.