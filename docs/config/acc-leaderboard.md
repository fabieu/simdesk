## Description

The leaderboard feature enables processing of ACC result files to create a leaderboard for lap records as well as
detailed
session information.

**View Demo
**: [Lap-Records](https://sim2real.simdesk.eu/leaderboard/lap-records), [Sessions](https://sim2real.simdesk.eu/leaderboard/sessions)

Enable the leaderboard feature by adding `acc-leaderboard` to `SPRING_PROFILES_ACTIVE`.

## Docker Bind Mount

The leaderboard feature requires a specific bind mount to read the local result files.  
When the application starts, all result files are scanned in the mounted folder **recursively**. Then a
file watcher takes over to monitor the folder for file creations or changes **recursively**. The application ensures
that only valid results files are being processed and that no duplicates are being created.  
The default path inside the docker container, which needs to be mounted, is `/app/results`.

> For more information about bind mounts, please refer to
> the [Docker documentation](https://docs.docker.com/storage/bind-mounts/).

**Example:**

```yaml
# compose.yaml (Docker Compose)
services:
  app:
    image: ghcr.io/fabieu/simdesk:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=acc-leaderboard
    volumes:
      - ./data:/app/data # Bind mount for data persistence
      - ./results:/app/results # Bind mount for the result files
```

## Environment variables

### `SIMDESK_ACC_RESULTS_SCAN_INTERVAL`

> optional, default=1m

Overrides the default scan interval for the results folder. The following methods can be used for defining the interval:

- A regular long representation using milliseconds
- The standard ISO-8601 format used by `java.time.Duration`
- A more readable format where the value and the unit are coupled (e.g. 10s means 10 seconds)

### `SIMDESK_ACC_RESULTS_EXCLUDE_PATTERN`

> optional, default=

Define a regex pattern to ignore certain server names, which can be useful if you want to exclude certain servers from
the leaderboard.

### `SIMDESK_ACC_RESULTS_FOLDERS`

> optional, default=/app/results

Overrides the default results folders.  
When using Docker it is required to adjust the volume bind mount to the new path.

## Local Development

The results folder can be overridden by setting the environment variable `SIMDESK_ACC_RESULTS_FOLDERS`.