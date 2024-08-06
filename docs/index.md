<p align="center">
    <img src="img/logo_h_200.png" alt="SimDesk Logo">
</p>
<p align="center">
    <em>Server leaderboard and tools for Assetto Corsa Competizione</em>
</p>

## Features

- Leaderboard for lap records
- Detailed session overview
- Balance of Performance overview with management interface
- Entrylist validator
- Balance of Performance Editor
- Discord integration

## Installation

### Using Docker

```bash
docker run -d -p 8080:8080 -v ./data:/app/data fabieu/simdesk:latest
```

### Using Docker Compose

```yaml
services:
  app:
    image: fabieu/simdesk:latest
    ports:
      - "8080:8080"
    volumes:
      - ./data:/app/data
    restart: unless-stopped
```

```bash
docker-compose up -d
```

## Changelog & Releases

This repository keeps a changelog using GitHub's releases functionality.

Releases are based on Semantic Versioning, and use the format of `MAJOR.MINOR.PATCH`. In short, the version will be
incremented based on the following:

- `MAJOR`: Incompatible or major changes.
- `MINOR`: Backwards-compatible new features and enhancements.
- `PATCH`: Backwards-compatible bugfixes and package updates.

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

## License

This project is licensed under the terms of the MIT license.

[project-url]: https://github.com/fabieu/simdesk

[issues-url]: https://github.com/fabieu/simdesk/issues

[forks-url]: https://github.com/fabieu/simdesk/forks
