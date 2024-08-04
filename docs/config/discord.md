# Discord

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