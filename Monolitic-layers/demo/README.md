# Course Enrollment System

A modern, well-structured Spring Boot monolith implementing a clean layered architecture for course enrollment management. This project refactors the original `Spaguetti-code/CourseEnrollmentSystem.java` Java Swing desktop application into a production-ready REST API backend.

## Project Structure

```
demo/
├── src/main/java/com/university/enrollment/
│   ├── domain/                    # JPA Entities (Student, Course, Professor, Enrollment, Teaching)
│   ├── repository/                # Spring Data JPA repositories
│   ├── service/                   # Business logic & transaction boundaries
│   ├── presentation/
│   │   ├── controller/            # REST API endpoints
│   │   └── dto/                   # Request & Response Data Transfer Objects
│   ├── exception/                 # Custom exceptions & global error handler
│   └── util/                      # Utilities (PriceCalculator, DataMigrationRunner)
├── src/main/resources/
│   ├── application.yml            # Main configuration (dev profile)
│   ├── application-dev.yml        # Development profile (H2 in-memory)
│   ├── application-prod.yml       # Production profile (PostgreSQL)
│   └── data-migration/            # Legacy data files (students.txt, courses.txt, etc.)
├── src/test/java/                 # Unit & integration tests
├── pom.xml                        # Maven build configuration
└── README.md                      # This file
```

## Architectural Layers

- **Presentation Layer (Controllers + DTOs)**: Handles HTTP requests/responses, input validation via Jakarta Validation, JSON serialization. Controllers delegate to services.
- **Business Logic Layer (Services)**: Encapsulates enrollment rules, price calculation, validation logic. Annotated with `@Transactional`.
- **Data Access Layer (Repositories)**: Spring Data JPA interfaces for CRUD and custom queries.
- **Domain Layer (Entities)**: Pure JPA entities with no framework-specific code in business logic.

**Dependency Direction**: Controllers → Services → Repositories → Database

## Features

- RESTful API for course enrollment operations
- JPA/Hibernate persistence with H2 (dev) and PostgreSQL (prod)
- Business rules: max 5 students per course, tiered pricing ($100 base, $85 discount for ≥3 prior enrollments), duplicate prevention
- Automatic migration of legacy `.txt` data into database on first startup
- Request validation with Jakarta Validation
- Global exception handling with structured JSON error responses
- SLF4J/Logback logging
- Environment-based configuration via `.env` and `application.yml`

## Prerequisites

- **Java 17** or higher (Spring Boot 3 requires Java 17+)
- **Maven 3.8+** (or use included Maven Wrapper `mvnw`)
- (Optional) PostgreSQL for production profile

## Build & Run

### Using Maven Wrapper (recommended)

```bash
cd Monolitic-layers/enrollment-system
./mvnw clean package        # Linux/macOS
mvnw.cmd clean package      # Windows
```

### Run the application

```bash
# Development mode (H2 in-memory database)
./mvnw spring-boot:run

# Or run the packaged JAR
java -jar target/enrollment-system-1.0.0-SNAPSHOT.jar
```

### Using environment variables

Copy `.env.example` to `.env` and adjust values:

```bash
cp .env.example .env
# Edit .env with your settings
```

### Profiles

- **dev** (default): H2 in-memory database, show SQL, create-drop DDL
- **prod**: PostgreSQL, no SQL logging, validate DDL

```bash
# Run with prod profile
SPRING_PROFILES_ACTIVE=prod ./mvnw spring-boot:run
```

## API Reference

Base URL: `http://localhost:8080/api/v1`

### Enrollments

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/enrollments` | Enroll a student in a course | `{"studentId":1,"courseId":101}` | `EnrollmentDTO` (201 Created) |
| GET | `/enrollments` | List all enrollments | — | `List<EnrollmentDTO>` (200) |
| GET | `/enrollments?courseName=Programming` | Filter by course name | — | `List<EnrollmentDTO>` (200) |

**Example**:

```bash
# Enroll student 1 in course 101
curl -X POST http://localhost:8080/api/v1/enrollments \
  -H "Content-Type: application/json" \
  -d '{"studentId":1,"courseId":101}'

# Get all enrollments
curl http://localhost:8080/api/v1/enrollments
```

### Students

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/students` | List all students | `List<StudentDTO>` (200) |

