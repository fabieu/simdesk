# Leaderboard

Enable the leaderboard feature by adding `acc-leaderboard` to `SPRING_PROFILES_ACTIVE`.

## Docker Volume

The leaderboard requires a Docker bind mount to read the local result files.  
The default path inside the docker container is `/app/results`, which can be overridden by setting
the `SIMDESK_ACC_RESULTS_FOLDERS` environment variable.

> For additional information about Volumes refer to
> the [Docker documentation](https://docs.docker.com/storage/volumes/).

### Examples

```bash
#!/bin/sh
docker run -d --restart=always -p 8080:8080 -e SPRING_PROFILES_ACTIVE=acc-leaderboard -v ./results:/app/results -v ./data:/app/data fabieu/simdesk:latest
```

```yaml
# compose.yaml
services:
  app:
    image: fabieu/simdesk:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=acc-leaderboard
    volumes:
      - ./data:/app/data
      - ./results:/app/results
    restart: unless-stopped
```

## Environment variables

### `SIMDESK_ACC_RESULTS_SCAN_INTERVAL`

> optional, default=1m

Overrides the default scan interval for the results. The following methods can be used for defining the interval:

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