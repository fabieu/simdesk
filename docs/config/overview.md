Environment variables are used to configure the application. The following sections describe the available configuration
options.

- [Basic configuration](#basic-configuration)
- [Persistence](persistence.md)
- [Authentication](auth.md)

**Features:**

- [Leaderboard](acc-leaderboard.md)
- [Entrylist](acc-entrylist.md)
- [Balance of Performance](acc-bop.md)
- [Map](map.md)

## Basic configuration

`SPRING_PROFILES_ACTIVE`

> optional

To enable additional features, you have to add the corresponding feature profile to the `SPRING_PROFILES_ACTIVE`
environment variable. If following profiles are set, when you do not override `SPRING_PROFILES_ACTIVE`:

- `acc-entrylist,acc-bop`: Enable the Entrylist and Balance of Performance feature set.

`SIMDESK_COMMUNITY_NAME`

> optional, default=SimDesk

Set the community name for the main page. If not set, a generic name will be used.

`SIMDESK_IMPRESSUM_URL`

> optional

The URL to the impressum page. If not set, no impressum link will be shown.

`SIMDESK_PRIVACY_URL`

> optional

The URL to the privacy page. If not set, no privacy link will be shown.