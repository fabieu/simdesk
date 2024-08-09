<p align="center">
    <img src="docs/img/logo_h_200.png" alt="SimDesk Logo">
</p>

<!--include-docs-start-->
<p align="center">
    <em>Server leaderboard and tools for Assetto Corsa Competizione</em>
</p>
<p align="center">
    <a href="https://simdesk.eu/"><b>Documentation</b></a>
</p>

## Features

- Leaderboard for lap records
- session overview
- Balance of Performance overview with management interface
- Entrylist validator
- Balance of Performance Editor
- Discord integration

## Quick Start

**Docker** 🐳

```bash 
docker run -d -p 8080:8080 fabieu/simdesk:latest
```

**Docker Compose** 🐳

```yaml
services:
  app:
    image: fabieu/simdesk:latest
    ports:
      - "8080:8080"
    restart: unless-stopped
```

```bash
docker-compose up -d
```

## Configuration

For additional configuration options, please refer to the **official documentation
**: [https://simdesk.eu](https://simdesk.eu).

## Support Level

> Active

SimDesk is being actively worked on, and we expect work to continue in the foreseeable future.  
Bug reports, feature requests, questions, and pull requests are welcome.

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any
contributions you make are **greatly appreciated**.

Please read [CONTRIBUTING.md](https://simdesk.eu/contributing/) for details on the process
for submitting pull requests to us, and [CREDITS.md](https://simdesk.eu/credits/) for a listing of maintainers of,
contributors to, and dependencies used by SimDesk.

## Changelog & Releases

This repository keeps a changelog using GitHub's releases functionality.

Releases are based on Semantic Versioning, and use the format of `MAJOR.MINOR.PATCH`. In short, the version will be
incremented based on the following:

- `MAJOR`: Incompatible or major changes.
- `MINOR`: Backwards-compatible new features and enhancements.
- `PATCH`: Backwards-compatible bugfixes and package updates.

## License

This project is licensed under the terms of the MIT license.

## Credits

For a full list of maintainers of, contributors to, and dependencies used by SimDesk, please refer
to [CREDITS.md](https://simdesk.eu/credits/)