# PADO Java EventFlow

A small Spring Boot REST backend for the same event-management domain as [PADO EventFlow](https://github.com/PADOALEX/PADO-EventFlow). It connects explicit HTTP DTOs to PostgreSQL through a service and Spring Data JPA, applies a Flyway migration, and supports the existing React client's event operations. The original .NET backend is unchanged.

## Stack and prerequisites

JDK 21, Spring Boot 3.5.16, Maven 3.9+, Spring Web, Bean Validation, Spring Data JPA, PostgreSQL and Flyway. JUnit 5 tests use both isolated logic checks and a real HTTP server/PostgreSQL integration profile. Tested locally with Temurin 21.0.13, Maven 3.10.0 and PostgreSQL 17.11 on Windows.

Java 21 is an LTS learning baseline. Spring Boot 3.5.16 is a verified stable 3.x release compatible with Java 21, selected to keep familiar Spring 6/JPA APIs rather than add a major-version migration to this exercise. It is not claimed to be the newest Spring Boot major. Its parent POM was successfully resolved from Maven Central. See [official requirements](https://docs.spring.io/spring-boot/3.5/system-requirements.html). Maven wrapper is omitted in favor of the explicit prerequisites; verify `java -version` and `mvn -version` first.

## Run with an existing PostgreSQL database

Create an empty local database named `eventflow` and a role that owns it. Keep this Java database separate from the original .NET schema. In PowerShell:

```powershell
$env:DB_URL = 'jdbc:postgresql://localhost:5432/eventflow'
$env:DB_USER = 'eventflow'
$env:DB_PASSWORD = '<your-local-database-password>'
mvn --batch-mode --no-transfer-progress package
java -jar target/java-eventflow-1.0.0.jar
```

The API binds to `127.0.0.1:5080`. Flyway applies the migration; Hibernate validates the schema rather than creating it. Missing credentials or an inaccessible database fail startup. `/health` is process liveness, not a continuing database-readiness check. A plain `mvn test` runs only six unit tests and needs no database.

`.env` is consumed by Docker Compose, not automatically by Java or PowerShell. Export `DB_*` explicitly for native execution. Do not commit `.env` or production connection strings.

## Optional Docker Compose

```powershell
Copy-Item .env.example .env
# Set your own POSTGRES_PASSWORD in .env.
docker compose up --build -d
```

API and database host ports bind only to loopback. The PostgreSQL named volume preserves data; `docker compose down` stops the stack. Deleting its volume destroys demo events. Native execution was verified; Docker build/Compose execution is **NOT RUN** in the current environment because Docker is unavailable. Container tags are mutable, so pin image digests for reproducible deployment.

## HTTP API

| Method | Route | Result |
| --- | --- | --- |
| GET | `/health` | 200 process liveness |
| GET | `/api/events` | 200 `{items,totalCount,page,pageSize,totalPages}` |
| GET | `/api/events/stats` | 200 global `{total,planned,completed}` |
| GET | `/api/events/{uuid}` | 200 event or 404 |
| POST | `/api/events` | 201 event plus Location |
| PUT | `/api/events/{uuid}` | 200 replacement or 404 |
| PATCH | `/api/events/{uuid}/complete` | 200 completed event or 404 |
| DELETE | `/api/events/{uuid}` | 204 or 404 |

List parameters: `search` (up to 120 characters), `status=Planned|Completed`, `sort=date|name`, `direction=asc|desc`, `page=1`, `pageSize=10` (1-100). UUID tie-breakers stabilize equal keys. Search is case-insensitive and treats `%`, `_` and backslash literally. Leading-wildcard search is suitable for small demos; ordinary B-tree indexes do not make substring searching fast.

```json
{"name":"Java Meetup","description":"Collections and REST","date":"2026-12-05","startTime":"18:30:00","location":"Lviv","status":"Planned"}
```

Date and optional local time have no timezone; audit timestamps are UTC instants. Blank required fields, excessive lengths and invalid query/JSON values return structured 400 responses. Missing events return 404; violated database constraints return 409. Stack traces and raw SQL details are not returned in error JSON.

## React compatibility

The original Vite configuration proxies `/api` to `http://localhost:5080`. Stop the .NET demo on that port before running this Java service, then run `npm ci` and `npm run dev` in the original React `frontend` directory. Direct cross-origin requests are not enabled; the development proxy keeps requests same-origin. The React UI's existing .NET labels remain cosmetic and should be updated only in a deliberate frontend fork.

[Contract comparison](docs/CONTRACT_COMPATIBILITY.md) records intentional edge differences. This is API compatibility, not a shared physical database schema or a replacement of the C# repository.

## Tests

```powershell
mvn test
# Create a separate disposable loopback database named exactly java_eventflow_test.
$env:EVENTFLOW_TEST_DB_URL = 'jdbc:postgresql://localhost:5432/java_eventflow_test'
$env:EVENTFLOW_TEST_DB_USER = 'eventflow'
$env:EVENTFLOW_TEST_DB_PASSWORD = '<test-only-database-password>'
mvn --batch-mode --no-transfer-progress -Pintegration verify
```

The integration profile deletes events **only in that named test database**, performs real HTTP CRUD/search/filter/pagination/validation checks, and tests a direct-write constraint. It fails explicitly without the required configuration; it never substitutes H2 or silently skips tests. CI provisions PostgreSQL 17 for the same profile. Prepared CI is not a completed remote run: [actual verification](docs/VERIFICATION.md).

## Architecture and limits

`React → HTTP → EventController → EventService → EventRepository → PostgreSQL`

The controller validates and maps HTTP. The service defines transaction boundaries; managed entity changes flush when a successful transaction commits. The repository performs JPA queries. Flyway owns schema changes and Hibernate verifies mappings. `ApiExceptionHandler` maps expected failures into problem details.

This is an unauthenticated, single-workspace educational demo. Every caller can change every event. It has no multi-user isolation, rate limiter, production observability, immutable history or distributed locking. Updates use last-write-wins; there is no optimistic revision token. Do not expose it publicly without designing access control and operational safeguards. GET statistics use a single aggregate query; page content/count can reflect concurrent changes under normal read-committed behavior.

Prepared with Codex assistance for learning. Running generated code does not prove the owner's independent Spring proficiency. Read [the .NET comparison](docs/ARCHITECTURE_FOR_DOTNET_DEVELOPER.md), [HTTP examples](docs/REST_API_EXAMPLES.md), [code explanations](docs/EXPLAIN_THE_CODE.md) and [interview questions](docs/JAVA_INTERVIEW_PREPARATION.md). Source is MIT-licensed; [dependency notices](docs/DEPENDENCIES_AND_LICENSES.md) apply separately.