### Courses

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/courses` | List all courses | `List<CourseDTO>` (200) |
| GET | `/courses/search?name=Prog` | Search by name (case-insensitive) | `List<CourseDTO>` (200) |

### Professors

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/professors` | List all professors | `List<ProfessorDTO>` (200) |

### Teaching Assignments

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| GET | `/teaching` | List professor-course pairs | `List<TeachingDTO>` (200) |

## Data Models

### Entities (Domain)

| Entity | Fields |
|--------|--------|
| `Student` | id, firstName, lastName, age, semester, credits |
| `Course` | id, name (unique), location |
| `Professor` | id, firstName, lastName, degree, employmentType |
| `Enrollment` | EnrollmentId (studentId + courseId), student (ManyToOne), course (ManyToOne), price, enrolledAt |
| `Teaching` | TeachingId (professorId + courseId), professor (ManyToOne), course (ManyToOne) |

### DTOs (Response)

- `StudentDTO`: id, firstName, lastName, age, semester, credits
- `CourseDTO`: id, name, location
- `ProfessorDTO`: id, firstName, lastName, degree, employmentType
- `EnrollmentDTO`: studentId, courseId, studentName, courseName, price, enrolledAt
- `TeachingDTO`: professorId, professorName, courseId, courseName

## Business Rules

1. **Maximum Capacity**: A course can have at most 5 enrolled students. Rejects with HTTP 409 if full.
2. **Pricing**:
   - Standard: $100.00 per enrollment
   - Discount: $85.00 if the student already has 3 or more prior enrollments
3. **Duplicate Prevention**: A student cannot enroll in the same course twice. Returns HTTP 409 on duplicate attempt.

## Validation

- Request body fields validated with `@NotNull` and `@Positive` (studentId, courseId must be positive integers)
- Missing/invalid fields return HTTP 400 with field-level error details

## Error Responses

All errors follow a consistent JSON format:

```json
{
  "status": 409,
  "error": "Course is full",
  "message": "Course has reached maximum capacity of 5 students",
  "timestamp": "2026-04-28T10:15:30"
}
```

For validation errors, include `fieldErrors`:

```json
{
  "status": 400,
  "error": "Validation failed",
  "message": "Invalid request parameters",
  "timestamp": "2026-04-28T10:16:00",
  "fieldErrors": {
    "studentId": "must be greater than 0"
  }
}
```

## Data Migration

On first startup, the application automatically loads data from the legacy `data-migration/*.txt` files into the database. This is a one-time, idempotent operation that runs only if the database is empty.

The `.txt` format is plain CSV, no headers:

- `students.txt`: `id,firstName,lastName,age,semester,credits`
- `courses.txt`: `id,name,location`
- `professors.txt`: `id,firstName,lastName,degree,employmentType`
- `enrollments.txt`: `studentId,courseId`
- `teaching.txt`: `professorId,courseId`

**Note**: Legacy files are preserved for reference; the new system no longer reads them at runtime.

## Configuration

### application.yml (default)

```yaml
server:
  port: ${SERVER_PORT:8080}

spring:
  datasource:
    url: ${DB_URL:jdbc:h2:mem:enrollmentdb}
    username: ${DB_USERNAME:sa}
    password: ${DB_PASSWORD:}
  jpa:
    hibernate:
      ddl-auto: create-drop
```

Configuration can be overridden via environment variables (preferred) or direct edits to `.env`.

## Testing

Run the test suite:

```bash
./mvnw test
```

Tests include:
- Unit tests for `EnrollmentService` (business rules, price calculation, edge cases)
- Integration tests for controllers with `MockMvc`
- Repository tests (to be added as needed)

## Development Tips

- Enable SQL logging: set `JPA_SHOW_SQL=true` in application.yml
- Access H2 console (dev only): `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:enrollmentdb`)
- Use curl or Postman to test API endpoints
- Check logs: default console output; in prod logs written to `logs/enrollment-system.log`

## Future Enhancements

- Swagger/OpenAPI documentation (springdoc-openapi)
- Pagination & sorting on list endpoints
- Advanced filters (by semester, location, etc.)
- MapStruct for DTO mapping
- Security (Spring Security + JWT)
- Actuator health endpoints
- CI/CD pipeline

## License

This project is for educational purposes as part of an architecture course.
