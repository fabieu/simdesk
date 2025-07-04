# Desktop Client

This simple JavaFX-based application listens for UDP broadcast messages from
**Assetto Corsa Competizione** and forwards the raw packets to a configurable
HTTP endpoint. The payload is sent without any modification using the
`application/octet-stream` content type.

## Usage

Run the application via Gradle:

```bash
./gradlew :desktop-client:run
```

Specify the forwarding URL, UDP port, and optional password in the GUI and press **Start** to begin forwarding messages.
