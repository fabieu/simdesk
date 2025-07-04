## Description

Enable the leaderboard feature by adding `discord` to `SPRING_PROFILES_ACTIVE`.

## Environment Variables

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

Discord Server ID used for OAuth2 authentication rules and role mappings.

## Discord Application

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

### Creating a Discord Application

1. Sign in to the [Discord Developer Portal](https://discord.com/developers/applications) and click **New Application** to create a project name.
2. Copy the **Application ID** and **Secret** from the **OAuth2** page. These values become `SIMDESK_DISCORD_CLIENT_ID` and `SIMDESK_DISCORD_CLIENT_SECRET`.
3. Under **Bot**, click **Add Bot** and copy the generated **Bot Token** for the `SIMDESK_DISCORD_BOT_TOKEN` variable. Enable **Server Members Intent** in this section.
4. On the **OAuth2 → Redirects** page, add the development URL `http://localhost:8080/login/oauth2/code/discord` and your production URL `https://{your-domain}/login/oauth2/code/discord`.
5. Generate an invite link via **OAuth2 → URL Generator** with the scopes `applications.commands` and `bot`. Use it to add the bot to your server.
6. To obtain your `SIMDESK_DISCORD_GUILD_ID`, enable *Developer Mode* in Discord (User Settings → Advanced), then right-click your server and choose **Copy Server ID**.
