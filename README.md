# Mortgage Microservice

## 1. Overview

This is a Spring Boot microservice that provides mortgage-related operations:

- **Check mortgage feasibility** based on income, home value, and loan amount.
- **Manage interest rates** with multiple maturity periods.
- **Profiles**:
    - `local` → in-memory, no database (pure runtime, no persistence)
    - `h2` → in-memory H2 database for testing and integration

**Technologies used**:

- Java 21, Spring Boot 3.5+
- Spring Data JPA (for H2 profile)
- SLF4J logging
- Swagger / OpenAPI
- Docker for containerized deployment

---

## 2. Prerequisites

- JDK 21+
- Maven 3.9+
- Docker (optional, for containerized deployment)
- IDE (IntelliJ IDEA, VS Code, etc.)

---

## 3. Profiles and Configuration

**Profiles**:

| Profile | Description                             | Database           |
|---------|-----------------------------------------|------------------|
| local   | In-memory, no persistence               | None              |
| h2      | In-memory database for testing         | H2                |

**Local profile (`application-local.yml`)**:

```yaml
spring:
  config:
    activate:
      on-profile: local

# No database configuration for local
# Application uses in-memory runtime objects only

```
**H2 profile (`application-h2.yml`)**:
```yaml
spring:
  config:
    activate:
      on-profile: h2

  datasource:
    url: jdbc:h2:mem:mortgage-db
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      enabled: true
      path: /h2-console

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

```

## 4. Running the Application

**Locally with Maven:**


```bash
# Run the application with the local profile (in-memory, no database)
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Run the application with the H2 profile (in-memory H2 database)
mvn spring-boot:run -Dspring-boot.run.profiles=h2

```
**With Docker:**

```bash
# Build Docker image
docker build -t mortgage-app .

# Run with local profile
docker run -e SPRING_PROFILES_ACTIVE=local -p 8080:8080 mortgage-app

# Run with H2 profile
docker run -e SPRING_PROFILES_ACTIVE=h2 -p 8080:8080 mortgage-app

```

## 5. API Endpoints

### 5.1 Get Interest Rates

**GET** `/v1/api/interest-rates`

**Response Body Example:**
```json
[
{"maturityPeriod":10,"interestRate":3.5},
{"maturityPeriod":20,"interestRate":4.0}
]
```

### 5.1 Mortgage Check

**POST** `/v1/api/mortgage-check`

**Request Body Example:**

```json
{
  "income": 50000,
  "maturityPeriod": 10,
  "loanValue": 150000,
  "homeValue": 200000
}
```

**Response Body Example:**

```json
{
  "feasible": true,
  "monthlyCost": 1342.50
}
```

## 6. Error Handling

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

## 7. Swagger / API Documentation

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

> Note: Swagger UI is enabled by default. Disable it in production with the following configuration:

```yaml
springdoc:
  swagger-ui:
    enabled: false
```
## 8. Notes

- Default profile is `local` (in-memory, no DB).
- Use `h2` profile for integration testing or CI pipelines.
- Trace IDs are generated for all errors for easy log correlation.
- Local profile objects are ephemeral; all data will be lost on restart.