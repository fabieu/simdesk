## Description

The `acc-entrylist` feature enables the Entrylist Editor to manipulate Assetto Corsa Competizione `entrylist.json` files
and validate them for syntax errors and inconsistencies.

**View Demo**: [Entrylist Editor](https://simdesk.eu/entrylist/editor)

Enable the leaderboard feature by adding `acc-entrylist` to `SPRING_PROFILES_ACTIVE`.

## Entrylist Editor

### Validation Rules

The following validation rules are available and can be toggled on or off:

- **Invalid race numbers:** Check if race numbers are valid, also check for duplicate race numbers.
- **Invalid SteamIDs:** Check if SteamIDs are present, also check for duplicate SteamIDs.
- **Invalid grid positions:** Check if grid positions are valid, also check for duplicate grid positions.
- **Invalid ballast values:** Check if ballast values are inside the allowed threshold.
- **Invalid restrictor values:** Check if restrictor values are inside the allowed threshold.
- **Missing driver names:** Check if firstName, lastName and shortName are set if `overrideDriverInfo` is set to `1` in
  the entrylist file.
- **Invalid driver categories:** Check if driver categories are valid if `overrideDriverInfo` is set to `1` in the
  entrylist file.