# Clinica Dutton — REST API

REST API for a medical clinic management system built with Spring Boot, JPA and MySQL.

> Desktop client available at [Clinica-Dutton](https://github.com/biabrauna/Clinica-Dutton)

## Tech Stack

- Java 16
- Spring Boot 2.7
- Spring Data JPA
- MySQL 8
- Docker & Docker Compose
- JUnit 5 + MockMvc
- Swagger / OpenAPI (springdoc)

## Endpoints

### Doctors
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/doctors` | List all doctors |
| GET | `/doctors/{id}` | Find doctor by id |
| POST | `/doctors` | Register a doctor |
| PUT | `/doctors/{id}` | Update a doctor |
| DELETE | `/doctors/{id}` | Delete a doctor |

### Patients
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/patients` | List all patients |
| GET | `/patients/{id}` | Find patient by id |
| POST | `/patients` | Register a patient |
| DELETE | `/patients/{id}` | Delete a patient |

### Appointments
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/appointments` | List all appointments |
| GET | `/appointments/{id}` | Find appointment by id |
| POST | `/appointments` | Schedule an appointment |
| DELETE | `/appointments/{id}` | Delete an appointment |

## Running locally

### Requirements
- JDK 16+
- Maven 3.8+
- Docker

### 1. Start the database

```bash
docker run -d --name clinica-mysql \
  -e MYSQL_ROOT_PASSWORD=ifgoiano \
  -e MYSQL_DATABASE=clinica \
  -p 3306:3306 mysql:8.0
```

### 2. Run the API

```bash
mvn spring-boot:run
```

API will be available at `http://localhost:8080`

### 3. Or run everything with Docker Compose

```bash
docker-compose up
```

## API Documentation

Swagger UI available at `http://localhost:8080/swagger-ui.html`

## Running tests

```bash
mvn test
```
