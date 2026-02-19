# SimDesk – Copilot Coding Agent Instructions

## Project Summary

SimDesk is a modern server leaderboard and management platform for sim racing organizers, with features including lap-record leaderboards, session overviews, Balance of Performance (BoP) management, entrylist editing, a real-time weather map, and Discord OAuth2/bot integration. It targets Assetto Corsa Competizione (ACC) game servers.

## Technology Stack

- **Language:** Java 21 (Adoptium/Temurin JDK)
- **Framework:** Spring Boot 4.x, Vaadin 25 (UI framework)
- **Build tool:** Gradle (wrapper at `./gradlew`), multi-project build
- **Database:** SQLite (default) or PostgreSQL; migrations managed by Flyway
- **ORM/Query:** MyBatis
- **Frontend bundler:** Vite (invoked via Vaadin Gradle plugin)
- **Containerisation:** Docker, image based on `eclipse-temurin:21-alpine`
- **Testing:** JUnit 5, Testcontainers (PostgreSQL)

## Repository Layout

```
simdesk/                        ← repo root
├── settings.gradle             ← Gradle multi-project root (includes simdesk-web)
├── gradlew / gradlew.bat       ← Gradle wrapper scripts
├── simdesk-web/                ← Main application module
│   ├── build.gradle            ← Module build file (dependencies, plugins)
│   ├── gradle.properties       ← Contains version=x.y.z
│   ├── Dockerfile              ← Production container image
│   ├── vite.config.ts          ← Vite/Vaadin frontend config
│   ├── development/
│   │   └── compose.yml         ← Local PostgreSQL via Docker Compose
│   └── src/
│       ├── main/
│       │   ├── java/de/sustineo/simdesk/
│       │   │   ├── configuration/   ← Spring configuration classes
│       │   │   ├── controller/      ← REST API controllers
│       │   │   ├── entities/        ← Domain entities (database, JSON, enums)
│       │   │   ├── filter/          ← Servlet filters
│       │   │   ├── layouts/         ← Vaadin layout components
│       │   │   ├── mybatis/         ← MyBatis mappers and type handlers
│       │   │   ├── services/        ← Business logic
│       │   │   ├── utils/           ← Utility classes
│       │   │   └── views/           ← Vaadin UI views
│       │   └── resources/
│       │       ├── application.yaml          ← Main Spring config (profiles: development, discord)
│       │       ├── db/migration/{sqlite,postgres}/ ← Flyway migrations
│       │       └── schema/entrylist.json     ← JSON schema for entrylist validation
│       └── test/
│           └── java/de/sustineo/simdesk/
│               ├── PostgresTestSuite.java    ← Base class; spins up Testcontainers PostgreSQL
│               ├── configuration/            ← Test-specific Spring config
│               └── services/                 ← Service-layer tests
├── .github/
│   ├── workflows/
│   │   ├── test.yml             ← CI: runs tests on every PR to main
│   │   └── build-web-app.yml   ← CI: build jar + Docker image + release on push to main
│   └── dependabot.yml
└── docs/                        ← MkDocs documentation source
```

## Build & Validation Commands

Always run commands from the **repository root** unless otherwise specified.

### Prerequisites

- Java 21 (Temurin) must be available. The Gradle toolchain will attempt to auto-provision it.
- Docker is required to run tests (Testcontainers pulls `postgres:16-alpine`).

### Run tests

```bash
chmod +x gradlew
./gradlew :simdesk-web:test
```

Tests use Testcontainers and require a working Docker daemon. The `PostgresTestSuite` base class starts a PostgreSQL container automatically; no manual database setup is needed.

### Build the application JAR

```bash
chmod +x gradlew
./gradlew :simdesk-web:vaadinBuildFrontend :simdesk-web:bootJar
```

The production JAR is written to `simdesk-web/build/libs/simdesk-web.jar`.  
**Always run `vaadinBuildFrontend` before `bootJar`** to include the compiled frontend bundle.

### Build the Docker image (requires the JAR)

```bash
# From simdesk-web/ directory:
docker build -t simdesk:local .
```

### Local development with PostgreSQL

```bash
cd simdesk-web/development
docker compose up -d   # starts postgres:16-alpine on port 5432
```

Then run the app with the `development` Spring profile and set:

```
SIMDESK_DB_TYPE=postgresql
SIMDESK_DB_URL=jdbc:postgresql://localhost:5432/simdesk
SIMDESK_DB_USERNAME=postgres
SIMDESK_DB_PASSWORD=development
```

## CI Workflows

| Workflow | Trigger | Key steps |
|---|---|---|
| `test.yml` | PR opened/synchronized/reopened targeting `main` | `./gradlew test` (5-min timeout) |
| `build-web-app.yml` | Push to `main` (when `gradle.properties` changes) or manual dispatch | tests → build jar → build Docker image → create GitHub release |

PRs must pass the `test.yml` workflow before merging.

## Key Configuration Notes

- **Spring profiles** – `acc-entrylist` and `acc-bop` are active by default. `discord` profile enables Discord OAuth2. `development` profile enables debug logging and disables Vaadin production mode.
- **Database vendor** – Controlled by `SIMDESK_DB_TYPE` env var (`sqlite` default, `postgresql` supported). Flyway migrations are in `src/main/resources/db/migration/{sqlite|postgres}/`.
- **Version** – Stored in `simdesk-web/gradle.properties` as `version=x.y.z`. The release pipeline reads this file.
- **Lombok** – Used for boilerplate reduction (`@Data`, `@Builder`, etc.). Annotation processing is configured in `build.gradle`.

## Testing Notes

- Tests are JUnit 5 and use `@SpringBootTest` with Testcontainers for database-dependent tests.
- Extend `PostgresTestSuite` for tests that require a live database.
- No separate linting step exists; the Gradle `compileJava` task runs with `options.deprecation = true` and will surface deprecation warnings as part of the build.
