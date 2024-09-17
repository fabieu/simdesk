## Persistence

The application uses an SQLite database file named `simdesk.db` to store data. If you want to persist the data across
container restarts, you have to mount a volume to
`/app/data` in the container.

**Example:**

```yaml
# compose.yml (Docker Compose)
services:
  app:
    image: ghcr.io/fabieu/simdesk:latest
    volumes:
      - ./data:/app/data # You can use bind mounts or volumes here
    ports:
      - "8080:8080"
```

## Authentication

See [Authentication](auth.md) for more information.

## Configuration Basics

Environment variables are used to configure the application. Feature based environment variables are described in the
corresponding feature sections.

- [Leaderboard](acc-leaderboard.md)
- [Entrylist](acc-entrylist.md)
- [Balance of Performance](acc-bop.md)

## Environment Variables

`SPRING_PROFILES_ACTIVE`

> optional

To enable additional features, you have to add the corresponding feature profile to the `SPRING_PROFILES_ACTIVE`
environment variable. If following profiles are set, when you do not override `SPRING_PROFILES_ACTIVE`:

- `acc-entrylist,acc-bop`: Enable the Entrylist and Balance of Performance feature set.

`SIMDESK_DB_URL`

> required

The JDBC URL for the PostgreSQL database. The general form of the connection URL is:
`jdbc:postgresql://[serverName[:portNumber]]/[databaseName][?property=value[;property=value]]`

- **jdbc:postgresql://** (Required) - Is known as the subprotocol and is constant.
- **serverName** (Required) - Is the address of the server to connect to. This address can be a DNS or IP address, or it
  can be localhost or 127.0.0.1 for the local computer.
- **portNumber** (Optional) - Is the port to connect to on serverName. The default is 5432. If you're using the default
  port, you don't have to specify the port, nor the preceding : in the URL.
- **databaseName** (Required) - Is the name of the database to connect to. The database name must be unique for each
  database server.
- **property** (Optional) - Is one or more option connection properties. Properties can only be delimited by using the
  semicolon (;), and they can't be duplicated.

`SIMDESK_DB_USERNAME`

> required

The username to connect to the PostgreSQL database.

`SIMDESK_DB_PASSWORD`

> required

The password to connect to the PostgreSQL database.

`SIMDESK_COMMUNITY_NAME`

> optional, default=SimDesk

Set the community name for the main page. If not set, a generic name will be used.

`SIMDESK_IMPRESSUM_URL`

> optional

The URL to the impressum page. If not set, no impressum link will be shown.

`SIMDESK_PRIVACY_URL`

> optional

The URL to the privacy page. If not set, no privacy link will be shown.