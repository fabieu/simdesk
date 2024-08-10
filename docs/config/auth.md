## Username and Password

The username and password for the admin user can be set via environment variables. If no password is set, a random
password will be generated and printed to the console every time the application starts.

### Environment Variables

`SIMDESK_ADMIN_USERNAME`

> optional, default=admin

Username for the admin user.

`SIMDESK_ADMIN_PASSWORD`

> optional

Password for the admin user. If not set, a random password will be generated every time the application starts.