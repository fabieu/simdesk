<a id="readme-top"></a>

<!-- SHIELDS -->
[![Stargazers][stars-shield]][project-url]
[![Issues][issues-shield]][issues-url]
[![Contributors][contributors-shield]][project-url]
[![Forks][forks-shield]][forks-url]
[![MIT License][license-shield]][license-url]



<!-- OVERVIEW -->
<br />
<div style="text-align: center">
    <img src="docs/icon.png" alt="Logo" width="128" height="128">
    <h3 style="text-align: center">ACC Server Tools</h3>

  <p style="text-align: center">
    Simple and customizable server dashboard for Assetto Corsa Competizione
    <br />
    <a href="https://acc.sim2real.eu">View Demo</a>
    ·
    <a href="https://gitlab.com/sim2real-eu/acc-server-tools/issues">Report Bug</a>
    ·
    <a href="https://gitlab.com/sim2real-eu/acc-server-tools/issues">Request Feature</a>
  </p>
</div>



<!-- TABLE OF CONTENTS -->
[[_TOC_]]


<!-- FEATURES -->

## Features

- ✅ Lap time leaderboard
- ✅ Session overview with detailed insights
- ✅ Driver overview
- ✅ Customizable entrylist validator
- ✅ Various conversion utilities
- ✅ Responsive design
- ✅ Light/Dark mode
- ✅ REST API for custom frontends

<p style="text-align: end">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->

## Getting started

> For all available options take a look at the [Configuration](#configuration) section.

🐳 Docker

```bash
docker run -d --restart=always -p 8085:8080 --name acc-server-tools -e SPRING_PROFILES_ACTIVE="acc-leaderboard,acc-entrylist,acc-raceapp" -v ./results:/opt/acc-server-tools/results registry.gitlab.com/sim2real-eu/acc-server-tools:1
```

🐳 Docker Compose

```yaml
version: "3.9"
services:
  app:
    image: registry.gitlab.com/sim2real-eu/acc-server-tools:1
    ports:
      - "8085:8080"
    volumes:
      - ./results:/opt/acc-server-tools/results
    environment:
      SPRING_PROFILES_ACTIVE: acc-leaderboard, acc-entrylist, acc-raceapp
    restart: always
```

```bash
docker-compose up -d
```

<p style="text-align: end">(<a href="#readme-top">back to top</a>)</p>

<!-- CONFIGURATION -->

## Configuration

The configuration is done via environment variables. The following options are available:

### `SPRING_PROFILES_ACTIVE` (required)

Set active Spring profiles to activate/deactivate certain features. The following profiles are available:

- `acc-leaderboard`: Enables the server leaderboard features
- `acc-entrylist`: Enables the entrylist features
- `acc-raceapp`: Enables the RaceApp integration and API endpoints

### `LEADERBOARD_RESULTS_FOLDER` (optional)

Overrides the default results folder. If not set, the default folder is set to `/opt/acc-server-tools/results`.

### `LEADERBOARD_RESULTS_SCAN_INTERVAL` (optional)

Overrides the default scan interval for the results folder. If not set, the default interval is set to 1 minute.

### `LEADERBOARD_RESULTS_IGNORE_PATTERNS` (optional)

Set regex pattern to ignore certain server names. If not set no server names will be ignored.

### `LEADERBOARD_COMMUNITY_NAME` (optional)

Set the community name for the main page. If not set, a generic name will be used.

### `LEADERBOARD_IMPRESSUM_URL` (optional)

The URL to the impressum page. If not set, the impressum link will be hidden.

### `LEADERBOARD_PRIVACY_URL` (optional)

The URL to the privacy page. If not set, the privacy link will be hidden.

<p style="text-align: end">(<a href="#readme-top">back to top</a>)</p>


<!-- BUILT WITH -->

## Built With

* [![Spring Boot][spring-boot]][spring-boot-url]
* [![Vaaadin][vaadin]][vaadin-url]

<p style="text-align: end">(<a href="#readme-top">back to top</a>)</p>


<!-- RELEASES -->

## Changelog & Releases

This repository keeps a changelog using GitLab's releases functionality.

Releases are based on Semantic Versioning, and use the format of `MAJOR.MINOR.PATCH`. In short, the version will be
incremented based on the following:

- `MAJOR`: Incompatible or major changes.
- `MINOR`: Backwards-compatible new features and enhancements.
- `PATCH`: Backwards-compatible bugfixes and package updates.

<p style="text-align: end">(<a href="#readme-top">back to top</a>)</p>



<!-- CONTRIBUTING -->

## Contributing

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


<!-- ACKNOWLEDGMENTS -->

## Acknowledgments

* [Choose an Open Source License](https://choosealicense.com)
* [shields.io](https://shields.io)

<p style="text-align: end">(<a href="#readme-top">back to top</a>)</p>


<!-- LICENSE -->

## License

Distributed under the Apache License 2.0. See [LICENSE][license-url] for more information.

### Third Party Licenses

- The favicon was generated using the following font:
    - Font Title: Luxurious Script
    - Font Author: Copyright 2015-2021 The Luxurious Project Authors (https://github.com/googlefonts/luxurious)
    - Font Source: http://fonts.gstatic.com/s/luxuriousscript/v7/ahcCv9e7yydulT32KZ0rBIoD7DzMg0rOby1JtYk.ttf
    - Font License: SIL Open Font License, 1.1 (http://scripts.sil.org/OFL))

<p style="text-align: end">(<a href="#readme-top">back to top</a>)</p>



<!-- MARKDOWN LINKS & IMAGES -->

[project-url]: https://gitlab.com/sim2real-eu/acc-server-tools

[stars-shield]: https://img.shields.io/gitlab/stars/sim2real-eu%2Facc-server-tools?style=for-the-badge

[issues-shield]: https://img.shields.io/gitlab/issues/open/sim2real-eu%2Facc-server-tools?style=for-the-badge

[issues-url]: https://gitlab.com/sim2real-eu/acc-server-tools/-/issues

[contributors-shield]: https://img.shields.io/gitlab/contributors/sim2real-eu%2Facc-server-tools?style=for-the-badge

[forks-shield]: https://img.shields.io/gitlab/forks/sim2real-eu%2Facc-server-tools?style=for-the-badge

[forks-url]: https://gitlab.com/sim2real-eu/acc-server-tools/-/forks

[license-shield]: https://img.shields.io/gitlab/license/sim2real-eu%2Facc-server-tools?style=for-the-badge

[license-url]: https://gitlab.com/sim2real-eu/acc-server-tools/-/blob/main/LICENSE

[spring-boot]: https://img.shields.io/badge/spring%20boot-6DB33F?logo=springboot&logoColor=white&style=for-the-badge

[spring-boot-url]: https://vaadin.com/

[vaadin]: https://img.shields.io/badge/vaadin-00B4F0?logo=vaadin&logoColor=white&style=for-the-badge

[vaadin-url]: https://vaadin.com/