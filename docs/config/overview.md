# Overview

## Environment Variables

The configuration is done via environment variables. The following options are available:

`SPRING_PROFILES_ACTIVE` (optional)

Set active Spring profiles to activate/deactivate certain features. The following profiles are available:

- `acc-entrylist`: Enables the entrylist features
- `acc-bop`: Enables the BoP editor
- `acc-leaderboard`: Enables the server leaderboard features
- `discord`: Enables Discord OAuth2 login and Discord Bot integration

If this environment variable is not set, the following profiles are activated by default:

- `acc-entrylist`
- `acc-bop`

`SIMDESK_ACC_RESULTS_FOLDERS` (optional)

Overrides the default results folders. If not set, a single folder `/app/results` is configured inside the docker
container.
Normally it is sufficient to mount a volume to `/app/results`.

`SIMDESK_ACC_RESULTS_SCAN_INTERVAL` (optional)

Overrides the default scan interval for the results folder. If not set, the default interval is set to 1 minute.

`SIMDESK_ACC_RESULTS_IGNORE_PATTERNS` (optional)

Set regex pattern to ignore certain server names. If not set no server names will be ignored.

`SIMDESK_COMMUNITY_NAME` (optional)

Set the community name for the main page. If not set, a generic name will be used.

`SIMDESK_IMPRESSUM_URL` (optional)

The URL to the impressum page. If not set, no impressum link will be shown.

`SIMDESK_PRIVACY_URL` (optional)

The URL to the privacy page. If not set, no privacy link will be shown.

`SIMDESK_DISCORD_CLIENT_ID` (required for `discord` profile)

OAuth2 client id of the Discord Application.

`SIMDESK_DISCORD_CLIENT_SECRET` (required for `discord` profile)

OAuth2 client secret of the Discord Application.

`SIMDESK_DISCORD_BOT_TOKEN` (required for `discord` profile)

Bot Token of the Discord Application.

`SIMDESK_DISCORD_GUILD_ID` (required for `discord` profile)

Discord Server-ID used for OAuth2 authentication rules and role mappings.