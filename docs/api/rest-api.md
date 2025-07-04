# REST API

SimDesk exposes a small REST API which is documented via [OpenAPI](https://www.openapis.org/).
An automatically generated JSON specification is served from the running application
at `/openapi`. You can open `/swagger-ui` for an interactive view of the endpoints.

Below is a short overview of the available endpoints.

## Endpoints

### Sessions

| Method | Path | Description |
| ------ | ---- | ----------- |
| `GET` | `/api/v1/sessions` | Retrieve all sessions within a time range. Optional query parameters can include lap data or filter by insert time. |
| `GET` | `/api/v1/sessions/{fileChecksum}` | Retrieve a single session identified by its file checksum. Optional query parameter `withLaps` adds lap data. |

### Drivers

| Method | Path | Description |
| ------ | ---- | ----------- |
| `GET` | `/api/v1/drivers/{driverId}/sessions` | Get all sessions for a driver. Optional query parameters allow filtering by time range and including lap data. |

### Weather

| Method | Path | Description |
| ------ | ---- | ----------- |
| `GET` | `/api/v1/weather/current` | Returns the current weather prediction for the specified track. |
| `GET` | `/api/v1/weather/prediction` | Predicts weather settings for a given number of race hours. |

All endpoints require an authenticated user with the `ADMIN` role. Some requests
accept additional query parameters, see the OpenAPI specification for the
complete schema.
