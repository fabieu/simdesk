# Overview

Environment variables are used to configure the application.
Feature based environment variables are described in the corresponding feature documentation.

## Environment variables

### `SPRING_PROFILES_ACTIVE`

> optional

Features can be enabled by setting the corresponding Spring profiles.  
When you want to enable additional features, you have to add the corresponding profile to the `SPRING_PROFILES_ACTIVE`
variable.   
The following profiles are set by default:

- `acc-entrylist, acc-bop`: Enable the entrylist validator and BoP editor.

### `SIMDESK_COMMUNITY_NAME`

> optional, default=SimDesk

Set the community name for the main page. If not set, a generic name will be used.

### `SIMDESK_IMPRESSUM_URL`

> optional, default=

The URL to the impressum page. If not set, no impressum link will be shown.

### `SIMDESK_PRIVACY_URL`

> optional, default=

The URL to the privacy page. If not set, no privacy link will be shown.