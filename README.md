<a id="readme-top"></a>

<!-- OVERVIEW -->
<br />
<div style="text-align: center">
  <img src="/src/main/resources/META-INF/resources/assets/img/logo_full_white.png" alt="Logo" height="196">
  <p style="text-align: center">
    Simple and customizable server dashboard and utilities for Assetto Corsa Competizione
    <br />
    <a href="https://sim2real.simdesk.eu">View Demo</a>
    ·
    <a href="https://github.com/fabieu/simdesk/issues">Report Bug</a>
    ·
    <a href="https://github.com/fabieu/simdesk/issues">Request Feature</a>
  </p>
</div>

<!-- FEATURES -->

# Features

- ✅ Lap time leaderboard
- ✅ Entrylist validator
- ✅ BoP Editor
- ✅ Session overview with detailed insights
- ✅ Driver overview
- ✅ Various conversion utilities
- ✅ Discord authentication
- ✅ Discord bot implementation
- ✅ Responsive design
- ✅ Light/Dark mode

<p style="text-align: end">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->

# Quickstart

> For all available options take a look at the [Configuration](#configuration) section.

🐳 Docker

```bash
docker run -d --restart=always -p 8085:8080 --name simdesk -e SPRING_PROFILES_ACTIVE="acc-entrylist,acc-bop,acc-leaderboard" -v ./results:/app/results -v ./data:/app/data ghcr.io/fabieu/simdesk:latest
```

🐳 Docker Compose

```yaml
version: "3.9"
services:
  app:
    image: ghcr.io/fabieu/simdesk:latest
    ports:
      - "8080:8080"
    volumes:
      - ./data:/app/data
      - ./results:/app/results
    environment:
      SPRING_PROFILES_ACTIVE: acc-entrylist, acc-bop, acc-leaderboard
    restart: unless-stopped
```

```bash
docker-compose up -d  
```

<p style="text-align: end">(<a href="#readme-top">back to top</a>)</p>

<!-- CONFIGURATION -->

# Configuration

## Environment Variables

The configuration is done via environment variables. The following options are available:

### `SPRING_PROFILES_ACTIVE` (optional)

Set active Spring profiles to activate/deactivate certain features. The following profiles are available:

- `acc-entrylist`: Enables the entrylist features
- `acc-bop`: Enables the BoP editor
- `acc-leaderboard`: Enables the server leaderboard features
- `discord`: Enables Discord OAuth2 login and Discord Bot integration

If this environment variable is not set, the following profiles are activated by default:

- `acc-entrylist`
- `acc-bop`

### `SIMDESK_ACC_RESULTS_FOLDERS` (optional)

Overrides the default results folders. If not set, a single folder `/app/results` is configured inside the docker
container.
Normally it is sufficient to mount a volume to `/app/results`.

### `SIMDESK_ACC_RESULTS_SCAN_INTERVAL` (optional)

Overrides the default scan interval for the results folder. If not set, the default interval is set to 1 minute.

### `SIMDESK_ACC_RESULTS_IGNORE_PATTERNS` (optional)

Set regex pattern to ignore certain server names. If not set no server names will be ignored.

### `SIMDESK_COMMUNITY_NAME` (optional)

Set the community name for the main page. If not set, a generic name will be used.

### `SIMDESK_IMPRESSUM_URL` (optional)

The URL to the impressum page. If not set, no impressum link will be shown.

### `SIMDESK_PRIVACY_URL` (optional)

The URL to the privacy page. If not set, no privacy link will be shown.

### `SIMDESK_DISCORD_CLIENT_ID` (required for `discord` profile)

OAuth2 client id of the Discord Application.

### `SIMDESK_DISCORD_CLIENT_SECRET` (required for `discord` profile)

OAuth2 client secret of the Discord Application.

### `SIMDESK_DISCORD_BOT_TOKEN` (required for `discord` profile)

Bot Token of the Discord Application.

### `SIMDESK_DISCORD_GUILD_ID` (required for `discord` profile)

Discord Server-ID used for OAuth2 authentication rules and role mappings.

<p style="text-align: end">(<a href="#readme-top">back to top</a>)</p>

## Discord Integration

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

<!-- BUILT WITH -->

# Built With

[![Spring Boot][spring-boot]][spring-boot-url]
[![Vaaadin][vaadin]][vaadin-url]

<p style="text-align: end">(<a href="#readme-top">back to top</a>)</p>


<!-- RELEASES -->

# Changelog & Releases

This repository keeps a changelog using GitHub's releases functionality.

Releases are based on Semantic Versioning, and use the format of `MAJOR.MINOR.PATCH`. In short, the version will be
incremented based on the following:

- `MAJOR`: Incompatible or major changes.
- `MINOR`: Backwards-compatible new features and enhancements.
- `PATCH`: Backwards-compatible bugfixes and package updates.

<p style="text-align: end">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->

# Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any
contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also
simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p style="text-align: end">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE -->

# License

Distributed under the Apache License 2.0. See [LICENSE][license-url] for more information.

<p style="text-align: end">(<a href="#readme-top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->

[project-url]: https://github.com/fabieu/simdesk

[issues-url]: https://github.com/fabieu/simdesk/issues

[forks-url]: https://github.com/fabieu/simdesk/forks

[license-url]: https://gitlab.com/sustineo/simdesk/-/blob/main/LICENSE

[spring-boot]: https://img.shields.io/badge/spring%20boot-6DB33F?logo=springboot&logoColor=white&style=for-the-badge

[spring-boot-url]: https://spring.io/projects/spring-boot

[vaadin]: https://img.shields.io/badge/vaadin-00B4F0?logo=vaadin&logoColor=white&style=for-the-badge

[vaadin-url]: https://vaadin.com/