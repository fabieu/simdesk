Enable the leaderboard feature by adding `discord` to `SPRING_PROFILES_ACTIVE`.

## Environment variables

### `SIMDESK_DISCORD_CLIENT_ID`

> required

OAuth2 client id of the Discord Application.

### `SIMDESK_DISCORD_CLIENT_SECRET`

> required

OAuth2 client secret of the Discord Application.

### `SIMDESK_DISCORD_BOT_TOKEN`

> required

Bot Token of the Discord Application.

### `SIMDESK_DISCORD_GUILD_ID`

> required

## Discord Application

Discord Server-ID used for OAuth2 authentication rules and role mappings.

To enable Discord OAuth2 login and Discord Bot integration, the `discord` profile must be active.
You need to create a new Discord application in
the [Discord Developer Portal](https://discord.com/developers/applications) and setup the application with the following
settings:

**Installation**:

- Authorization Methods: ☑ Guild Install
- Default Install Settings - Scopes:`applications.commands`, `bot`

**OAuth2**:

- Redirects:
    - `http://localhost:8080/login/oauth2/code/discord` (for development)
    - `https://{your-domain}/login/oauth2/code/discord`

**Bot**:

- Privileged Gateway Intents: ☑ Server Members Intent