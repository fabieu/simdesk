This application can be configured to use either a SQLite (default) or PostgreSQL database. The database type will be
detected via the `SIMDESK_DB_TYPE` environment variable. The available database types are`sqlite` and `postgres`. Take a
look at the following sections to see how to configure the application to use the desired database.

## SQLite

When using the SQLite database you only need to mount the `/app/data` directory inside the container to a persistent
storage to keep the data between application restarts. You can use a Docker volume or a bind mount to achieve this.

### Environment variables

`SIMDESK_DB_TYPE`

> optional, default=sqlite

Set the database type to `sqlite` to use the SQLite database. Since the default value is `sqlite`, you don't need to set
this variable.

### Examples

**Docker** 🐳

```bash 
docker run -d -p 8080:8080 -e SIMDESK_DB_TYPE=sqlite ghcr.io/fabieu/simdesk:latest
```

**Docker Compose** 🐳

```yaml
# compose.yml (Docker Compose)
services:
  app:
    image: ghcr.io/fabieu/simdesk:latest
    environment:
      SIMDESK_DB_TYPE: sqlite
    volumes:
      - simdesk-data:/app/data
    ports:
      - "8080:8080"
    restart: unless-stopped
```

## PostgreSQL

When using the PostgreSQL database you need to set the connection details via the following environment variables.
This application will create a new schema called `simdesk` in the specified database.
This allows you to use an existing database without any conflicts.

### Environment variables

`SIMDESK_DB_TYPE`

> required

Set the database type to `postgres` to use the PostgreSQL database.

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

### Examples

**Docker Compose** 🐳

```yaml
# compose.yml (Docker Compose)
services:
  app:
    image: ghcr.io/fabieu/simdesk:latest
    environment:
      SIMDESK_DB_URL: jdbc:postgresql://database:5432/simdesk
      SIMDESK_DB_USERNAME: postgres
      SIMDESK_DB_PASSWORD: development
    ports:
      - "8080:8080"
    restart: unless-stopped
  database:
    image: postgres:16
    shm_size: 128mb
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: development
      POSTGRES_DB: simdesk
    ports:
      - "5432:5432"
    volumes:
      - simdesk-db:/var/lib/postgresql/data
    restart: unless-stopped
```