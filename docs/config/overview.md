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

`SIMDESK_COMMUNITY_NAME`

> optional, default=SimDesk

Set the community name for the main page. If not set, a generic name will be used.

`SIMDESK_IMPRESSUM_URL`

> optional, default=

The URL to the impressum page. If not set, no impressum link will be shown.

`SIMDESK_PRIVACY_URL`

> optional, default=

The URL to the privacy page. If not set, no privacy link will be shown.